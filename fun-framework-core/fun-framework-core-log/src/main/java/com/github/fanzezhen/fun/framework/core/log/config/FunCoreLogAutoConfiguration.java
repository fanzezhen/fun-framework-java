package com.github.fanzezhen.fun.framework.core.log.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 日志模块的自动配置类.
 * <p>
 * 加载日志辅助工具（FunLogHelper）和相关组件。
 * 支持结构化日志记录、敏感数据脱敏和日志上下文传播。
 */
@Configuration
@EnableConfigurationProperties(FunLogProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.log")
public class FunCoreLogAutoConfiguration {
}
