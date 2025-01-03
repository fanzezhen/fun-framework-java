package com.github.fanzezhen.fun.framework.log.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zezhen.fan
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.log")
@EnableFeignClients(basePackages = {"com.github.fanzezhen.fun.framework.log"})
public class LogSpringConfig {
}
