package com.github.fanzezhen.fun.framework.core.log.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.log")
@EnableFeignClients(basePackages = {"com.github.fanzezhen.fun.framework.core.log"})
public class FunCoreLogAutoConfiguration {
}
