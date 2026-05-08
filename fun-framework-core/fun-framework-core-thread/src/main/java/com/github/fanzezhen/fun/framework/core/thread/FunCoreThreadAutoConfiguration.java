package com.github.fanzezhen.fun.framework.core.thread;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池模块自动配置类
 * <p>
 * 加载默认线程池（PoolExecutors）和线程装饰器（ThreadPoolTaskDecorator）等组件，
 * 支持上下文传递、线程池监控等功能。
 *
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.thread")
@ConfigurationPropertiesScan("com.github.fanzezhen.fun.framework.core.thread")
public class FunCoreThreadAutoConfiguration {
}
