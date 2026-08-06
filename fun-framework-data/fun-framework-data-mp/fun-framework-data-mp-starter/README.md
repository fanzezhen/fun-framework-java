fun-framework-data-mp-starter
------------------------------------------
错误码格式 121**
Mybatis-plus配置模块

# 快速开始

## 1. 添加依赖

```xml
<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-data-mp-starter</artifactId>
</dependency>
```

## 2. ⚠️ 必需配置（重要！）

> **⚠️ 注意**: 使用本 starter 时，**必须**在项目中提供 `InnerInterceptor` Bean 配置，否则**分页功能将不可用**。

### 最小化配置示例

在项目的任意 `@Configuration` 类中添加以下 Bean：

```java
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件配置（必需）
     * 根据实际使用的数据库类型选择对应的 DbType
     */
    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor(DbType.MYSQL); // 根据数据库类型调整
    }
}
```

### 支持的数据库类型

| 数据库       | DbType 常量           | 说明                    |
|-------------|----------------------|------------------------|
| MySQL       | `DbType.MYSQL`       | MySQL 5.x / 8.x        |
| PostgreSQL  | `DbType.POSTGRE_SQL` | PostgreSQL 9.x+        |
| Oracle      | `DbType.ORACLE`      | Oracle 11g+            |
| SQL Server  | `DbType.SQL_SERVER`  | SQL Server 2012+       |
| H2          | `DbType.H2`          | H2 内存数据库（常用于测试）|
| SQLite      | `DbType.SQLITE`      | SQLite 3.x             |
| MariaDB     | `DbType.MARIADB`     | MariaDB 10.x+          |

### 进阶配置（可选）

如果需要添加多个拦截器（如乐观锁、租户隔离等），可以注册多个 `InnerInterceptor` Bean：

```java
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件（必需）
     */
    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor interceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 可选：设置单页最大条数限制（默认 500）
        interceptor.setMaxLimit(1000L);
        return interceptor;
    }

    /**
     * 乐观锁插件（可选）
     */
    @Bean
    public InnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 防止全表更新与删除插件（可选，生产环境推荐）
     */
    @Bean
    public InnerInterceptor blockAttackInnerInterceptor() {
        return new BlockAttackInnerInterceptor();
    }
}
```

> 💡 **提示**: 框架会自动扫描所有 `InnerInterceptor` Bean 并注册到 `MybatisPlusInterceptor` 中，无需手动配置拦截器链。

## 3. 启用自动配置

```java
package demo;

import com.github.fanzezhen.fun.framework.mp.EnableFunMpAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFunMpAutoConfiguration
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
```
# 功能介绍

## 📝 完整配置示例

完整的配置示例（包含所有可选拦截器）请参考：  
[MybatisPlusExampleConfig.java](src/test/java/com/github/fanzezhen/fun/framework/mp/example/MybatisPlusExampleConfig.java)

---

## [base.entity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity)：基础实体类
按主键策略分三套，路径为 `base/entity/{increment,snowflake,uuid}/`，主键类型依次为 Integer、Long、String。
下表以 `snowflake` 为例：

