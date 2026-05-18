package com.github.fanzezhen.fun.framework.trace.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 MyBatis-Plus 数据追踪自动配置注解
 * <p>
 * 在 Spring Boot 启动类或配置类上添加此注解，可自动启用数据变更追踪功能。
 * 该注解会自动导入 {@link FunMpTraceAutoConfiguration} 配置类，初始化追踪拦截器和相关组件。
 * <p>
 * 使用示例：
 * <pre>
 * &#64;SpringBootApplication
 * &#64;EnableFunMpTraceAutoConfiguration
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * </pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({FunMpTraceAutoConfiguration.class})
public @interface EnableFunMpTraceAutoConfiguration {
}
