/**
 * 上下文管理核心包.
 * <p>
 * 提供基于ThreadLocal的请求上下文管理能力，
 * 支持在请求链路中传递用户、租户、追踪等信息。
 * 主要组件：
 * <ul>
 *   <li>{@link com.github.fanzezhen.fun.framework.core.context.Context} - 上下文数据容器</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.core.context.ContextHolder} - 线程上下文管理器</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.core.context.FunContextFilter} - 上下文过滤器</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.core.context;
