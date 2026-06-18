# SpringBoot 后端补充规范

依赖：`java.md`（必读，含其依赖 `测试.md`、`java脚手架.md`）。本文件只列 SpringBoot 专属增量。

## 集成测试

通用测试理念见 `测试.md`，JUnit 等 Java 通用测试见 `java.md`。本节为 SpringBoot 专属。

### 数据库测试（H2 的 Spring 接入）

H2 本身与依赖、DDL 兼容性见 `java.md`。本节为 SpringBoot 装配方式。演示项目强制 H2 内存库，禁用 MySQL/PostgreSQL（除非测试 DB 特定特性）。

`src/test/resources/application-test.properties`：
```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:db/schema-h2.sql
```

测试类加 `@ActiveProfiles("test")`，建表脚本放 `src/test/resources/db/schema-h2.sql`。

测试基类：
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 自动回滚
public abstract class BaseServiceTest {
}
```

### Web 层

- MockMvc 模拟请求：通用「优先真实协作对象」的例外，允许使用
- Testcontainers：需要真实中间件（如真实 MySQL/Redis）的集成测试

### 反模式

- 集成测试不用 `@Transactional` 自动回滚，导致用例间数据残留
