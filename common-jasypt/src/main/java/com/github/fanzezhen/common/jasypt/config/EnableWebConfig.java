package com.github.fanzezhen.common.jasypt.config;

import com.github.fanzezhen.common.core.config.EnableCoreConfig;
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
@Import({JasyptSpringConfig.class})
public @interface EnableWebConfig {
}
