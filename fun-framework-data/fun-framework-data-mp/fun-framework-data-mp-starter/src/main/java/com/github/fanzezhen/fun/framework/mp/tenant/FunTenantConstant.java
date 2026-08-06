package com.github.fanzezhen.fun.framework.mp.tenant;

/**
 * 多租户常量
 * <p>
 * 只定义与租户隔离机制强相关、无业务语义的常量。业务租户号的分配、校验、
 * 停用保护等规则由业务侧负责，脚手架不介入。
 * </p>
 *
 * @since 4.1.1
 */
public final class FunTenantConstant {

    private FunTenantConstant() {
    }

    /**
     * 租户隔离列名的<b>内置缺省值</b>
     * <p>
     * 即配置项 {@code fun.mp.tenant.column} 未显式指定时的取值，与 MyBatis-Plus
     * {@code TenantLineHandler#getTenantIdColumn()} 的默认值一致。运行期请读
     * {@code FunMpProperties.Tenant#getColumn()}，勿直接引用本常量——否则改了配置而
     * 列缓存仍按 {@code tenant_id} 扫库，会出现「拼的列与扫的列不是同一个」的错配。
     * </p>
     */
    public static final String TENANT_COLUMN = "tenant_id";

    /**
     * 默认租户 ID 的<b>内置缺省值</b>
     * <p>
     * 即配置项 {@code fun.mp.tenant.default-tenant-id} 未显式指定时的取值。承载平台级 /
     * 历史无租户数据；存量表加租户列时统一 backfill 为该值。运行期请读
     * {@code FunMpProperties.Tenant#getDefaultTenantId()}。
     * </p>
     */
    public static final String DEFAULT_TENANT_ID = "0";
}
