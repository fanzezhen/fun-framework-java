package com.github.fanzezhen.fun.framework.api.count;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;
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
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * Controller切面，统计所有接口参数空置率.
 * <p>
 * 通过AOP拦截所有Controller方法，异步统计接口请求次数和响应字段的填充情况，
 * 数据存储在Redis的ZSet中，Key有效期365天。
 * <p>
 * <b>性能考虑：</b>
 * <ul>
 *   <li>前置任务和后置任务均在独立线程池中异步执行，不阻塞主请求</li>
 *   <li>统计失败不影响业务逻辑，只记录warn日志</li>
 * </ul>
 */
@Slf4j
@Aspect
@Component
public class FunApiCountAop {
    /**
     * Redis Key 过期天数.
     */
    private static final int REDIS_KEY_EXPIRE_DAYS = NormalTypeConstant.INT_365;

    /**
     * Redis模板，用于存储统计数据.
     */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 结果解析器，用于从Controller返回值中提取实际数据.
     */
    @Resource
    private FunApiCountResultResolve funApiCountResultResolve;

    /**
     * API统计专用线程池，异步执行统计任务.
     */
    @Resource(name = "funApiCountThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor funApiCountThreadPoolTaskExecutor;

    /**
     * Spring应用名称，用于构建Redis Key前缀.
     */
    @Value("${spring.application.name}")
    private String springApplicationName;

    /**
     * 定义切点，拦截所有Controller类中的方法.
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) " +
        "|| @within(org.springframework.stereotype.Controller)")
    public void webExecutePointcut() {
    }

    /**
     * 环绕通知，统计接口请求和响应字段.
     * <p>
     * 工作流程：
     * <ol>
     *   <li>异步任务1：记录请求次数（前置）</li>
     *   <li>执行目标方法</li>
     *   <li>异步任务2：统计响应字段填充情况（后置）</li>
     * </ol>
     * 所有统计任务失败不会影响业务逻辑，仅记录warn日志。
     *
     * @param joinPoint 切点信息
     * @return 目标方法返回值
     * @throws Throwable 目标方法抛出的异常
     */
    @Around("webExecutePointcut()")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
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

    /**
     * 增加ZSet中指定成员的分数（增量为1）.
     *
     * @param key     Redis ZSet的Key
     * @param hashKey ZSet的成员（如字段名或@Request）
     */
    public void incrementKey(final String key, final String hashKey) {
        incrementKey(key, hashKey, 1);
    }

    /**
     * 增加ZSet中指定成员的分数.
     * <p>
     * 每次调用会自动刷新Key的过期时间为365天。
     *
     * @param key     Redis ZSet的Key
     * @param hashKey ZSet的成员（如字段名或@Request）
     * @param delta   增量值
     */
    public void incrementKey(final String key, final String hashKey, final long delta) {
        ZSetOperations<String, Object> operations = redisTemplate.opsForZSet();
        operations.incrementScore(key, hashKey, delta);
        redisTemplate.expire(key, Duration.ofDays(REDIS_KEY_EXPIRE_DAYS));
    }

    /**
     * 构建Redis Key（使用当前应用名称）.
     *
     * @param url 请求URL
     * @return Redis Key
     */
    public String getKey(final String url) {
        return getKey(springApplicationName, url);
    }

    /**
     * 获取Redis Key前缀.
     * <p>
     * 格式：{应用名}:WebCount:
     *
     * @param springApplicationName Spring应用名称
     * @return Redis Key前缀
     */
    public static String getKeyPrefix(final String springApplicationName) {
        return springApplicationName + StrPool.COLON + "WebCount" + StrPool.COLON;
    }

    /**
     * 构建Redis Key.
     * <p>
     * 格式：{应用名}:WebCount:{URL}
     *
     * @param springApplicationName Spring应用名称
     * @param url                   请求URL
     * @return Redis Key
     */
    public static String getKey(final String springApplicationName, final String url) {
        return getKeyPrefix(springApplicationName) + url;
    }

    /**
     * 从HttpServletRequest中提取URL并构建Redis Key.
     *
     * @param request HTTP请求
     * @return Redis Key
     */
    private String getRequestKey(final HttpServletRequest request) {
        String requestURL = request.getRequestURI();
        return getKey(requestURL);
    }

    /**
     * 处理响应数据，统计字段填充情况.
     * <p>
     * 支持以下数据类型：
     * <ul>
     *   <li>Java Bean：遍历所有字段，统计每个字段的空/非空次数</li>
     *   <li>Collection/Array：取第一个非空元素作为模型进行分析</li>
     *   <li>简单类型：仅记录类型名称</li>
     * </ul>
     *
     * @param key    Redis Key
     * @param object Controller方法返回值
     */
    private void handleResponseData(final String key, final Object object) {
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

    /**
     * 处理Java Bean字段，统计每个字段的填充情况.
     * <p>
     * 优先使用 {@link JSONField#name()} 作为字段名，否则使用Java字段名。
     * 字段值为空时增量为0（相当于不增加计数），非空时增量为1。
     *
     * @param key                 Redis Key
     * @param responseModelClass  响应模型类
     * @param item                响应对象实例
     */
    private void processFields(final String key, final Class<?> responseModelClass, final Object item) {
        for (Field field : ReflectUtil.getFields(responseModelClass)) {
            JSONField jsonField = field.getAnnotation(JSONField.class);
            String fieldName = jsonField == null || CharSequenceUtil.isEmpty(jsonField.name())
                ? field.getName() : jsonField.name();
            Object fieldValue = ReflectUtil.getFieldValue(item, field);
            if (isFieldValueEmpty(fieldValue)) {
                incrementKey(key, fieldName, 0);
            } else {
                incrementKey(key, fieldName);
            }
        }
    }

    /**
     * 判断字段值是否为空.
     * <p>
     * 空值定义：
     * <ul>
     *   <li>null</li>
     *   <li>空白字符串</li>
     *   <li>空集合</li>
     *   <li>空数组</li>
     * </ul>
     *
     * @param fieldValue 字段值
     * @return true表示为空，false表示非空
     */
    private boolean isFieldValueEmpty(final Object fieldValue) {
        return fieldValue == null ||
           CharSequenceUtil.isBlank(fieldValue.toString()) ||
           ((fieldValue instanceof Collection) && CollUtil.isEmpty((Collection<?>) fieldValue)) ||
           ArrayUtil.isEmpty(fieldValue);
    }
}
