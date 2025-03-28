package com.github.fanzezhen.fun.framework.core.verify.repeat;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import com.github.fanzezhen.fun.framework.core.exception.ExceptionUtil;
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

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author fanzezhen
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
     * 要处理的方法，包名+类名+方法名
     */
    @Pointcut("@annotation(com.github.fanzezhen.fun.framework.core.verify.repeat.NoRepeat)")
    public void cut() {
    }

    @Before("cut()")
    public void doBefore(JoinPoint joinPoint) {
        String key;
        String value;
        NoRepeat noRepeat;
        try {
            noRepeat = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NoRepeat.class);
            if (noRepeat == null) {
                return;
            }
            key = getKey(joinPoint, noRepeat);
            if (cacheService == null) {
                log.warn("noRepeated check skip cacheService is null");
                return;
            }
            value = cacheService.get(key);
        } catch (Throwable throwable) {
            log.error("noRepeated check failed exception", throwable);
            return;
        }
        if (!CharSequenceUtil.isEmpty(value)) {
            throw ExceptionUtil.wrapException("请勿重复提交");
        }
        try {
            cacheService.set(key, String.valueOf(System.currentTimeMillis()), noRepeat.timeout(), noRepeat.timeUnit());
        } catch (Exception exception) {
            log.error("noRepeated put failed exception", exception);
        }
    }

    private String getKey(JoinPoint joinPoint, NoRepeat noRepeat) {
        Object[] args = joinPoint.getArgs();
        JSONObject param = new JSONObject();
        String[] headerArgs = noRepeat.headerArgs();
        loadHeaderArgs(param, headerArgs);
        String[] paramArgs = noRepeat.paramArgs();
        loadParamArgs(param, paramArgs, args);
        String headerJsonStr = ContextHolder.getHeaderJsonStr(noRepeat.headerArgs());
        String paramKey = noRepeat.key();
        if (CharSequenceUtil.isEmpty(paramKey)) {
            paramKey = JSON.toJSONString(Arrays.stream(args).filter(arg -> !(arg instanceof HttpServletRequest)).toList());
        }
        String key = env + StrPool.SLASH + springApplicationName + StrPool.SLASH + "NoRepeat" + StrPool.SLASH + joinPoint.getTarget().getClass().getName() + StrPool.DOT + joinPoint.getSignature().getName() + StrPool.SLASH + paramKey + StrPool.SLASH + headerJsonStr + StrPool.SLASH + param.toJSONString();
        log.info("key={}", key);
        return key;
    }

    private static void loadParamArgs(JSONObject param, String[] paramArgs, Object[] args) {
        if (ArrayUtil.isNotEmpty(paramArgs) && ArrayUtil.isNotEmpty(args)) {
            for (String validArg : paramArgs) loadParamArgs(param, validArg, args);
        }
    }

    private static void loadParamArgs(JSONObject param, String validArg, Object[] args) {
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

    private static void loadHeaderArgs(JSONObject param, String[] headerArgs) {
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
