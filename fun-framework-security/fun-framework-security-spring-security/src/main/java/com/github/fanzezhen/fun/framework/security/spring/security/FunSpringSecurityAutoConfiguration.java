package com.github.fanzezhen.fun.framework.security.spring.security;

import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Security集成自动配置类
 * <p>
 * 加载Spring Security相关组件（过滤器、认证配置等），
 * 并启用Servlet组件扫描以支持自定义Filter。
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.security.spring.security")
@ServletComponentScan
public class FunSpringSecurityAutoConfiguration {
}
