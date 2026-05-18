package com.github.fanzezhen.fun.framework.proxy.core;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代理字段注解，标记需要进行代理处理的字段。
 * <p>
 * 使用场景：
 * <ul>
 *   <li>标记实体类中需要URL转换的字段
 *   （如头像URL、附件URL）</li>
 *   <li>配合 {@link ProxyHelper#decorateByAnnotation}
 *   方法使用</li>
 *   <li>在序列化/反序列化时自动进行URL代理转换</li>
 * </ul>
 * <p>
 * 示例：
 * <pre>{@code
 * public class User {
 *     @ProxyField
 *     private String avatarUrl;
 *     // 将自动从内网地址转换为外网地址
 * }
 * }</pre>
 *
 * @since 3.4.3.5
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ProxyField {

}
