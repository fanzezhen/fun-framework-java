/**
 * Orika 对象映射代理增强模块
 * <p>
 * 提供 Orika 对象映射过程中的自动URL代理转换功能
 * <p>
 * 核心功能：
 * <ul>
 *   <li>自定义转换器（{@link com.github.fanzezhen.fun.framework.proxy.orika.ProxyOrikaConverter}）：
 *       字符串字段映射时自动进行URL转换</li>
 *   <li>自定义过滤器（{@link com.github.fanzezhen.fun.framework.proxy.orika.ProxyOrikaFilter}）：
 *       仅处理带有 @ProxyField 注解的字段</li>
 *   <li>自定义 MapperFactory（{@link com.github.fanzezhen.fun.framework.proxy.orika.ProxyMapperFactory}）：
 *       自动注册代理转换器</li>
 * </ul>
 * <p>
 * 使用方式：
 * <ol>
 *   <li>引入本模块依赖</li>
 *   <li>在实体类字段上添加 @ProxyField 注解</li>
 *   <li>配置 fun.proxy.enabled=true 和 fun.proxy.orika.enabled=true</li>
 *   <li>使用 MapperFacade 进行对象映射时自动进行URL转换</li>
 * </ol>
 */
package com.github.fanzezhen.fun.framework.proxy.orika;
