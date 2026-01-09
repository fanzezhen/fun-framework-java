package com.github.fanzezhen.fun.framework.api.count;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Controller切面：统计所有接口参数空置率
 */
@Slf4j
@Aspect
@Component
public class FunApiCountAop {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private FunApiCountResultResolve funApiCountResultResolve;
    @Resource(name = "funApiCountThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor funApiCountThreadPoolTaskExecutor;
    @Value("${spring.application.name}")
    private String springApplicationName;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) " +
        "|| @within(org.springframework.stereotype.Controller)")
    public void webExecutePointcut() {
    }

    @Around("webExecutePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed(joinPoint.getArgs());
        }
        Future<String> future = funApiCountThreadPoolTaskExecutor.submit(() -> {
            try {
                String key = getRequestKey(attributes.getRequest());
                if (key != null) {
                    incrementKey(key, "@Request");
                }
                return key;
            } catch (Exception e) {
                log.warn("接口统计切面获取url失败", e);
                return null;
            }
        });
        final Object object = joinPoint.proceed(joinPoint.getArgs());
        funApiCountThreadPoolTaskExecutor.execute(() -> {
            try {
                String key = future.get();
                if (key != null) {
                    handleResponseData(key, object);
                } else {
                    log.warn("接口切面统计失败，key生成失败");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 重新中断当前线程
                log.warn("接口切面统计前置任务报错", e);
            } catch (Exception e) {
                log.warn("接口切面统计前置任务报错", e);
            }
        });

        return object;
    }

    public void incrementKey(String key, String hashKey) {
        incrementKey(key, hashKey, 1);
    }

    public void incrementKey(String key, String hashKey, long delta) {
        ZSetOperations<String, Object> operations = redisTemplate.opsForZSet();
        operations.incrementScore(key, hashKey, delta);
        redisTemplate.expire(key, 365, TimeUnit.DAYS);
    }

    public String getKey(String url) {
        return getKey(springApplicationName, url);
    }

    public static String getKeyPrefix(String springApplicationName) {
        return springApplicationName + StrPool.COLON + "WebCount" + StrPool.COLON;
    }

    public static String getKey(String springApplicationName, String url) {
        return getKeyPrefix(springApplicationName) + url;
    }

    private String getRequestKey(HttpServletRequest request) {
        String requestURL = request.getRequestURI();
        return getKey(requestURL);
    }

    private void handleResponseData(String key, Object object) {
        try {
            Object data = funApiCountResultResolve.unseal(object);
            Object model = data;
            if (data != null) {
                if (ArrayUtil.isArray(data)) {
                    data = Arrays.stream((Object[]) data).toList();
                }
                if (data instanceof Collection) {
                    model = ((Collection<?>) data).stream().filter(Objects::nonNull).findAny().orElse(null);
                } else {
                    data = CollUtil.newArrayList(data);
                }
            }
            incrementKey(key, "@Response");
            if (model != null) {
                Class<?> responseModelClass = model.getClass();
                incrementKey(key, "@Response@" + responseModelClass.getName());
                if (BeanUtil.isBean(responseModelClass)) {
                    for (Object item : ((Collection<?>) data)) {
                        incrementKey(key, "@BeanClass@" + responseModelClass.getName());
                        processFields(key, responseModelClass, item);
                    }
                } else {
                    incrementKey(key, "@SimpleClass@" + responseModelClass.getName());
                }
            }
        } catch (Exception e) {
            log.warn("接口切面统计报错", e);
        }
    }

    private void processFields(String key, Class<?> responseModelClass, Object item) {
        for (Field field : ReflectUtil.getFields(responseModelClass)) {
            JSONField jsonField = field.getAnnotation(JSONField.class);
            String fieldName = jsonField == null || CharSequenceUtil.isEmpty(jsonField.name()) ? field.getName() : jsonField.name();
            Object fieldValue = ReflectUtil.getFieldValue(item, field);
            if (isFieldValueEmpty(fieldValue)) {
                incrementKey(key, fieldName, 0);
            } else {
                incrementKey(key, fieldName);
            }
        }
    }

    private boolean isFieldValueEmpty(Object fieldValue) {
        return fieldValue == null ||
           CharSequenceUtil.isBlank(fieldValue.toString()) ||
           ((fieldValue instanceof Collection) && CollUtil.isEmpty((Collection<?>) fieldValue)) ||
           ArrayUtil.isEmpty(fieldValue);
    }
}
