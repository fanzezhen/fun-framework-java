/**
 * FastJson 序列化代理增强模块
 * <p>
 * 提供 FastJson2 序列化/反序列化过程中的自动URL代理转换功能
 * <p>
 * 核心功能：
 * <ul>
 *   <li>自定义字段读取器（{@link com.github.fanzezhen.fun.framework.proxy.fastjson.ProxyFieldReader}）：
 *       反序列化时自动处理代理字段</li>
 *   <li>自定义字段写入器（{@link com.github.fanzezhen.fun.framework.proxy.fastjson.ProxyFieldWriter}）：
 *       序列化时自动转换URL地址</li>
 *   <li>值过滤器（{@link com.github.fanzezhen.fun.framework.proxy.fastjson.ProxyFastJsonSerializeValueFilter}）：
 *       仅处理带有 @ProxyField 注解的字段</li>
 * </ul>
 * <p>
 * 使用方式：
 * <ol>
 *   <li>引入本模块依赖</li>
 *   <li>在实体类字段上添加 @ProxyField 注解</li>
 *   <li>配置 fun.proxy.enabled=true</li>
 *   <li>JSON序列化/反序列化时自动进行URL转换</li>
 * </ol>
 */
package com.github.fanzezhen.fun.framework.proxy.fastjson;
