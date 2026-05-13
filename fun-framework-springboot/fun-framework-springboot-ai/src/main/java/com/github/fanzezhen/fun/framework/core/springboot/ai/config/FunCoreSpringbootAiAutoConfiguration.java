package com.github.fanzezhen.fun.framework.core.springboot.ai.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * MapperFacadeUtil 自动配置
 * <p>
 * 在 Spring 容器初始化完成后，自动将 MapperFacade 实例注入到 MapperFacadeUtil 工具类中，
 * 使其能够在任何地方通过静态方法调用进行对象映射。
 * </p>
 *
 * @since 4.0.5
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.springboot.ai")
public class FunCoreSpringbootAiAutoConfiguration {
}
