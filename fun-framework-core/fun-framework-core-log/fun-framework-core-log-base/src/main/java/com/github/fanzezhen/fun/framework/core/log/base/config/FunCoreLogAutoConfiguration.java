package com.github.fanzezhen.fun.framework.core.log.base.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 */
@Configuration
@EnableConfigurationProperties(FunLogProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.log")
public class FunCoreLogAutoConfiguration {
}
