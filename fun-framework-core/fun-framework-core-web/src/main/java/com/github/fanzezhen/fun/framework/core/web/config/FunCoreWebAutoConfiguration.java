package com.github.fanzezhen.fun.framework.core.web.config;

import com.github.fanzezhen.fun.framework.core.web.FunCoreWebProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 */
@Configuration
@EnableConfigurationProperties(FunCoreWebProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.web")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class FunCoreWebAutoConfiguration {
}
