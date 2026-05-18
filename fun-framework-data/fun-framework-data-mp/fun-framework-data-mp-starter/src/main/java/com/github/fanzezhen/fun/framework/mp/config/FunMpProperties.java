package com.github.fanzezhen.fun.framework.mp.config;

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
 *       ignore-tenant-tables:
 *         - sys_config
 *         - sys_dict
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
         * 默认值：false
         */
        boolean enabled = false;

        /**
         * 忽略多租户拦截的表名集合
         * <p>
         * 配置的表不会自动添加租户ID过滤条件，适用于全局配置表、字典表等不需要租户隔离的表。
         */
        private Set<String> ignoreTenantTables = Collections.emptySet();
    }
}
