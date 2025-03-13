package com.github.fanzezhen.fun.framework.core.verify.jwt;

import java.lang.annotation.*;

/**
 * JWT校验
 *
 * @author fanzezhen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface JwtVerify {
    String header() default "TM-Header-Base-JwtToken";
}
