package com.github.fanzezhen.fun.framework.core.springboot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Fun Framework Core Springboot 模块自动配置。
 * <p>
 * 汇总核心 Spring Boot 集成能力：组件扫描与对象映射引擎装配
 * （委托 {@link FunMapperAutoConfiguration}）。
 * </p>
 *
 * @since 4.0.5
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.springboot")
@Import(FunMapperAutoConfiguration.class)
public class FunCoreSpringbootAutoConfiguration {
}
