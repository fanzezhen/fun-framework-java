package com.github.fanzezhen.fun.framework.mp.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.github.fanzezhen.fun.framework.mp.tenant.DefaultTenantLineHandler;
import com.github.fanzezhen.fun.framework.mp.tenant.TenantColumnCache;
import com.github.fanzezhen.fun.framework.mp.tenant.TenantIgnoreAspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

/**
 * 多租户能力装配
 * <p>
 * <b>总开关</b>：类级 {@code @ConditionalOnProperty(fun.mp.tenant.enabled=true)}，<b>默认关闭</b>。
 * 存量非多租户项目不显式开启即不注册任何租户 Bean，行为与引入本能力前一致。
 * </p>
 * <p>
 * <b>装配方式</b>：与同包其他配置一致，靠 {@link FunMpAutoConfiguration} 的组件扫描生效。
 * </p>
 * <p>
 * <b>拦截器顺序</b>：{@link TenantLineInnerInterceptor} 标 {@code @Order(0)}，由
 * {@link FunMpInterceptorAutoConfiguration} 收编进主 {@code MybatisPlusInterceptor} 时排在
 * 无 {@code @Order} 的分页拦截器之前——保证分页的 count 查询也带租户条件，不会越权计数。
 * </p>
 * <p>
 * 各 Bean 均标 {@code @ConditionalOnMissingBean}，业务侧自定义同类型 Bean 即可覆盖。
 * </p>
 *
 * @since 4.1.1
 */
@Configuration
@ConditionalOnProperty(prefix = "fun.mp.tenant", name = "enabled", havingValue = "true")
public class FunMpTenantConfiguration {

    /**
     * 含租户列的表名缓存
     * <p>
     * 用 {@link ObjectProvider#getIfUnique()} 取数据源：多数据源场景下无唯一候选时传
     * {@code null}，缓存自行退化为「全表隔离」并告警，而不是让整个租户装配失败。
     * 扫描在首次判定时惰性触发，避免早于 Flyway / Liquibase 建表。
     * </p>
     *
     * @param funMpProperties    MyBatis-Plus 增强配置
     * @param dataSourceProvider 数据源提供者
     * @return 列结构缓存
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantColumnCache tenantColumnCache(final FunMpProperties funMpProperties,
                                               final ObjectProvider<DataSource> dataSourceProvider) {
        FunMpProperties.Tenant tenant = funMpProperties.getTenant();
        return new TenantColumnCache(dataSourceProvider.getIfUnique(), tenant.getColumn(),
                tenant.isColumnScanEnabled());
    }

    /**
     * 租户行处理器（三级判定隔离范围）
     *
     * @param funMpProperties   MyBatis-Plus 增强配置
     * @param tenantColumnCache 列结构缓存
     * @return 租户行处理器
     */
    @Bean
    @ConditionalOnMissingBean(TenantLineHandler.class)
    public TenantLineHandler tenantLineHandler(final FunMpProperties funMpProperties,
                                               final TenantColumnCache tenantColumnCache) {
        return new DefaultTenantLineHandler(tenantColumnCache, funMpProperties.getTenant());
    }

    /**
     * 租户 SQL 拦截器
     *
     * @param tenantLineHandler 租户行处理器
     * @return 租户内部拦截器
     */
    @Bean
    @Order(0)
    @ConditionalOnMissingBean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(final TenantLineHandler tenantLineHandler) {
        return new TenantLineInnerInterceptor(tenantLineHandler);
    }

    /**
     * {@code @IgnoreTenant} 逃生口切面
     * <p>
     * 子开关 {@code fun.mp.tenant.aspect-enabled} 默认开，置 {@code false} 可单独关闭切面。
     * </p>
     *
     * @return 逃生口切面
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "fun.mp.tenant", name = "aspect-enabled", matchIfMissing = true)
    public TenantIgnoreAspect tenantIgnoreAspect() {
        return new TenantIgnoreAspect();
    }
}
