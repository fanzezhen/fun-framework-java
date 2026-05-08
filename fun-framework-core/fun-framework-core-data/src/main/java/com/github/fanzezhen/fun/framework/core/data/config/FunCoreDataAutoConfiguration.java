package com.github.fanzezhen.fun.framework.core.data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 数据处理模块自动配置类
 * <p>
 * 加载数据处理相关工具类和组件，包括Jackson配置、YApiUtil、
 * ServerInfoUtil、StorageUtil等数据处理和格式化工具。
 *
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.data")
public class FunCoreDataAutoConfiguration {
}
