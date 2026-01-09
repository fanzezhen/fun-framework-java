package com.github.fanzezhen.fun.framework.core.context;

import com.github.fanzezhen.fun.framework.core.context.properties.FunCoreContextProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * @author fanzezhen
 */
@Configuration
@EnableConfigurationProperties(FunCoreContextProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.context")
public class FunCoreContextAutoConfiguration {
    @Resource
    private FunCoreContextProperties funCoreContextProperties;

    @PostConstruct
    public void init() {
        ContextHolder.setProperties(funCoreContextProperties);
    }
}
