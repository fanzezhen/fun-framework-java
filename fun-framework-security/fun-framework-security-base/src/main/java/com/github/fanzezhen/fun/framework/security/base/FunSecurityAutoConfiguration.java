package com.github.fanzezhen.fun.framework.security.base;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.security")
@EnableConfigurationProperties(FunSpringSecurityProperties.class)
public class FunSecurityAutoConfiguration {
}
