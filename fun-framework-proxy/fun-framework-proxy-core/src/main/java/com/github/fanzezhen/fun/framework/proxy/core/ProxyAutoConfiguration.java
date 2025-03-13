package com.github.fanzezhen.fun.framework.proxy.core;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.proxy")
@ServletComponentScan
public class ProxyAutoConfiguration {
}
