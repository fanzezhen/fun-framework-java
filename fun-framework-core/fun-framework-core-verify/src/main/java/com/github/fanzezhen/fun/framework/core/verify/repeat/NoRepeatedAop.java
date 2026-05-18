package com.github.fanzezhen.fun.framework.core.verify.repeat;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;

/**
 * 防重复提交切面.
 * <p>
 * 配合 @NoRepeat 注解使用，通过缓存实现幂等性校验，防止短时间内重复提交.
 * 使用 CacheService.setIfAbsent 实现分布式场景下的原子性检查.
 * <p>
 * <b>执行时机：</b>方法执行前（@Before），校验失败时抛出ServiceException
 * <p>
 * <b>使用场景：</b>表单提交、支付接口、积分扣减等需要防重复的操作
 * <p>
 * <b>依赖条件：</b>容器中必须存在CacheService实现（如Redis缓存）
 */
@Slf4j
@Aspect
@Component
@ConditionalOnBean(CacheService.class)
public class NoRepeatedAop {
    /**
     * 应用名
     */
    @Value("${spring.application.name:}")
    private String springApplicationName;
    /**
     * 环境隔离变量
     */
    @Value("${spring.profiles.active:}")
    private String env;
    @Resource
    private CacheService cacheService;

    /**
     * 切点定义.
     * <p>
     * 匹配所有标注了 @NoRepeat 注解的方法.
     */
    @Pointcut("@annotation(com.github.fanzezhen.fun.framework.core.verify.repeat.NoRepeat)")
    public void cut() {
    }

    /**
     * 前置通知.
     * <p>
     * 在目标方法执行前进行重复提交校验，校验失败抛出 ServiceException.
     *
     * @param joinPoint 连接点信息
     */
    @Before("cut()")
    public void doBefore(final JoinPoint joinPoint) {
        String key;
        NoRepeat noRepeat;
        try {
            noRepeat = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NoRepeat.class);
            if (noRepeat == null) {
                return;
            }
            key = getKey(joinPoint, noRepeat);
            Duration timeout = Duration.of(noRepeat.timeout(), noRepeat.timeUnit().toChronoUnit());
            Boolean absent = cacheService.setIfAbsent(key, DateUtil.now(), timeout);
            if (!Boolean.TRUE.equals(absent)) {
                throw new ServiceException("请勿重复提交");
            }
        } catch (Exception exception) {
            log.error("noRepeated check failed", exception);
        }
    }

    /**
     * 生成缓存键.
     * <p>
     * 键的组成：环境/应用名/NoRepeat/类名.方法名/参数键/请求头JSON/自定义参数JSON.
     *
     * @param joinPoint 连接点信息
     * @param noRepeat  注解实例
     * @return 缓存键
     */
    private String getKey(final JoinPoint joinPoint, final NoRepeat noRepeat) {
        Object[] args = joinPoint.getArgs();
        JSONObject param = new JSONObject();
        String[] headerArgs = noRepeat.headerArgs();
        loadHeaderArgs(param, headerArgs);
        String[] paramArgs = noRepeat.paramArgs();
        loadParamArgs(param, paramArgs, args);
        String headerJsonStr = ContextHolder.getHeaderJsonStr(noRepeat.headerArgs());
        String paramKey = noRepeat.key();
        if (CharSequenceUtil.isEmpty(paramKey)) {
            paramKey = JSON.toJSONString(Arrays.stream(args)
                    .filter(arg -> !(arg instanceof HttpServletRequest))
                    .toList());
        }
        String key = env + StrPool.SLASH + springApplicationName + StrPool.SLASH +
                "NoRepeat" + StrPool.SLASH +
                joinPoint.getTarget().getClass().getName() + StrPool.DOT +
                joinPoint.getSignature().getName() + StrPool.SLASH + paramKey + StrPool.SLASH +
                headerJsonStr + StrPool.SLASH + param.toJSONString();
        log.info("key={}", key);
        return key;
    }

    /**
     * 加载方法参数到 JSON 对象.
     *
     * @param param     目标JSON对象
     * @param paramArgs 参数字段路径数组
     * @param args      方法实参数组
     */
    private static void loadParamArgs(final JSONObject param, final String[] paramArgs, final Object[] args) {
        if (ArrayUtil.isNotEmpty(paramArgs) && ArrayUtil.isNotEmpty(args)) {
            for (String validArg : paramArgs) {
                loadParamArgs(param, validArg, args);
            }
        }
    }

    /**
     * 加载单个参数字段到 JSON 对象.
     * <p>
     * 支持通过点号访问嵌套字段，如 "0.user.id" 表示第一个参数的 user 字段的 id 属性.
     *
     * @param param    目标JSON对象
     * @param validArg 参数字段路径（如 "0.id"）
     * @param args     方法实参数组
     */
    private static void loadParamArgs(final JSONObject param, final String validArg, final Object[] args) {
        if (CharSequenceUtil.isBlank(validArg)) {
            return;
        }
        String[] fieldParts = validArg.split("\\.");
        if (ArrayUtil.isEmpty(fieldParts)) {
            return;
        }
        if (NumberUtil.isInteger(fieldParts[0])) {
            int i = Integer.parseInt(fieldParts[0]);
            if (i < args.length) {
                Object o = args[i];
                if (fieldParts.length > 1) {
                    for (int j = 1; j < fieldParts.length; j++) {
                        o = ReflectUtil.getFieldValue(o, fieldParts[j]);
                    }
                }
                param.put(validArg, o);
            }
        }
    }

    /**
     * 加载请求头参数到 JSON 对象.
     *
     * @param param      目标JSON对象
     * @param headerArgs 请求头键名数组
     */
    private static void loadHeaderArgs(final JSONObject param, final String[] headerArgs) {
        if (ArrayUtil.isNotEmpty(headerArgs)) {
            for (String headerKey : headerArgs) {
                if (CharSequenceUtil.isBlank(headerKey)) {
                    continue;
                }
                param.put(headerKey, ContextHolder.get(headerKey));
            }
        }
    }

}
