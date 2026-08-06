package com.github.fanzezhen.fun.framework.mp.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.github.fanzezhen.fun.framework.mp.tenant.DefaultTenantLineHandler;
import com.github.fanzezhen.fun.framework.mp.tenant.TenantColumnCache;
import com.github.fanzezhen.fun.framework.mp.tenant.TenantIgnoreAspect;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 多租户装配测试
 *
 * @since 4.1.1
 */
@DisplayName("FunMpTenantConfiguration 多租户装配")
class FunMpTenantConfigurationTest {

    /**
     * 装配运行器：租户配置 + 属性绑定 + H2 数据源
     */
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FunMpTenantConfiguration.class))
            .withUserConfiguration(TenantPropertiesConfig.class, DataSourceConfig.class);

    @Test
    @DisplayName("装配_未开启总开关_不注册任何租户Bean")
    void assemble_DisabledByDefault_ShouldRegisterNothing() {
        runner.run(context -> assertThat(context)
                .doesNotHaveBean(TenantColumnCache.class)
                .doesNotHaveBean(TenantLineHandler.class)
                .doesNotHaveBean(TenantLineInnerInterceptor.class)
                .doesNotHaveBean(TenantIgnoreAspect.class));
    }

    @Test
    @DisplayName("装配_开启总开关_注册全套租户Bean")
    void assemble_Enabled_ShouldRegisterAllBeans() {
        runner.withPropertyValues("fun.mp.tenant.enabled=true")
                .run(context -> assertThat(context)
                        .hasSingleBean(TenantColumnCache.class)
                        .hasSingleBean(TenantLineHandler.class)
                        .hasSingleBean(TenantLineInnerInterceptor.class)
                        .hasSingleBean(TenantIgnoreAspect.class));
    }

    @Test
    @DisplayName("装配_关闭切面子开关_只不注册切面")
    void assemble_AspectDisabled_ShouldSkipAspectOnly() {
        runner.withPropertyValues("fun.mp.tenant.enabled=true", "fun.mp.tenant.aspect-enabled=false")
                .run(context -> assertThat(context)
                        .hasSingleBean(TenantLineInnerInterceptor.class)
                        .doesNotHaveBean(TenantIgnoreAspect.class));
    }

    @Test
    @DisplayName("装配_业务侧自定义行处理器_覆盖框架默认")
    void assemble_CustomHandler_ShouldOverrideDefault() {
        runner.withPropertyValues("fun.mp.tenant.enabled=true")
                .withUserConfiguration(CustomHandlerConfig.class)
                .run(context -> assertThat(context)
                        .hasSingleBean(TenantLineHandler.class)
                        .getBean(TenantLineHandler.class)
                        .isNotInstanceOf(DefaultTenantLineHandler.class));
    }

    @Test
    @DisplayName("装配_属性绑定_值类型与默认租户生效")
    void assemble_PropertyBinding_ShouldApplyValueTypeAndDefaultTenant() {
        runner.withPropertyValues("fun.mp.tenant.enabled=true",
                        "fun.mp.tenant.value-type=long",
                        "fun.mp.tenant.default-tenant-id=88",
                        "fun.mp.tenant.column=org_id")
                .run(context -> {
                    TenantLineHandler handler = context.getBean(TenantLineHandler.class);
                    assertThat(handler.getTenantIdColumn()).isEqualTo("org_id");
                    // 无租户上下文，按默认策略回退默认租户，且以数值字面量拼接
                    assertThat(handler.getTenantId()).isInstanceOf(LongValue.class)
                            .hasToString("88");
                });
    }

    /**
     * 属性绑定配置
     * <p>
     * 生产中由 {@code FunMpAutoConfiguration} 注册，测试里单独开以免拉起整个 MyBatis-Plus 装配。
     * </p>
     */
    @EnableConfigurationProperties(FunMpProperties.class)
    static class TenantPropertiesConfig {
    }

    /**
     * H2 数据源配置，供列结构扫描使用
     * <p>
     * 刻意不标 {@code @Configuration}：{@code FunMpAutoConfiguration} 对整个 mp 包做组件扫描，
     * 标注后会被其他测试的上下文一并扫入，与自动装配的数据源冲突。
     * </p>
     */
    static class DataSourceConfig {

        /**
         * 建一个含租户表与全局表的内存库
         *
         * @return 数据源
         */
        @Bean
        public DataSource dataSource() {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setUrl("jdbc:h2:mem:assemble_" + UUID.randomUUID().toString().replace("-", "")
                    + ";MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false"
                    + ";INIT=CREATE TABLE biz_order(id BIGINT PRIMARY KEY, tenant_id BIGINT)");
            dataSource.setUser("sa");
            dataSource.setPassword("");
            return dataSource;
        }
    }

    /**
     * 业务侧自定义行处理器配置
     */
    static class CustomHandlerConfig {

        /**
         * 恒定租户号的自定义处理器
         *
         * @return 行处理器
         */
        @Bean
        public TenantLineHandler customTenantLineHandler() {
            return new TenantLineHandler() {
                @Override
                public Expression getTenantId() {
                    return new LongValue(1L);
                }
            };
        }
    }
}
