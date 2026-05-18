package com.github.fanzezhen.fun.framework.core.verify.concurrent;

import java.lang.annotation.*;

/**
 * 禁止并发注解.
 * <p>
 * 通过分布式锁机制防止相同请求并发执行，确保同一请求在同一时刻只能有一个实例执行.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NoConcurrent {

    /**
     * 自定义锁键.
     * <p>
     * 为空时使用方法参数自动生成.
     *
     * @return 锁键
     */
    String key() default "";

    /**
     * 请求头参数数组.
     * <p>
     * 用于从 HTTP 请求头提取参数作为锁键的一部分.
     *
     * @return 请求头键名数组
     */
    String[] headerArgs() default {};
}
