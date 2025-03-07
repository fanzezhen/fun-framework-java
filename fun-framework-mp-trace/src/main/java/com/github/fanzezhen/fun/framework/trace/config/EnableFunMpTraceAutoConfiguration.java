package com.github.fanzezhen.fun.framework.trace.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zezhen.fan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({FunMpTraceAutoConfiguration.class})
public @interface EnableFunMpTraceAutoConfiguration {
}
