package com.github.fanzezhen.fun.framework.exception.config;

import com.github.fanzezhen.fun.framework.core.config.EnableCoreConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zezhen.fan
 */
@EnableCoreConfig
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ExceptionSpringConfig.class})
public @interface EnableExceptionConfig {
}
