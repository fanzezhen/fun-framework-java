package com.github.fanzezhen.fun.framework.core.thread;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.thread")
@ConfigurationPropertiesScan("com.github.fanzezhen.fun.framework.core.thread")
@ServletComponentScan
public class FunCoreThreadAutoConfiguration {
}
