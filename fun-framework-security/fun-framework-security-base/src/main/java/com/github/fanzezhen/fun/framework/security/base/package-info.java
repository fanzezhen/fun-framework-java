/**
 * 安全基础模块。
 * <p>
 * 提供统一的安全框架基础接口和配置，包括：
 * <ul>
 *   <li>权限管理门面接口 {@link com.github.fanzezhen.fun.framework.security.base.FunSecurityFacade}</li>
 *   <li>安全配置属性 {@link com.github.fanzezhen.fun.framework.security.base.FunSpringSecurityProperties}</li>
 *   <li>登录参数和结果接口定义</li>
 * </ul>
 * <p>
 * 该模块不依赖具体的安全框架实现，可与 Spring Security 或 Sa-Token 等框架集成使用。
 */
package com.github.fanzezhen.fun.framework.security.base;
