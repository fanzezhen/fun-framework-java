package com.github.fanzezhen.fun.framework.core.verify.concurrent;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.cache.service.LockService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Arrays;

/**
 * 防并发切面
 * <p>
 * 配合 @NoConcurrent 注解使用，通过分布式锁防止相同请求并发执行。
 * 锁的Key由应用名、类名、方法名、参数和请求头组合而成，支持环境隔离。
 * <p>
 * <b>使用场景：</b>防止用户重复点击、防止同一参数的请求并发处理
 * <p>
 * <b>依赖条件：</b>容器中必须存在LockService实现（如Redis分布式锁）
 *
 */
@Slf4j
@Aspect
@Component
@ConditionalOnBean(LockService.class)
public class NoConcurrentAop {
    @Value("${spring.application.name:}")
    private String springApplicationName;
    /**
     * 环境隔离变量
     */
    @Value("${spring.profiles.active:}")
    private String env;
    @Resource
    private LockService lockService;

    @Pointcut("@annotation(com.github.fanzezhen.fun.framework.core.verify.concurrent.NoConcurrent)")
    public void cut() {
    }

    @SneakyThrows
    @Around("cut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        if (lockService == null) {
            return joinPoint.proceed();
        }
        NoConcurrent noConcurrent = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NoConcurrent.class);
        if (noConcurrent == null) {
            return joinPoint.proceed();
        }
        String key = getKey(joinPoint, noConcurrent);
        return lockService.lockAndExecute(joinPoint::proceed, key);
    }

    private String getKey(JoinPoint joinPoint, NoConcurrent noConcurrent) {
        Object[] args = joinPoint.getArgs();
        String paramKey = noConcurrent.key();
        if (CharSequenceUtil.isEmpty(paramKey)) {
            paramKey = JSON.toJSONString(Arrays.stream(args).filter(arg -> !(arg instanceof HttpServletRequest)).toList());
        }
        String key = env + StrPool.SLASH +
            springApplicationName + StrPool.SLASH +
            "NoConcurrent" + StrPool.SLASH +
            joinPoint.getTarget().getClass().getName() + StrPool.DOT + joinPoint.getSignature().getName() + StrPool.SLASH +
            paramKey + StrPool.SLASH + ContextHolder.getHeaderJsonStr(noConcurrent.headerArgs());
        log.info("key={}", key);
        return key;
    }

}