1. [BaseEntity](src/main/java/com/github/fanzezhen/fun/framework/mp/base/entity/snowflake/BaseEntity.java)：基础实体模型，包含 主键、创建时间、创建人 字段
2. [BaseGenericEntity](src/main/java/com/github/fanzezhen/fun/framework/mp/base/entity/snowflake/BaseGenericEntity.java)：常规实体模型，继承自 `BaseEntity`，新增 删除标识、更新时间、更新人 字段
3. [BaseTenantEntity](src/main/java/com/github/fanzezhen/fun/framework/mp/base/entity/snowflake/tenant/BaseTenantEntity.java)：租户基础实体模型，继承自 `BaseEntity`，新增 租户id 字段（类型同主键）
4. [BaseTenantGenericEntity](src/main/java/com/github/fanzezhen/fun/framework/mp/base/entity/snowflake/tenant/BaseTenantGenericEntity.java)：租户常规实体模型，继承自 `BaseTenantEntity`，新增 删除标识、更新时间、更新人 字段
5. `BaseGenericEntity<P>`：主键类型自定义时用泛型版
## [tenant](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Ftenant)：多租户数据隔离
总开关 `fun.mp.tenant.enabled`，默认关闭。用法见 FAQ [多租户模式下如何配置](#4-多租户模式下如何配置)。

1. `DefaultTenantLineHandler`：三级判定隔离范围（逃生口 → 例外表 → 租户列缓存）
2. `TenantColumnCache`：扫描库表结构，登记含租户列的表，全局表自动放行
3. `@IgnoreTenant` + `TenantIgnoreAspect`：方法级 / 类级跨租户逃生口
4. `AbstractTenantContextInterceptor`：请求入口拦截器骨架，业务侧实现 `resolveTenantId` 即可
5. 租户号由 `ContextHolder` 承载，网关透传 `fun-tenant-id` 请求头即自动生效

## [generator](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fgenerator)：代码生成器
示例：[GeneratorTest.java](src%2Ftest%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2FGeneratorTest.java)

---

## ❓ 常见问题 (FAQ)

### 1. 分页查询返回空数据或不生效 ⭐

**症状**: 
- 调用分页查询接口返回空列表
- 日志显示 SQL 未添加 `LIMIT` 分页条件
- 控制台报错: `Pagination not available, please add PaginationInnerInterceptor`

**原因**: 未配置分页拦截器 `PaginationInnerInterceptor`

**解决方案**: 参考上方 [必需配置](#2-⚠️-必需配置重要) 章节添加配置

---

### 2. 如何动态选择数据库类型 (DbType)

**场景**: 开发环境使用 H2，生产环境使用 MySQL，希望自动切换

**方案**: 根据 Spring 配置动态选择

```java
@Configuration
public class MybatisPlusConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        DbType dbType = getDbType(driverClassName);
        return new PaginationInnerInterceptor(dbType);
    }

    private DbType getDbType(String driverClassName) {
        if (driverClassName.contains("mysql")) {
            return DbType.MYSQL;
        } else if (driverClassName.contains("postgresql")) {
            return DbType.POSTGRE_SQL;
        } else if (driverClassName.contains("h2")) {
            return DbType.H2;
        }
        // 默认返回 MySQL
        return DbType.MYSQL;
    }
}
```

**常用驱动类名对应关系**:

| 数据库       | 驱动类名包含关键字         | DbType 常量           |
|-------------|--------------------------|----------------------|
| MySQL       | `mysql`                  | `DbType.MYSQL`       |
| PostgreSQL  | `postgresql`             | `DbType.POSTGRE_SQL` |
| Oracle      | `oracle`                 | `DbType.ORACLE`      |
| SQL Server  | `sqlserver`              | `DbType.SQL_SERVER`  |
| H2          | `h2`                     | `DbType.H2`          |

---

### 3. BaseEntity 和 BaseGenericEntity 的区别

| 类型                  | 字段                                      | 适用场景                          |
|----------------------|-------------------------------------------|----------------------------------|
| **BaseEntity**       | id, createTime, createUserId             | 只需基础字段，不需要更新追踪       |
| **BaseGenericEntity**| 继承 BaseEntity + delFlag, updateTime, updateUserId | 需要逻辑删除和更新追踪（推荐）    |

**示例**:

```java
// 使用 BaseEntity（简化版）
import com.github.fanzezhen.fun.framework.mp.base.entity.increment.BaseEntity;

@TableName("sys_log")
public class LogEntity extends BaseEntity {
    private String message; // 只需记录创建信息，不需要更新
}

// 使用 BaseGenericEntity（完整版）
import com.github.fanzezhen.fun.framework.mp.base.entity.increment.BaseGenericEntity;

@TableName("sys_user")
public class UserEntity extends BaseGenericEntity<Integer> {
    private String username; // 需要更新追踪和逻辑删除
}
```

---

### 4. 多租户模式下如何配置

框架已内置整套租户隔离，无需自行实现 `TenantLineHandler`。

**步骤 1**: 实体类继承多租户 BaseEntity

```java
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.tenant.BaseTenantEntity;

@TableName("tenant_order")
public class OrderEntity extends BaseTenantEntity {
    private String orderNo;
    // tenantId 字段已在 BaseTenantEntity 中定义
}
```

**步骤 2**: 开启开关

```yaml
fun:
  mp:
    tenant:
      enabled: true          # 总开关，默认关闭
      value-type: long       # 租户列为 Integer/Long 时配 long；String 列保持默认 string
      missing-strategy: default  # 无租户上下文时：default 回退默认租户，reject 抛异常
      default-tenant-id: 0
      ignore-tenant-tables:  # 有租户列但仍需跨租户访问的例外表
        - sys_tenant_permission
```

**步骤 3**: 请求入口注入租户号

租户号由 `ContextHolder` 承载。若网关已透传 `fun-tenant-id` 请求头，`FunContextFilter` 会自动写入，
无需额外代码。需要从登录态取租户号时，继承 `AbstractTenantContextInterceptor`：

```java
public class MyTenantInterceptor extends AbstractTenantContextInterceptor {
    public MyTenantInterceptor(FunMpProperties properties) {
        super(properties);
    }

    @Override
    protected String resolveTenantId(HttpServletRequest request) {
        // 从自己的登录态取，返回 null 则兜底为 default-tenant-id
        return LoginHelper.isLogin() ? LoginHelper.getTenantId() : null;
    }
}

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private FunMpProperties funMpProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyTenantInterceptor(funMpProperties))
                // 登录接口需跨租户按账号查用户，排除掉并在其 service 上加 @IgnoreTenant
                .excludePathPatterns("/auth/login");
    }
}
```

#### 隔离范围如何判定

按优先级三级判定，命中即返回：

| 优先级 | 判据 | 效果 |
|-------|------|------|
| 1 | `@IgnoreTenant` 逃生口 | 整块跳过租户条件 |
| 2 | `ignore-tenant-tables` 例外表 | 该表放行（大小写不敏感） |
| 3 | 表是否含租户列（主判据） | 含则隔离，不含则放行 |

第 3 级靠启动后首次查询时扫描库表结构得出，故**全局配置表、字典表无需手工配进例外清单**。
扫描不可用时（`column-scan-enabled=false`、无唯一 `DataSource`、扫描异常、库中无任何租户列）
退化为「除例外表外全表隔离」并输出告警——宁可 SQL 报错，也不放行全部表造成跨租户泄漏。

#### 跨租户操作

```java
@IgnoreTenant  // 也可标在类上，使类内所有方法跨租户
public UserEntity getByAccount(String account) {
    return userMapper.selectOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getAccount, account));
}
```

与 MyBatis-Plus 自带 `@InterceptorIgnore(tenantLine = "true")` 的分工：后者只作用于 mapper 接口与
其方法，`@IgnoreTenant` 作用于任意 Spring bean 方法，适合「某个 service 流程整体跨租户」。

> ⚠️ 跨租户**写入**时租户列不会自动补值，须显式 `setTenantId(目标租户)`，否则该列落库为 null。

#### 完整配置项

| 配置项 | 默认值 | 说明 |
|-------|-------|------|
| `fun.mp.tenant.enabled` | `false` | 总开关 |
| `fun.mp.tenant.column` | `tenant_id` | 租户列名，同时作用于条件拼接与列扫描 |
| `fun.mp.tenant.value-type` | `string` | `string` / `long`，决定拼 `'1001'` 还是 `1001` |
| `fun.mp.tenant.missing-strategy` | `default` | `default` 回退默认租户 / `reject` 抛异常 |
| `fun.mp.tenant.default-tenant-id` | `0` | 默认租户，承载平台级与历史无租户数据 |
| `fun.mp.tenant.ignore-tenant-tables` | 空 | 有租户列但需跨租户访问的例外表 |
| `fun.mp.tenant.aspect-enabled` | `true` | `@IgnoreTenant` 切面开关 |
| `fun.mp.tenant.column-scan-enabled` | `true` | 租户列结构扫描开关 |

> 租户列为数值类型时务必配 `value-type: long`。用字符串字面量比数值列会触发隐式类型转换，导致索引失效。

---

### 5. 拦截器（分页 / 租户）配置了却不生效

**症状**: 分页返回全量数据，或多租户已开启但 SQL 里没有租户条件；同一份配置有时又正常。

**原因**: `FunMpInterceptorAutoConfiguration` 曾用 `@ConditionalOnBean` 控制装配。该注解在组件扫描期
求值，那时同批次的 `MybatisPlusInterceptor` Bean 定义尚未注册，配置类会被静默跳过，
内部拦截器的收编随之不发生——表现为「时灵时不灵」。

**解决方案**: 已修复（改为 `ObjectProvider` 注入，配置类恒装配）。升级到含该修复的版本即可。
自查方式：注入 `MybatisPlusInterceptor` 后打印 `getInterceptors()`，应能看到自己注册的拦截器。

> 若确实未提供任何 `InnerInterceptor` Bean，启动日志会有醒目告警，参考
> [必需配置](#2-⚠️-必需配置重要) 章节添加分页插件。

---

### 6. 如何在不同环境使用不同配置

**方案 1**: 使用 `@ConditionalOnProperty` 控制

```java
@Configuration
public class MybatisPlusConfig {

    @Bean
    @ConditionalOnProperty(name = "mybatis-plus.interceptor.pagination.enabled", havingValue = "true", matchIfMissing = true)
    public InnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor(DbType.MYSQL);
    }
}
```

在 `application.yml` 中禁用：
```yaml
mybatis-plus:
  interceptor:
    pagination:
      enabled: false # 禁用分页插件
```

**方案 2**: 使用 Profile 区分环境

```java
// 生产环境配置
@Configuration
@Profile("prod")
public class ProdMybatisPlusConfig {
    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor(DbType.MYSQL);
    }
}

// 测试环境配置
@Configuration
@Profile("test")
public class TestMybatisPlusConfig {
    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor(DbType.H2);
    }
}
```

---

### 7. 分页查询溢出处理（超出总页数）

**场景**: 用户请求第 100 页，但实际只有 10 页数据

**默认行为**: 返回空列表

**自定义处理**: 启用溢出处理，自动跳转到第一页

```java
@Bean
public InnerInterceptor paginationInnerInterceptor() {
    PaginationInnerInterceptor interceptor = new PaginationInnerInterceptor(DbType.MYSQL);
    
    // 启用溢出处理：自动跳转到第一页
    interceptor.setOverflow(true);
    
    return interceptor;
}
```

---

### 8. 如何在测试环境使用 H2 数据库

**依赖配置** (`pom.xml`):

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

**测试配置** (`src/test/resources/application.yml`):

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1
    username: sa
    password: 
```

**分页插件配置**:

```java
@Configuration
@Profile("test")
public class TestMybatisPlusConfig {
    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor(DbType.H2); // 使用 H2 类型
    }
}
```

---

## 📚 相关文档

- [脚手架能力索引](../../../../doc/dev/提示词/专业提示词/java脚手架.md) - 查看框架所有可用模块
- [MyBatis-Plus 官方文档](https://baomidou.com/) - MyBatis-Plus 详细使用指南
- [分层对象规范](../../../../doc/dev/提示词/专业提示词/后端.md) - 了解 DTO/BO/Entity 使用规范

---

## 🤝 贡献与反馈

- **问题反馈**: [GitHub Issues](https://github.com/fanzezhen/fun-framework-java/issues)
- **功能建议**: [GitHub Discussions](https://github.com/fanzezhen/fun-framework-java/discussions)
- **代码贡献**: 欢迎提交 Pull Request
