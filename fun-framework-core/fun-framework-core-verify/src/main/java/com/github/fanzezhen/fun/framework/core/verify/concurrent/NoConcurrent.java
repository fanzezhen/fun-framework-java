package com.github.fanzezhen.fun.framework.core.verify.concurrent;

import com.github.fanzezhen.fun.framework.core.context.ContextConstant;

import java.lang.annotation.*;

/**
 * 禁止并发
 *
 * @author zezhen.fan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NoConcurrent {

    String key() default "";
    String[] headerArgs() default {ContextConstant.HEADER_TENANT_ID};
}
