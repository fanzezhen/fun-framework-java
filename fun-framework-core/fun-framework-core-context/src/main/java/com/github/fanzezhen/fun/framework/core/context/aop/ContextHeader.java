package com.github.fanzezhen.fun.framework.core.context.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求头校验
 *
 * @author fanzezhen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ContextHeader {
    /**
     * 需校验的请求头
     */
    String[] required() default {};

    /**
     * 需隐藏的请求头
     */
    String[] hidden() default {};
}
