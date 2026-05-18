/**
 * MyBatis 代理增强模块
 * <p>
 * 提供 MyBatis 查询结果的自动URL代理转换功能
 * <p>
 * 核心功能：
 * <ul>
 *   <li>查询拦截器（{@link com.github.fanzezhen.fun.framework.proxy.mybatis.ProxyMybatisInterceptor}）：
 *       拦截所有查询操作，对结果进行代理处理</li>
 *   <li>类型处理器（{@link com.github.fanzezhen.fun.framework.proxy.mybatis.ProxyFieldStringTypeHandler}）：
 *       字符串字段从数据库读取时自动进行URL转换</li>
 * </ul>
 * <p>
 * 使用方式：
 * <ol>
 *   <li>引入本模块依赖</li>
 *   <li>在实体类字段上添加 @ProxyField 注解</li>
 *   <li>配置 fun.proxy.enabled=true</li>
 *   <li>MyBatis 查询结果自动进行URL转换</li>
 * </ol>
 * <p>
 * 注意事项：
 * <ul>
 *   <li>拦截器会处理所有查询操作，建议仅在需要时启用</li>
 *   <li>类型处理器需在 MyBatis 配置中注册才能生效</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.proxy.mybatis;
