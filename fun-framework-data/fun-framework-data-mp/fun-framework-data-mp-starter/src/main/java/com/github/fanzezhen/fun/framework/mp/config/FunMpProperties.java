package com.github.fanzezhen.fun.framework.mp.config;

import com.github.fanzezhen.fun.framework.mp.tenant.FunTenantConstant;
import com.github.fanzezhen.fun.framework.mp.tenant.enums.TenantMissingStrategyEnum;
import com.github.fanzezhen.fun.framework.mp.tenant.enums.TenantValueTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
 * MyBatis-Plus 配置属性
 * <p>
 * 通过配置文件（application.yml/properties）管理 MyBatis-Plus 增强功能的配置。
 * <p>
 * 配置前缀：{@code fun.mp}
 * <p>
 * 配置示例：
 * <pre>{@code
 * fun:
 *   mp:
 *     tenant:
 *       enabled: true
 *       # 租户列为 Integer/Long 时配 long，String 时保持默认 string
 *       value-type: long
 *       # 上下文缺失：default 回退默认租户，reject 抛异常
 *       missing-strategy: default
 *       default-tenant-id: 0
 *       # 有租户列但仍需跨租户访问的例外表
 *       ignore-tenant-tables:
 *         - sys_tenant_permission
 * }</pre>
 */
@Data
@ConfigurationProperties(prefix = "fun.mp")
public class FunMpProperties {
    /**
     * 多租户配置
     */
    private Tenant tenant = new Tenant();

    /**
     * 多租户配置类
     */
    @Data
    public static class Tenant {
        /**
         * 是否启用多租户功能
         * <p>
         * 默认值：false。存量非多租户项目不显式开启即不注册任何租户 Bean，行为与引入本能力前一致。
         */
        boolean enabled = false;

        /**
         * 忽略多租户拦截的表名集合
         * <p>
         * 配置的表不会自动添加租户ID过滤条件（大小写不敏感）。
         * <p>
         * 开启 {@link #columnScanEnabled} 后，无租户列的全局表已自动放行，本项只需列
         * 「有租户列但仍需跨租户访问」的例外表，如租户授权中间表。
         */
        private Set<String> ignoreTenantTables = Collections.emptySet();

        /**
         * 租户隔离列名
         * <p>
         * 默认 {@link FunTenantConstant#TENANT_COLUMN}（{@code tenant_id}），
         * 同时作用于 SQL 条件拼接与列结构扫描。
         *
         * @since 4.1.1
         */
        private String column = FunTenantConstant.TENANT_COLUMN;

        /**
         * 租户ID拼进SQL的字面量类型
         * <p>
         * 默认 {@link TenantValueTypeEnum#STRING}。租户列为 Integer / Long 时应配
         * {@code long}，否则字符串字面量会触发隐式类型转换导致索引失效。
         *
         * @since 4.1.1
         */
        private TenantValueTypeEnum valueType = TenantValueTypeEnum.STRING;

        /**
         * 租户上下文缺失时的处置策略
         * <p>
         * 默认 {@link TenantMissingStrategyEnum#DEFAULT}（回退默认租户）；
         * 严格场景配 {@code reject} 改为抛异常。
         *
         * @since 4.1.1
         */
        private TenantMissingStrategyEnum missingStrategy = TenantMissingStrategyEnum.DEFAULT;

        /**
         * 默认租户ID
         * <p>
         * 仅 {@link TenantMissingStrategyEnum#DEFAULT} 策略下生效，默认
         * {@link FunTenantConstant#DEFAULT_TENANT_ID}（{@code 0}），承载平台级 / 历史无租户数据。
         *
         * @since 4.1.1
         */
        private String defaultTenantId = FunTenantConstant.DEFAULT_TENANT_ID;

        /**
         * 是否启用 {@code @IgnoreTenant} 逃生口切面
         * <p>
         * 默认 true。置 false 可单独关闭切面而不影响其余租户 Bean。
         *
         * @since 4.1.1
         */
        private boolean aspectEnabled = true;

        /**
         * 是否启用租户列结构扫描
         * <p>
         * 默认 true：扫描当前库，只对「含租户列」的表施加隔离，全局表自动放行。
         * 置 false 或扫描不可用时退化为「除例外表外全表隔离」。
         *
         * @since 4.1.1
         */
        private boolean columnScanEnabled = true;
    }
}
