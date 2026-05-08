package com.github.fanzezhen.fun.framework.core.exception;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 核心异常模块自动配置类
 * <p>
 * 加载全局异常处理器（DefaultExceptionHandler）等异常处理相关组件，
 * 提供统一的异常拦截和响应格式化能力。
 *
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.exception")
public class FunCoreExceptionAutoConfiguration {
}
