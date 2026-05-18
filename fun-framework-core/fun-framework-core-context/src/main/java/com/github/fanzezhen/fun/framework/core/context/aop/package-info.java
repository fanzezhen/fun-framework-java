/**
 * 上下文AOP增强包.
 * <p>
 * 提供基于注解的上下文请求头校验和隐藏能力，配合
 * {@link com.github.fanzezhen.fun.framework.core.context.aop.ContextHeaderChecker}
 * 注解使用，支持：
 * <ul>
 *   <li>校验必需的请求头是否存在</li>
 *   <li>临时隐藏指定请求头，方法执行完成后自动恢复</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.core.context.aop;
