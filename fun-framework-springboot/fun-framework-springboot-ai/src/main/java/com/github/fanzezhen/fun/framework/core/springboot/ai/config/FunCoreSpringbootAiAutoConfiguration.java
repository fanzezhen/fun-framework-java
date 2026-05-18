package com.github.fanzezhen.fun.framework.core.springboot.ai.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Fun Framework Springboot AI 模块自动配置.
 * <p>
 * 自动扫描并注册 AI 模块相关的 Spring Bean，包括日志打印过滤器等组件。
 * </p>
 *
 * @since 4.0.5
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.springboot.ai")
public class FunCoreSpringbootAiAutoConfiguration {
}
