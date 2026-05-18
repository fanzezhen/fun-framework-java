package com.github.fanzezhen.fun.framework.proxy.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 代理模块自动配置类。
 * <p>
 * 自动扫描并加载 com.github.fanzezhen.fun.framework.proxy
 * 包下的所有代理相关组件，包括静态资源代理、实体字段代理等功能。
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.proxy")
public class ProxyAutoConfiguration {
}
