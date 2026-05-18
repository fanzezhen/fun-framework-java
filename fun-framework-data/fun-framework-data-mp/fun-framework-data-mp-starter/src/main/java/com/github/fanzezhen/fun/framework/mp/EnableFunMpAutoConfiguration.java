package com.github.fanzezhen.fun.framework.mp;

import com.github.fanzezhen.fun.framework.mp.config.FunMpAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 MyBatis-Plus 增强自动配置注解
 * <p>
 * 在 Spring Boot 主类或配置类上添加此注解，可启用框架提供的 MyBatis-Plus 增强功能。
 * <p>
 * 该注解会导入 {@link FunMpAutoConfiguration} 配置类，提供以下功能：
 * <ul>
 *   <li>MybatisPlusInterceptor 拦截器配置</li>
 *   <li>SQL 注入器配置（支持批量插入）</li>
 *   <li>字段自动填充处理器</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * @SpringBootApplication
 * @EnableFunMpAutoConfiguration
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * }</pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({FunMpAutoConfiguration.class})
public @interface EnableFunMpAutoConfiguration {
}
