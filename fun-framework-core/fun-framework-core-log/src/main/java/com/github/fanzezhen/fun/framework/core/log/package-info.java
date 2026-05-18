/**
 * Fun Framework 的核心日志模块.
 * <p>
 * 提供增强的日志记录功能，包括动态日志级别管理、结构化日志记录、请求/响应日志记录以及跨线程的日志上下文传播。
 * <p>
 * 主要功能：
 * <ul>
 *   <li>无需重启的动态日志级别调整</li>
 *   <li>自动跟踪 ID 生成和传播</li>
 *   <li>带有敏感数据过滤的 HTTP 请求/响应日志记录</li>
 *   <li>针对不同对象类型的自定义序列化器</li>
 *   <li>异步/多线程场景中的 MDC 上下文传播</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.core.log;
