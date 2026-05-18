package com.github.fanzezhen.fun.framework.core.context;

import com.github.fanzezhen.fun.framework.core.context.properties.FunCoreContextProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * 上下文模块自动配置类.
 * <p>
 * 加载上下文管理相关组件，并在初始化时将配置属性注入到 {@link ContextHolder} 中，
 * 使其支持从请求头中提取和传递用户信息、租户信息、traceId等上下文数据。
 */
@Configuration
@EnableConfigurationProperties(FunCoreContextProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.context")
public class FunCoreContextAutoConfiguration {
    /**
     * 上下文配置属性.
     */
    @Resource
    private FunCoreContextProperties funCoreContextProperties;

    /**
     * 初始化上下文配置.
     * <p>
     * 将配置属性注入ContextHolder，确保后续可通过ContextHolder获取自定义的请求头Key
     */
    @PostConstruct
    public void init() {
        ContextHolder.setProperties(funCoreContextProperties);
    }
}
