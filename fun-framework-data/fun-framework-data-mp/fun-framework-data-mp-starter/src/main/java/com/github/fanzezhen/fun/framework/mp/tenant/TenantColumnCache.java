package com.github.fanzezhen.fun.framework.mp.tenant;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 含租户列的表名缓存，租户隔离范围的<b>主判据</b>
 * <p>
 * 扫描当前库的表结构，登记「含租户列」的表名（统一小写）。表在缓存内则 SQL 自动拼接租户条件；
 * 不在缓存内（全局配置表、字典表、无该列的存量表）则直接放行，不会误拼条件导致 SQL 报错。
 * 这使得同库中多租户表与全局表可以共存，无需人工维护 {@code ignore-tenant-tables} 清单。
 * </p>
 * <p>
 * <b>用 JDBC {@link DatabaseMetaData} 而非 {@code information_schema} 查询</b>：后者的
 * {@code SCHEMA()} 函数只在 MySQL / H2 可用，而本模块声明支持 Oracle、SQLite、PostgreSQL 等。
 * </p>
 * <p>
 * <b>懒加载而非构造期扫描</b>：构造期（bean 初始化）可能早于 Flyway / Liquibase 建表，那时扫描
 * 得到空集，之后所有表都判为「无租户列」。改为首次判定时扫描——此时任何 SQL 都已在迁移之后。
 * </p>
 * <p>
 * <b>扫描不可用时退化为「全表隔离」</b>：无唯一数据源、扫描异常或结果为空时
 * {@link #available()} 返回 {@code false}，调用方据此对所有表施加租户条件。宁可 SQL 报错
 * 暴露问题，也不能放行全部表——后者方向是跨租户数据泄漏。
 * </p>
 *
 * @since 4.1.1
 */
@Slf4j
public class TenantColumnCache {

    /**
     * 数据源，无唯一候选时为 {@code null}
     */
    private final DataSource dataSource;

    /**
     * 租户列名
     */
    private final String column;

    /**
     * 是否启用扫描
     */
    private final boolean scanEnabled;

    /**
     * 含租户列的表名（小写）
     */
    private final Set<String> tables = ConcurrentHashMap.newKeySet();

    /**
     * 是否已完成首次扫描
     */
    private volatile boolean initialized;

    /**
     * 构造列缓存
     *
     * @param dataSource  数据源，允许为 {@code null}（无唯一候选时退化为全表隔离）
     * @param column      租户列名
     * @param scanEnabled 是否启用扫描，{@code false} 时退化为全表隔离
     */
    public TenantColumnCache(final DataSource dataSource, final String column, final boolean scanEnabled) {
        this.dataSource = dataSource;
        this.column = column;
        this.scanEnabled = scanEnabled;
    }

    /**
     * 表是否含租户列（大小写不敏感）
     *
     * @param tableName 表名
     * @return {@code true}=需自动隔离；{@code null} 或未命中返回 {@code false}
     */
    public boolean contains(final String tableName) {
        ensureInitialized();
        return tableName != null && tables.contains(tableName.toLowerCase());
    }

    /**
     * 缓存是否可用
     * <p>
     * 扫描未启用、无数据源、扫描异常或结果为空时不可用，调用方应退化为「全表隔离」。
     * </p>
     *
     * @return {@code true}=可作为隔离范围的判据
     */
    public boolean available() {
        ensureInitialized();
        return !tables.isEmpty();
    }

    /**
     * 首次判定时触发扫描，双重检查避免并发重复扫库
     */
    private void ensureInitialized() {
        if (initialized) {
            return;
        }
        synchronized (this) {
            if (!initialized) {
                refresh();
            }
        }
    }

    /**
     * 重新扫描当前库中所有含租户列的表
     * <p>
     * DDL 变更后（如运行期新建租户表）可手动调用刷新。扫描失败不抛异常，只告警并让
     * {@link #available()} 返回 {@code false}，由调用方退化为全表隔离。
     * </p>
     */
    public synchronized void refresh() {
        initialized = true;
        tables.clear();
        if (!scanEnabled) {
            log.info("多租户列扫描已关闭（fun.mp.tenant.column-scan-enabled=false），所有表按需隔离，"
                    + "全局表请配置 fun.mp.tenant.ignore-tenant-tables");
            return;
        }
        if (dataSource == null) {
            log.warn("多租户列扫描跳过：容器中无唯一 DataSource 候选，所有表按需隔离，"
                    + "全局表请配置 fun.mp.tenant.ignore-tenant-tables");
            return;
        }
        try {
            scan();
        } catch (SQLException e) {
            log.warn("多租户列扫描失败，所有表按需隔离，全局表请配置 fun.mp.tenant.ignore-tenant-tables", e);
            tables.clear();
            return;
        }
        if (tables.isEmpty()) {
            log.warn("多租户已启用但未扫描到任何含 [{}] 列的表，所有表按需隔离。"
                    + "请核对租户列名配置（fun.mp.tenant.column）与建表脚本", column);
        } else {
            log.info("多租户列扫描完成，含 [{}] 列的表 {} 张：{}", column, tables.size(), tables);
        }
    }

    /**
     * 执行表结构扫描
     * <p>
     * 列名模式先按配置原值查，无命中再按大写重试——Oracle 等库的元数据以大写存储标识符，
     * 而 {@code getColumns} 的列名参数是区分大小写的模式串。
     * </p>
     *
     * @throws SQLException 取连接或读元数据失败
     */
    private void scan() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = currentSchema(connection);
            collect(metaData, catalog, schema, column);
            if (tables.isEmpty()) {
                collect(metaData, catalog, schema, column.toUpperCase());
            }
        }
    }

    /**
     * 按列名模式收集表名
     *
     * @param metaData      数据库元数据
     * @param catalog       目录名
     * @param schema        模式名
     * @param columnPattern 列名模式
     * @throws SQLException 读元数据失败
     */
    private void collect(final DatabaseMetaData metaData, final String catalog,
                         final String schema, final String columnPattern) throws SQLException {
        try (ResultSet resultSet = metaData.getColumns(catalog, schema, null, columnPattern)) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                if (CharSequenceUtil.isNotBlank(tableName)) {
                    tables.add(tableName.toLowerCase());
                }
            }
        }
    }

    /**
     * 取当前模式名
     * <p>
     * 部分驱动不实现 {@code getSchema()} 会抛异常或返回 null，此时传 {@code null} 交由驱动
     * 按默认模式解析。
     * </p>
     *
     * @param connection 数据库连接
     * @return 模式名，取不到时返回 {@code null}
     */
    private String currentSchema(final Connection connection) {
        try {
            return connection.getSchema();
        } catch (SQLException | RuntimeException e) {
            log.debug("驱动不支持 getSchema()，按默认模式扫描", e);
            return null;
        }
    }
}
