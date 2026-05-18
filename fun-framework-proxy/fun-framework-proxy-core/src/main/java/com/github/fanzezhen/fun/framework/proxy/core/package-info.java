/**
 * 代理核心模块，提供统一的代理装饰器和配置功能。
 * <p>
 * 核心功能：
 * <ul>
 *   <li>静态资源URL代理转换（内网地址→外网地址）</li>
 *   <li>字段级别的代理装饰（支持注解标记）</li>
 *   <li>HTTP请求代理转发</li>
 *   <li>支持递归处理复杂对象（Map/List/Bean）</li>
 * </ul>
 * <p>
 * 核心类：
 * <ul>
 *   <li>{@link com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper}
 *   - 代理装饰器门面类</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.proxy.core.ProxyDecorator}
 *   - 核心装饰器实现</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.proxy.core.ProxyField}
 *   - 字段级代理注解</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.proxy.core.ProxyController}
 *   - HTTP代理转发接口</li>
 * </ul>
 * <p>
 * 配置示例（application.yml）：
 * <pre>{@code
 * fun:
 *   proxy:
 *     enabled: true
 *     api: http://proxy.example.com/proxy/static/
 *     address-list:
 *       - origin: internal.cdn.com
 *         target: cdn.example.com
 * }</pre>
 */
package com.github.fanzezhen.fun.framework.proxy.core;
