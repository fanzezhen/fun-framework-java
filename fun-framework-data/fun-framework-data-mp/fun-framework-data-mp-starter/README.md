fun-framework-data-mp
------------------------------------------
错误码格式 120**
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
[MybatisPlusConfigExample.java](src/test/java/com/github/fanzezhen/fun/framework/mp/example/MybatisPlusConfigExample.java)

---

## [base.entity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity)：基础实体类
1. [BaseEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2FBaseEntity.java)：基础实体模型，包含 主键、创建时间、创建人 字段
2. [BaseGenericEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2FBaseGenericEntity.java)：常规实体模型，继承自[BaseEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2FBaseEntity.java)，新增 删除标识、更新时间、更新人 字段
3. [BaseTenantEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2Ftenant%2FBaseTenantEntity.java)：租户基础实体模型，继承自[BaseEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2FBaseEntity.java)，新增 租户id 字段
4. [BaseTenantGenericEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2Ftenant%2FBaseTenantGenericEntity.java)：租户常规实体模型，继承自[BaseTenantEntity](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fmp%2Fbase%2Fentity%2Ftenant%2FBaseTenantEntity.java)，新增 删除标识、更新时间、更新人 字段
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

**步骤 1**: 实体类继承多租户 BaseEntity

```java
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.tenant.BaseTenantEntity;

@TableName("tenant_order")
public class OrderEntity extends BaseTenantEntity {
    private String orderNo;
    // tenantId 字段已在 BaseTenantEntity 中定义
}
```

**步骤 2**: 配置多租户拦截器

```java
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor(DbType.MYSQL);
    }

    @Bean
    public InnerInterceptor tenantLineInnerInterceptor() {
        TenantLineInnerInterceptor interceptor = new TenantLineInnerInterceptor();
        interceptor.setTenantLineHandler(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                // 从上下文获取当前租户 ID（需自行实现 TenantContextHolder）
                Long tenantId = TenantContextHolder.getTenantId();
                return new LongValue(tenantId);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id"; // 租户字段名
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // 忽略不需要租户隔离的表（如系统配置表）
                return Arrays.asList("sys_config", "sys_dict").contains(tableName);
            }
        });
        return interceptor;
    }
}
```

---

### 5. 启动时报错: ConditionalOnBean(InnerInterceptor.class) 不满足

**症状**:
```
CONDITIONS EVALUATION REPORT:
...
Negative matches:
   FunMpInterceptorAutoConfiguration:
      Did not match: @ConditionalOnBean (types: InnerInterceptor) did not find any beans
```

**原因**: 未提供任何 `InnerInterceptor` Bean 配置

**解决方案**: 添加至少一个拦截器配置（通常是分页插件），参考 [必需配置](#2-⚠️-必需配置重要) 章节

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

- [脚手架能力索引](../../../../doc/dev/提示词/专业提示词/后端脚手架.md) - 查看框架所有可用模块
- [MyBatis-Plus 官方文档](https://baomidou.com/) - MyBatis-Plus 详细使用指南
- [分层对象规范](../../../../doc/dev/提示词/专业提示词/后端.md) - 了解 DTO/BO/Entity 使用规范

---

## 🤝 贡献与反馈

- **问题反馈**: [GitHub Issues](https://github.com/fanzezhen/fun-framework-java/issues)
- **功能建议**: [GitHub Discussions](https://github.com/fanzezhen/fun-framework-java/discussions)
- **代码贡献**: 欢迎提交 Pull Request
