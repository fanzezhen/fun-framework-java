package com.github.fanzezhen.fun.framework.core.verify;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 验证模块自动配置类.
 * <p>
 * 加载验证相关组件：JWT令牌验证、防重复提交（@NoRepeat）、防并发（@NoConcurrent）等功能.
 * 当容器中不存在自定义JwtService时，注册默认的JWT服务实现.
 */
@Configuration
@EnableConfigurationProperties(FunCoreVerifyProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.verify")
@ConfigurationPropertiesScan("com.github.fanzezhen.fun.framework.core.verify")
public class FunCoreVerifyAutoConfiguration {
}
