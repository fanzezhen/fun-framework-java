# Java 后端补充规范

依赖：`测试.md`（必读）、`java脚手架.md`（涉及框架通用能力时查）。

## 枚举格式

- 枚举常量之间用逗号分隔
- 最后一个常量保留逗号，分号独立成行（即使只有一个常量）
- 原因：扩展时只新增行，Git diff 干净

```java
public enum StatusEnum {
    ACTIVE(1, "激活"),
    INACTIVE(0, "未激活"),
    ;

    private final Integer code;
    private final String name;

    StatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
```

## 字段命名

集合字段按声明类型加后缀：
- `List<T>` → `xxxList`
- `Collection<T>` → `xxxs` 或 `xxxCollection`（不用 `List` 后缀）
- `Set<T>` → `xxxSet`
- `Map<K,V>` → `xxxMap`

## 测试

通用测试理念见 `测试.md`，本节为 Java 专属；SpringBoot 集成测试（H2、`@SpringBootTest`、MockMvc）见 `java-springboot.md`。

- 框架：JUnit 5；断言可配 AssertJ；mock 用 Mockito；架构约束用 ArchUnit
- 命名配 `@DisplayName`，方法名如 `testRegisterUser_UsernameExists_ShouldThrowException`
- 断言聚合用 `assertAll`（任一失败仍执行其余）：

```java
assertAll(
    () -> assertNotNull(result),
    () -> assertEquals("test", result.getUsername()),
    () -> assertEquals("test@example.com", result.getEmail())
);
```

- 重复构造的测试对象抽到静态测试数据工厂
- 覆盖率用 JaCoCo：`mvn clean test jacoco:report`，报告在 `target/site/jacoco/index.html`

### 测试数据库（H2）

H2 内存库适用于任何 Java 测试（纯 JDBC 即可，不依赖 Spring）。SpringBoot 的接入方式见 `java-springboot.md`。

依赖：
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

纯 JDBC 连接：`jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1`。

DDL 用标准 SQL，避免 MySQL 特定语法：

| 特性 | H2 | 建议 |
|------|----|----|
| AUTO_INCREMENT / PRIMARY KEY / FOREIGN KEY / INDEX | ✅ | 使用 |
| CURRENT_TIMESTAMP / NOW() / TIMESTAMP | ✅ | 使用 |
| COMMENT | ❌ | 避免 |
| ENGINE / CHARSET | ❌ | 避免 |
| ON UPDATE CURRENT_TIMESTAMP | ❌ | 用触发器 |

如需真实 MySQL 脚本：`schema-mysql.sql`（生产）+ `schema-h2.sql`（测试）。

### 测试图数据库（neo4j-harness）

关系库用 H2，图库用 `org.neo4j.test:neo4j-harness`（进程内 Neo4j，走真实 Bolt，无需 Docker）：

```xml
<dependency>
    <groupId>org.neo4j.test</groupId>
    <artifactId>neo4j-harness</artifactId>
    <scope>test</scope>
</dependency>
```

```java
neo4j = Neo4jBuilders.newInProcessBuilder().withDisabledServer().build();
// neo4j.boltURI() 即真实 Bolt 地址，驱动直连
```

注意事项：

- 与 H2 不同，它启动完整 Neo4j 内核（数据落临时目录，非纯内存），启动秒级 —— 整类共用一个实例（`@BeforeAll`），每个用例前清库（`MATCH (n) DETACH DELETE n`）保证隔离
- Neo4j 内核为 **GPL-3.0-or-later**，仅可用 `test` scope，不得进入分发产物
- Neo4j 2026.x 在 JDK 21+ 需额外 JVM 参数，子模块自定义 surefire `argLine` 会完全覆盖父 POM 默认值，须把父值一并带上再追加：
  `--add-opens java.base/java.nio=ALL-UNNAMED --add-modules jdk.incubator.vector`
- 定位为集成测试：验证语句能否被真实图库执行、驱动返回值能否正确归一化；纯逻辑仍用桩测

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

## 框架能力查询

涉及通用功能（统一返回、全局异常/`ServiceException`、分页、参数校验、对象转换、缓存、分布式锁、异步、用户上下文、租户隔离、MyBatis-Plus、日志、认证鉴权等）前必须读 `java脚手架.md`（本地 `doc/dev/提示词/专业提示词/java脚手架.md` → GitHub Raw → Gitee Raw），完整场景与用法以该文档为准。纯业务逻辑或已确认用法无需查阅。

## 常量管理

非业务通用常量必须使用 `com.github.fanzezhen.fun.framework.core.model.constant` 包：

| 类型 | 类 |
|------|-----|
| 时间/数值/字符串 | `NormalTypeConstant` |
| 数据源 | `FunFrameworkCoreDataConstant` |

`NormalTypeConstant` 常用：
- `INT_MILLIS_PER_SECOND` (1000)
- `INT_ONE_MINUTE_SECONDS` (60)
- `INT_ONE_HOUR_SECONDS` (3600)
- `INT_ONE_HOUR_MILLIS` / `LONG_ONE_HOUR_MILLIS`
- `INT_TWELVE_HOURS_MILLIS`
- `INT_1024`（缓冲区大小）
- `STR_RECORDS` / `STR_UNDERLINE_COUNT`

规则：
- 禁止业务代码定义 `1000`/`60`/`3600` 等魔法数字
- 禁止重复定义同义常量
- 禁止在 Service/Controller 定义通用常量
- 新增通用常量统一加到 `NormalTypeConstant`
- 业务特定常量放业务模块的常量类

## Maven 构建

所有 Maven 命令使用 `-T 数字` 启用多线程：
- 线程数 = `min(max(CPU 核心数, 3), Maven 模块数)`
- 线程数不超过实际参与构建的 Maven 模块数（多余线程无模块可调度，纯属浪费）
- 不使用 `-T 1C`/`-T 2C`
- 不跳过单测（除非明确要求）

## 三层穿梭典型问题

| 问题 | 现象 | 本质 | 方案 |
|------|------|------|------|
| NPE | NullPointerException、判空冗长 | 契约缺失、防御不足 | `Optional`、`@NonNull`/`@Nullable`、边界处验证 |
| 并发 | 数据不一致、死锁 | 竞态、锁粒度、可见性 | `synchronized`/`Lock`、并发集合、不可变对象、`ThreadLocal` |
| 内存/GC | OOM、Full GC 频繁 | 生命周期失控、资源未关 | try-with-resources、对象池、流式处理、合适数据结构 |
| 循环依赖 | `BeanCurrentlyInCreationException` | 职责不清 | `@Lazy`、提取共同依赖、事件解耦 |
| 事务失效 | `@Transactional` 不生效 | 自调用绕过 AOP、异常被吞 | 方法 public 外部调用、catch 重抛、提取到独立 Service |
| Stream 滥用 | 难调试、性能差 | 嵌套过度、并行误用 | 简单遍历用 for-each、大数据集才 `parallel()` |
