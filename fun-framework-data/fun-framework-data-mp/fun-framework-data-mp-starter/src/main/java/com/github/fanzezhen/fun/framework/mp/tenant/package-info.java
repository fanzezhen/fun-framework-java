/**
 * 多租户数据隔离包
 * <p>
 * 基于 MyBatis-Plus {@code TenantLineInnerInterceptor} 的自动隔离能力，总开关
 * {@code fun.mp.tenant.enabled} 默认关闭。核心组件：
 * <ul>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.tenant.DefaultTenantLineHandler} - 三级判定隔离范围</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.tenant.TenantColumnCache} - 含租户列的表名缓存（主判据）</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.tenant.IgnoreTenant} - 跨租户逃生口注解</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.tenant.TenantIgnoreAspect} - 逃生口切面</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.tenant.TenantIgnoreContext} - 逃生口线程上下文</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.tenant.AbstractTenantContextInterceptor} - 请求入口拦截器骨架</li>
 *   <li>{@link com.github.fanzezhen.fun.framework.mp.tenant.FunTenantConstant} - 内置缺省值常量</li>
 * </ul>
 * <p>
 * 租户号本身由 {@code ContextHolder} 承载，不另立线程上下文——请求头 {@code fun-tenant-id}
 * 经 {@code FunContextFilter} 写入后天然流到 SQL 层。
 *
 * @since 4.1.1
 */
package com.github.fanzezhen.fun.framework.mp.tenant;
