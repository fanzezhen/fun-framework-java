package com.github.fanzezhen.fun.framework.core.verify.repeat;

import com.github.fanzezhen.fun.framework.core.context.properties.ContextConstant;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 防止重复提交注解.
 * <p>
 * 通过缓存机制实现幂等性校验，防止短时间内重复提交相同请求.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NoRepeat {
    /**
     * 自定义缓存键.
     * <p>
     * 为空时使用方法参数自动生成.
     *
     * @return 缓存键
     */
    String key() default "";

    /**
     * 方法参数字段数组.
     * <p>
     * 支持通过下标访问参数，如 "0.id" 表示第一个参数的id字段.
     *
     * @return 参数字段路径数组
     */
    String[] paramArgs() default {};

    /**
     * 请求头参数数组.
     * <p>
     * 用于从 HTTP 请求头提取参数作为缓存键的一部分.
     *
     * @return 请求头键名数组
     */
    String[] headerArgs() default {ContextConstant.DEFAULT_HEADER_TENANT_ID};

    /**
     * 超时时间单位.
     *
     * @return 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 超时时间.
     * <p>
     * 在此时间内重复提交将被拒绝.
     *
     * @return 超时时长
     */
    long timeout() default 1;
}
