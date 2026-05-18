package com.github.fanzezhen.fun.framework.core.context.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求头校验注解.
 * <p>
 * 用于方法级别的上下文请求头校验和隐藏，配合 {@link FunContextAop} 切面使用。
 * 提供两种能力：
 * <ul>
 *   <li>required: 校验必需的请求头是否存在，缺失时抛出ServiceException</li>
 *   <li>hidden: 临时隐藏指定请求头，方法执行完成后自动恢复</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ContextHeaderChecker {
    /**
     * 需校验的请求头列表.
     * <p>
     * 如果指定的请求头不存在，切面会抛出 ServiceException。
     *
     * @return 必需的请求头Key数组
     */
    String[] requireds() default {};

    /**
     * 需临时隐藏的请求头列表.
     * <p>
     * 方法执行前移除这些请求头，执行完成后自动恢复，用于防止内部方法调用时污染上下文。
     *
     * @return 需要隐藏的请求头Key数组
     */
    String[] hiddens() default {};
}
