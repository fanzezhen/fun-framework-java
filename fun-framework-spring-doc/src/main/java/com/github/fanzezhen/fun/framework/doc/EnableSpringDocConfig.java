package com.github.fanzezhen.fun.framework.doc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 SpringDoc 配置注解.
 *
 * <p>通过 {@code @Import} 导入 {@link FunDocAutoConfiguration} 配置类，
 * 快速启用 Fun 框架的 SpringDoc 文档功能
 *
 * <p>使用示例：
 * <pre>{@code
 * @SpringBootApplication
 * @EnableSpringDocConfig
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * }</pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({FunDocAutoConfiguration.class})
public @interface EnableSpringDocConfig {
}
