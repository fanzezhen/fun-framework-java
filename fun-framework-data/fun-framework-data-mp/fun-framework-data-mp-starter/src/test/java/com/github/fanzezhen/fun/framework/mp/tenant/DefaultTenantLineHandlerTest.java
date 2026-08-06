package com.github.fanzezhen.fun.framework.mp.tenant;

import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.mp.config.FunMpProperties;
import com.github.fanzezhen.fun.framework.mp.enums.FunDataMpExceptionEnum;
import com.github.fanzezhen.fun.framework.mp.tenant.enums.TenantMissingStrategyEnum;
import com.github.fanzezhen.fun.framework.mp.tenant.enums.TenantValueTypeEnum;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 默认租户行处理器测试
 *
 * @since 4.1.1
 */
@DisplayName("DefaultTenantLineHandler 默认租户行处理器")
class DefaultTenantLineHandlerTest {

    @AfterEach
    void tearDown() {
        ContextHolder.clean();
        TenantIgnoreContext.clear();
    }

    /**
     * 造一份默认多租户配置
     *
     * @return 多租户配置
     */
    private FunMpProperties.Tenant tenant() {
        FunMpProperties.Tenant tenant = new FunMpProperties.Tenant();
        tenant.setEnabled(true);
        return tenant;
    }

