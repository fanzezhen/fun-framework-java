/**
 * MethodHandle 对象映射代理增强模块。
 * <p>
 * 为默认的 MethodHandle 映射引擎提供 @ProxyField 字段级脱敏/URL 代理能力，
 * 等价于 proxy-orika 之于 Orika 引擎。
 * <p>
 * 核心功能：
 * <ul>
 *   <li>字段写入 hook（{@link com.github.fanzezhen.fun.framework.proxy.methodhandle.ProxyFieldValueHook}）：
 *       映射写入目标字段前，对带 @ProxyField 注解的 String 字段进行代理装饰</li>
 * </ul>
 * <p>
 * 使用方式：
 * <ol>
 *   <li>引入本模块依赖（使用默认 MethodHandle 引擎，即 fun.mapper.engine 缺省或为 method-handle）</li>
 *   <li>在实体类字段上添加 @ProxyField 注解</li>
 *   <li>配置 fun.proxy.enabled=true 并提供 ProxyDecorator bean</li>
 *   <li>使用 MapperFacadeUtil 进行对象映射时自动进行 URL 代理转换</li>
 * </ol>
 */
package com.github.fanzezhen.fun.framework.proxy.methodhandle;
