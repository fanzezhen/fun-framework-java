package com.github.fanzezhen.fun.framework.mp.tenant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 关闭租户过滤：标注的方法（或类内所有方法）在执行期间跨租户，不拼租户条件
 * <p>
 * 典型场景：登录时按账号跨租户查用户、平台管理员跨租户读写、定时任务与系统调用。
 * 由 {@link TenantIgnoreAspect} 环绕生效，方法退出自动还原。
 * </p>
 * <p>
 * 与 MyBatis-Plus 自带 {@code @InterceptorIgnore(tenantLine = "true")} 的分工：后者只作用于
 * mapper 接口与其方法，前者作用于任意 Spring bean 方法——业务往往是「某个 service 流程整体
 * 跨租户」而非「某条 SQL 跨租户」，用 mapper 级注解表达不了。
 * </p>
 * <p>
 * <b>跨租户写入须显式赋值</b>：本注解只关闭「查询条件拼接」，插入时租户列同样不再自动补值，
 * 须在实体上显式 {@code setTenantId(目标租户)}，否则该列落库为 null。
 * </p>
 *
 * @since 4.1.1
 */
@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreTenant {
}
