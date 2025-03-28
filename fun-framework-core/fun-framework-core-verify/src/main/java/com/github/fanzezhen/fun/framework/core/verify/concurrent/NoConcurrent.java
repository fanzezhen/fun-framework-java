package com.github.fanzezhen.fun.framework.core.verify.concurrent;

import java.lang.annotation.*;

/**
 * 禁止并发
 *
 * @author fanzezhen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NoConcurrent {

    String key() default "";
    String[] headerArgs() default {};
}
