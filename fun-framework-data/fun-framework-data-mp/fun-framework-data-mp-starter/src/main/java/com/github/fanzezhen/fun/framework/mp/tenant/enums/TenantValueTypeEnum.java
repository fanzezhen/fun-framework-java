package com.github.fanzezhen.fun.framework.mp.tenant.enums;

/**
 * 租户 ID 拼进 SQL 的字面量类型
 * <p>
 * 由配置 {@code fun.mp.tenant.value-type} 决定。租户上下文统一以字符串承载
 * （{@code ContextHolder.getTenantId()}），但实体侧 {@code tenantId} 有 Integer / Long / String
 * 三种，数据库列类型随之不同，故拼 SQL 时需要知道该用哪种字面量。
 * </p>
 *
 * @since 4.1.1
 */
public enum TenantValueTypeEnum {

    /**
     * 字符串字面量，拼作 {@code tenant_id = '1001'}
     * <p>
     * 默认值。适配 String 类型租户列（如 {@code uuid.tenant.BaseTenantEntity}）。
     * 租户列为数值类型时用本项会触发隐式类型转换，可能导致索引失效，应改配 {@link #LONG}。
     * </p>
     */
    STRING,

    /**
     * 数值字面量，拼作 {@code tenant_id = 1001}
     * <p>
     * 适配 Integer / Long 类型租户列（如 {@code snowflake.tenant.BaseTenantEntity}）。
     * 上下文中的租户号无法解析为数值时抛业务异常，不静默降级为字符串——静默降级会让
     * 「配置与实际列类型不匹配」这类问题潜伏到线上才以索引失效的形式暴露。
     * </p>
     */
    LONG,
    ;
}
