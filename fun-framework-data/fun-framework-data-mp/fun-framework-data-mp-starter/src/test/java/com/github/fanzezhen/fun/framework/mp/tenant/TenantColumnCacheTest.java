package com.github.fanzezhen.fun.framework.mp.tenant;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 租户列结构缓存测试
 * <p>
 * 用 H2 内存库的真实 JDBC 元数据验证，不 mock {@code DatabaseMetaData}——扫描逻辑的价值恰在于
 * 应对各库元数据的大小写与 catalog/schema 差异，mock 掉就等于没测。
 * </p>
 *
 * @since 4.1.1
 */
@DisplayName("TenantColumnCache 租户列结构缓存")
class TenantColumnCacheTest {

    /**
     * 建库并执行 DDL
     * <p>
     * 每个用例独立库名，避免用例间状态残留。{@code DB_CLOSE_DELAY=-1} 保证连接关闭后库不消失。
     * </p>
     *
     * @param ddlList 建表语句
     * @return 数据源
     * @throws SQLException 执行 DDL 失败
     */
    private DataSource h2(final String... ddlList) throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:tenant_" + UUID.randomUUID().toString().replace("-", "")
                + ";MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            for (String ddl : ddlList) {
                statement.execute(ddl);
            }
        }
        return dataSource;
    }

    @Test
    @DisplayName("contains_含租户列的表_命中且大小写不敏感")
    void contains_TableWithTenantColumn_ShouldHitIgnoringCase() throws SQLException {
        DataSource dataSource = h2("CREATE TABLE biz_order (id BIGINT PRIMARY KEY, tenant_id BIGINT)");
        TenantColumnCache cache = new TenantColumnCache(dataSource, "tenant_id", true);

        assertAll(
                () -> assertTrue(cache.available()),
                () -> assertTrue(cache.contains("biz_order")),
                () -> assertTrue(cache.contains("BIZ_ORDER"), "表名判定应大小写不敏感"),
                () -> assertTrue(cache.contains("Biz_Order"))
        );
    }

    @Test
    @DisplayName("contains_无租户列的表_不命中")
    void contains_TableWithoutTenantColumn_ShouldMiss() throws SQLException {
        DataSource dataSource = h2(
                "CREATE TABLE biz_order (id BIGINT PRIMARY KEY, tenant_id BIGINT)",
                "CREATE TABLE sys_dict (id BIGINT PRIMARY KEY, dict_key VARCHAR(64))");
        TenantColumnCache cache = new TenantColumnCache(dataSource, "tenant_id", true);

        assertAll(
                () -> assertTrue(cache.contains("biz_order")),
                () -> assertFalse(cache.contains("sys_dict"), "全局表不应被隔离"),
                () -> assertFalse(cache.contains("not_exists_table"))
        );
    }

    @Test
    @DisplayName("contains_传null_返回false")
    void contains_NullTableName_ShouldReturnFalse() throws SQLException {
        DataSource dataSource = h2("CREATE TABLE biz_order (id BIGINT PRIMARY KEY, tenant_id BIGINT)");
        TenantColumnCache cache = new TenantColumnCache(dataSource, "tenant_id", true);

        assertFalse(cache.contains(null));
    }

    @Test
    @DisplayName("available_自定义列名_按配置列扫描")
    void available_CustomColumn_ShouldScanConfiguredColumn() throws SQLException {
        DataSource dataSource = h2("CREATE TABLE biz_order (id BIGINT PRIMARY KEY, org_id BIGINT)");
        TenantColumnCache matched = new TenantColumnCache(dataSource, "org_id", true);
        TenantColumnCache mismatched = new TenantColumnCache(dataSource, "tenant_id", true);

        assertAll(
                () -> assertTrue(matched.contains("biz_order")),
                () -> assertFalse(mismatched.available(), "配置列与实际列不一致时应判为不可用")
        );
    }

    @Test
    @DisplayName("available_库中无租户列_不可用以退化全表隔离")
    void available_NoTenantColumnInSchema_ShouldBeUnavailable() throws SQLException {
        DataSource dataSource = h2("CREATE TABLE sys_dict (id BIGINT PRIMARY KEY, dict_key VARCHAR(64))");
        TenantColumnCache cache = new TenantColumnCache(dataSource, "tenant_id", true);

        assertFalse(cache.available());
    }

    @Test
    @DisplayName("available_扫描关闭_不可用且不触碰数据源")
    void available_ScanDisabled_ShouldBeUnavailable() throws SQLException {
        DataSource dataSource = h2("CREATE TABLE biz_order (id BIGINT PRIMARY KEY, tenant_id BIGINT)");
        TenantColumnCache cache = new TenantColumnCache(dataSource, "tenant_id", false);

        assertAll(
                () -> assertFalse(cache.available()),
                () -> assertFalse(cache.contains("biz_order"))
        );
    }

    @Test
    @DisplayName("available_数据源为null_不可用且不抛异常")
    void available_NullDataSource_ShouldBeUnavailableWithoutThrowing() {
        TenantColumnCache cache = new TenantColumnCache(null, "tenant_id", true);

        assertAll(
                () -> assertFalse(cache.available()),
                () -> assertFalse(cache.contains("biz_order"))
        );
    }

    @Test
    @DisplayName("refresh_建表后重扫_能识别新表")
    void refresh_AfterDdl_ShouldPickUpNewTable() throws SQLException {
        DataSource dataSource = h2("CREATE TABLE biz_order (id BIGINT PRIMARY KEY, tenant_id BIGINT)");
        TenantColumnCache cache = new TenantColumnCache(dataSource, "tenant_id", true);
        boolean beforeDdl = cache.contains("biz_stock");

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE biz_stock (id BIGINT PRIMARY KEY, tenant_id BIGINT)");
        }
        cache.refresh();

        assertAll(
                () -> assertFalse(beforeDdl),
                () -> assertTrue(cache.contains("biz_stock")),
                () -> assertTrue(cache.contains("biz_order"), "重扫不应丢掉原有表")
        );
    }

    @Test
    @DisplayName("contains_懒加载_首次判定才扫库")
    void contains_LazyInit_ShouldScanOnFirstUse() throws SQLException {
        DataSource dataSource = h2("CREATE TABLE sys_dict (id BIGINT PRIMARY KEY, dict_key VARCHAR(64))");
        // 构造时库中还没有租户表，模拟 bean 早于 Flyway 建表的时序
        TenantColumnCache cache = new TenantColumnCache(dataSource, "tenant_id", true);
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE biz_order (id BIGINT PRIMARY KEY, tenant_id BIGINT)");
        }

        assertTrue(cache.contains("biz_order"), "构造期不扫库，首次判定时才扫，故能看到之后建的表");
    }
}
