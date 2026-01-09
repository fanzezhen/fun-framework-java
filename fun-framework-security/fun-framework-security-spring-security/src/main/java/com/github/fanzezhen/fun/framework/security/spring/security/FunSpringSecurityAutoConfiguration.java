package com.github.fanzezhen.fun.framework.security.spring.security;

import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.security.spring.security")
@ServletComponentScan
public class FunSpringSecurityAutoConfiguration {
}
