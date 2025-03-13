package com.github.fanzezhen.fun.framework.core.verify;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 */
@Configuration
@EnableConfigurationProperties(FunCoreVerifyProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.verify")
@ConfigurationPropertiesScan("com.github.fanzezhen.fun.framework.core.verify")
@ServletComponentScan
public class FunCoreVerifyAutoConfiguration {
}
