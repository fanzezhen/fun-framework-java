package com.github.fanzezhen.fun.framework.all;

import com.github.fanzezhen.fun.framework.core.config.EnableCoreConfig;
import com.github.fanzezhen.fun.framework.exception.config.EnableExceptionConfig;
import com.github.fanzezhen.fun.framework.log.config.EnableLogConfig;
import com.github.fanzezhen.fun.framework.mp.EnableFunMpAutoConfiguration;
import com.github.fanzezhen.fun.framework.redis.EnableFunRedisAutoConfiguration;
import com.github.fanzezhen.fun.framework.swagger.config.EnableSpringDocConfig;
import com.github.fanzezhen.fun.framework.web.config.EnableWebConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zezhen.fan
 */
@EnableCoreConfig
@EnableFunRedisAutoConfiguration
@EnableLogConfig
@EnableExceptionConfig
@EnableSpringDocConfig
@EnableFunMpAutoConfiguration
@EnableWebConfig
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableFunAutoConfiguration {
}