    /**
     * 造一个含 biz_order（有租户列）与 sys_dict（无租户列）的列缓存
     *
     * @return 列缓存
     * @throws SQLException 执行 DDL 失败
     */
    private TenantColumnCache cacheWithTenantTable() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:handler_" + UUID.randomUUID().toString().replace("-", "")
                + ";MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE biz_order (id BIGINT PRIMARY KEY, tenant_id BIGINT)");
            statement.execute("CREATE TABLE sys_dict (id BIGINT PRIMARY KEY, dict_key VARCHAR(64))");
            statement.execute("CREATE TABLE sys_tenant_permission (id BIGINT PRIMARY KEY, tenant_id BIGINT)");
        }
        return new TenantColumnCache(dataSource, "tenant_id", true);
    }

    /**
     * 造一个不可用的列缓存（无数据源）
     *
     * @return 列缓存
     */
    private TenantColumnCache unavailableCache() {
        return new TenantColumnCache(null, "tenant_id", true);
    }

    @Test
    @DisplayName("getTenantId_有上下文且类型string_拼字符串字面量")
    void getTenantId_StringType_ShouldReturnStringValue() {
        FunMpProperties.Tenant tenant = tenant();
        ContextHolder.setTenantId("1001");
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(unavailableCache(), tenant);

        Expression expression = handler.getTenantId();

        assertAll(
                () -> assertInstanceOf(StringValue.class, expression),
                () -> assertEquals("'1001'", expression.toString())
        );
    }

    @Test
    @DisplayName("getTenantId_有上下文且类型long_拼数值字面量")
    void getTenantId_LongType_ShouldReturnLongValue() {
        FunMpProperties.Tenant tenant = tenant();
        tenant.setValueType(TenantValueTypeEnum.LONG);
        ContextHolder.setTenantId("1001");
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(unavailableCache(), tenant);

        Expression expression = handler.getTenantId();

        assertAll(
                () -> assertInstanceOf(LongValue.class, expression),
                () -> assertEquals("1001", expression.toString())
        );
    }

    @Test
    @DisplayName("getTenantId_类型long但租户号非数值_抛业务异常")
    void getTenantId_LongTypeWithNonNumericId_ShouldThrow() {
        FunMpProperties.Tenant tenant = tenant();
        tenant.setValueType(TenantValueTypeEnum.LONG);
        ContextHolder.setTenantId("a3f-uuid");
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(unavailableCache(), tenant);

        ServiceException exception = assertThrows(ServiceException.class, handler::getTenantId);

        assertEquals(FunDataMpExceptionEnum.TENANT_ID_NOT_NUMERIC.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("getTenantId_无上下文且策略default_回退默认租户")
    void getTenantId_MissingWithDefaultStrategy_ShouldFallbackToDefault() {
        FunMpProperties.Tenant tenant = tenant();
        tenant.setDefaultTenantId("0");
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(unavailableCache(), tenant);

        Expression expression = handler.getTenantId();

        assertEquals("'0'", expression.toString());
    }

    @Test
    @DisplayName("getTenantId_无上下文且策略reject_抛业务异常")
    void getTenantId_MissingWithRejectStrategy_ShouldThrow() {
        FunMpProperties.Tenant tenant = tenant();
        tenant.setMissingStrategy(TenantMissingStrategyEnum.REJECT);
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(unavailableCache(), tenant);

        ServiceException exception = assertThrows(ServiceException.class, handler::getTenantId);

        assertEquals(FunDataMpExceptionEnum.TENANT_CONTEXT_MISSING.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("getTenantId_上下文为空白串_按缺失处置")
    void getTenantId_BlankTenantId_ShouldTreatAsMissing() {
        FunMpProperties.Tenant tenant = tenant();
        tenant.setMissingStrategy(TenantMissingStrategyEnum.REJECT);
        ContextHolder.setTenantId("   ");
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(unavailableCache(), tenant);

        assertThrows(ServiceException.class, handler::getTenantId);
    }

    @Test
    @DisplayName("ignoreTable_逃生口开启_所有表放行")
    void ignoreTable_IgnoreContextOn_ShouldIgnoreAll() throws SQLException {
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(cacheWithTenantTable(), tenant());
        TenantIgnoreContext.set(true);

        assertAll(
                () -> assertTrue(handler.ignoreTable("biz_order")),
                () -> assertTrue(handler.ignoreTable("sys_dict"))
        );
    }

    @Test
    @DisplayName("ignoreTable_配置的例外表_放行且大小写不敏感")
    void ignoreTable_ConfiguredIgnoreTable_ShouldIgnoreIgnoringCase() throws SQLException {
        FunMpProperties.Tenant tenant = tenant();
        tenant.setIgnoreTenantTables(Set.of("sys_tenant_permission"));
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(cacheWithTenantTable(), tenant);

        assertAll(
                () -> assertTrue(handler.ignoreTable("sys_tenant_permission")),
                () -> assertTrue(handler.ignoreTable("SYS_TENANT_PERMISSION")),
                () -> assertFalse(handler.ignoreTable("biz_order"))
        );
    }

    @Test
    @DisplayName("ignoreTable_例外表未配置_按列缓存判定")
    void ignoreTable_ByColumnCache_ShouldIsolateOnlyTenantTables() throws SQLException {
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(cacheWithTenantTable(), tenant());

        assertAll(
                () -> assertFalse(handler.ignoreTable("biz_order"), "含租户列的表须隔离"),
                () -> assertTrue(handler.ignoreTable("sys_dict"), "无租户列的全局表须放行")
        );
    }

    @Test
    @DisplayName("ignoreTable_列缓存不可用_退化为全表隔离")
    void ignoreTable_CacheUnavailable_ShouldIsolateAllTables() {
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(unavailableCache(), tenant());

        assertAll(
                () -> assertFalse(handler.ignoreTable("biz_order")),
                () -> assertFalse(handler.ignoreTable("sys_dict"), "扫描不可用时宁可报错也不放行，避免跨租户泄漏")
        );
    }

    @Test
    @DisplayName("ignoreTable_例外表清单为null_不抛异常")
    void ignoreTable_NullIgnoreTables_ShouldNotThrow() {
        FunMpProperties.Tenant tenant = tenant();
        tenant.setIgnoreTenantTables(null);
        DefaultTenantLineHandler handler = new DefaultTenantLineHandler(unavailableCache(), tenant);

        assertFalse(handler.ignoreTable("biz_order"));
    }

    @Test
    @DisplayName("getTenantIdColumn_默认与自定义_均取配置值")
    void getTenantIdColumn_ShouldReturnConfiguredColumn() {
        FunMpProperties.Tenant defaultTenant = tenant();
        FunMpProperties.Tenant customTenant = tenant();
        customTenant.setColumn("org_id");

        assertAll(
                () -> assertEquals("tenant_id",
                        new DefaultTenantLineHandler(unavailableCache(), defaultTenant).getTenantIdColumn()),
                () -> assertEquals("org_id",
                        new DefaultTenantLineHandler(unavailableCache(), customTenant).getTenantIdColumn())
        );
    }
}
