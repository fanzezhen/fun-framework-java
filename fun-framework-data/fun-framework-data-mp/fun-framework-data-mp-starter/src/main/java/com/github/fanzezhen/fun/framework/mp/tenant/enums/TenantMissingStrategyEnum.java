package com.github.fanzezhen.fun.framework.mp.tenant.enums;

/**
 * 租户上下文缺失时的处置策略
 * <p>
 * 由配置 {@code fun.mp.tenant.missing-strategy} 决定。「缺失」指
 * {@code ContextHolder.getTenantId()} 为空——请求入口未注入租户号，典型来源是定时任务、
 * MQ 消费、启动期初始化等无登录态的系统调用。
 * </p>
 * <p>
 * 本策略仅在「表需隔离」时才被触发。{@code @IgnoreTenant} 逃生口、{@code ignore-tenant-tables}
 * 例外表、无租户列的全局表在 {@code DefaultTenantLineHandler#ignoreTable(String)} 阶段
 * 即已放行，不受策略影响。
 * </p>
 *
 * @since 4.1.1
 */
public enum TenantMissingStrategyEnum {

    /**
     * 回退默认租户（{@code fun.mp.tenant.default-tenant-id}）
     * <p>
     * 行为宽松，适合「平台级数据 + 业务租户数据」共存、且无登录态调用需落默认租户的场景。
     * 代价是漏注入租户号时不会报错，数据静默落到默认租户。
     * </p>
     */
    DEFAULT,

    /**
     * 直接抛异常拒绝执行
     * <p>
     * fail fast，适合要求「任何隔离表访问都必须有明确租户」的严格场景。漏注入租户号时立即暴露，
     * 避免脏数据与越权读写。无登录态的系统调用须显式 {@code ContextHolder.setTenantId(...)}
     * 或加 {@code @IgnoreTenant}。
     * </p>
     */
    REJECT,
    ;
}
