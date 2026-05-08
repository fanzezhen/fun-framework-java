package com.github.fanzezhen.fun.framework.security.base;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 安全模块自动配置类
 * <p>
 * 加载安全相关组件并启用配置属性 {@link FunSpringSecurityProperties}，
 * 支持接口白名单、CAS/OAuth单点登录、微服务路由认证等功能。
 *
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.security")
@EnableConfigurationProperties(FunSpringSecurityProperties.class)
public class FunSecurityAutoConfiguration {
}
