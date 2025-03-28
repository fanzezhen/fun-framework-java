package com.github.fanzezhen.fun.framework.core.verify.repeat;

import com.github.fanzezhen.fun.framework.core.context.properties.ContextConstant;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 防止重复提交注解
 *
 * @author fanzezhen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NoRepeat {
    String key() default "";

    String[] paramArgs() default {};

    String[] headerArgs() default {ContextConstant.DEFAULT_HEADER_TENANT_ID};

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 超时时间（毫秒）
     */
    long timeout() default 1;
}
