package com.github.fanzezhen.fun.framework.mp.example;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置示例
 * <p>
 * ⚠️ 这是一个示例配置类，展示了如何配置 MyBatis-Plus 拦截器。
 * 使用 fun-framework-data-mp-starter 时，必须在子项目中提供至少一个 InnerInterceptor Bean。
 * </p>
 *
 * <h3>使用方法</h3>
 * <ol>
 *   <li>复制此类到子项目的 config 包下</li>
 *   <li>根据实际数据库类型修改 {@link DbType}</li>
 *   <li>根据需要启用或禁用可选拦截器</li>
 * </ol>
 *
 * <h3>支持的数据库类型</h3>
 * <ul>
 *   <li>{@link DbType#MYSQL} - MySQL 5.x / 8.x</li>
 *   <li>{@link DbType#POSTGRE_SQL} - PostgreSQL 9.x+</li>
 *   <li>{@link DbType#ORACLE} - Oracle 11g+</li>
 *   <li>{@link DbType#SQL_SERVER} - SQL Server 2012+</li>
 *   <li>{@link DbType#H2} - H2 内存数据库（测试环境推荐）</li>
 *   <li>{@link DbType#SQLITE} - SQLite 3.x</li>
 *   <li>{@link DbType#MARIADB} - MariaDB 10.x+</li>
 * </ul>
 *
 * @author fanzezhen
 * @since 3.4.3.1
 * @see InnerInterceptor
 * @see PaginationInnerInterceptor
 */
@Configuration
public class MybatisPlusExampleConfig {

    /**
     * 分页插件（必需）
     * <p>
     * 提供 MyBatis-Plus 的分页查询能力，必须配置，否则分页功能不可用。
     * </p>
     *
     * @return 分页拦截器实例
     */
    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor interceptor = new PaginationInnerInterceptor(DbType.H2);

        // 可选配置：设置单页最大条数限制（默认 500，-1 表示不限制）
        interceptor.setMaxLimit(1000L);

        // 可选配置：溢出总页数后是否进行处理（默认 false）
        // true: 如果请求页码大于总页数，自动跳转到第一页
        // false: 如果请求页码大于总页数，继续请求（返回空数据）
        interceptor.setOverflow(false);

        return interceptor;
    }

    /**
     * 乐观锁插件（可选）
     * <p>
     * 当实体类中使用 @Version 注解时，自动处理乐观锁逻辑。
     * </p>
     *
     * <h4>使用示例</h4>
     * <pre>{@code
     * @Data
     * @TableName("user")
     * public class User {
     *     @TableId
     *     private Long id;
     *
     *     @Version
     *     private Integer version; // 版本号字段
     * }
     * }</pre>
     *
     * @return 乐观锁拦截器实例
     */
    @Bean
    public InnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 防止全表更新与删除插件（可选，生产环境强烈推荐）
     * <p>
     * 阻止恶意的全表更新/删除操作，当执行 UPDATE/DELETE 语句时，
     * 如果 WHERE 条件为空或永真条件（如 1=1），将抛出异常。
     * </p>
     *
     * <h4>阻止的 SQL 示例</h4>
     * <pre>{@code
     * DELETE FROM user;                    // ❌ 无 WHERE 条件
     * UPDATE user SET status = 0;          // ❌ 无 WHERE 条件
     * DELETE FROM user WHERE 1=1;          // ❌ 永真条件
     * }</pre>
     *
     * @return 防全表更新删除拦截器实例
     */
    @Bean
    public InnerInterceptor blockAttackInnerInterceptor() {
        return new BlockAttackInnerInterceptor();
    }

    /**
     * 多租户插件（可选，多租户系统使用）
     * <p>
     * 自动在 SQL 中添加租户条件，实现租户数据隔离。
     * 需要结合 TenantLineHandler 使用，详见框架租户配置文档。
     * </p>
     *
     * @return 多租户拦截器实例
     */
    // @Bean
    // public InnerInterceptor tenantLineInnerInterceptor() {
    //     TenantLineInnerInterceptor interceptor = new TenantLineInnerInterceptor();
    //     interceptor.setTenantLineHandler(new TenantLineHandler() {
    //         @Override
    //         public Expression getTenantId() {
    //             // 从上下文中获取当前租户 ID
    //             return new LongValue(TenantContextHolder.getTenantId());
    //         }
    //
    //         @Override
    //         public String getTenantIdColumn() {
    //             return "tenant_id"; // 租户字段名
    //         }
    //
    //         @Override
    //         public boolean ignoreTable(String tableName) {
    //             // 忽略不需要租户隔离的表
    //             return "sys_config".equalsIgnoreCase(tableName);
    //         }
    //     });
    //     return interceptor;
    // }
}
