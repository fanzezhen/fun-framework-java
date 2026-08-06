# fun-framework-java 脚手架能力索引

编写新代码涉及通用功能时查阅，避免重复实现。

仓库：GitHub/Gitee `fanzezhen/fun-framework-java`
版本：父 POM 统一管理，禁止覆盖
流程：查本文档 → 找模块 → 读 README → 看测试 → 写代码

核心原则：
1. 先查后写
2. 依赖版本不覆盖
3. 不确定查文档或问用户
4. 分层解耦：Service 不依赖 MyBatis-Plus 的 `Page`/`IPage`，DAO 用框架的 `PageCondition`/`PageDTO`

---

## 1. 核心模块（fun-framework-core）

### 1.1 核心模型（fun-framework-core-model）

包：`com.github.fanzezhen.fun.framework.core.model`

关键类：
- `Result<T>`：统一返回（code/message/data）
- `PageRequest`：Controller 接收分页参数
- `PageCondition`：Service/Dao 传递分页条件
- `PageDTO<T>`：分页结果，支持 `convert(Function)` 转换
- `PageUtil`：DAO 层 `IPage` ↔ `PageDTO` 转换
  - `toPageResult(IPage)` → `PageDTO`
  - `toPage(PageCondition)` → MyBatis `Page`
- `MapperFacadeUtil`：可插拔引擎（默认 MethodHandle，可切 Orika）的对象映射
  - `map(source, TargetClass)`
  - `page(pageDTO, SourceClass, TargetClass)`
- `StrTemplateUtil`：字符串模板（`${var}` 和 `$var`）
- 基类（泛型 P 为主键类型）：
  - `BaseBO<P>` / `BaseDTO<P>`：包含 id/createTime/createUserId
  - `BaseTenantBO<P>` / `BaseTenantDTO<P>`：额外含 tenantId（类型同 P）
  - `IGenericEntity<P>`：非 MyBatis Entity 接口
- 常量包 `constant`：
  - `NormalTypeConstant`：时间/数值/字符串通用常量
  - `FunFrameworkCoreDataConstant`：数据源常量

继承规范：
- BO → `BaseBO<P>`（Service 内部）
- DTO → `BaseDTO<P>`（Controller 入参/出参，Service 返回值）
- 多租户 → `BaseTenant*<P>`
- 非 MyBatis Entity → `IGenericEntity<P>`

注意：
- `MapperFacadeUtil` 委托可插拔映射引擎 `FunObjectMapper`，配置项 `fun.mapper.engine` 选引擎：
  - `method-handle`（默认）：纯 JDK MethodHandle 引擎，走 public getter/setter，无需 `--add-opens`
  - `orika`：包装 Orika `MapperFacade`（需 classpath 提供 orika-core）
- Spring Boot 项目添加 `fun-framework-core-springboot`/`web`/`ai` 任一依赖即自动装配默认引擎；子项目注册自定义 `FunObjectMapper` bean 可覆盖
- 非 Spring Boot 无需初始化，`MapperFacadeUtil` 首次调用即用 MethodHandle 引擎兜底
- 用法不变：`MapperFacadeUtil.map/mapAsList/page`
- BO/DTO 的 tenantId 类型为泛型 P；MyBatis Entity 的 tenantId 类型固定（Integer/Long/String）

### 1.1.1 JDK21 强封装：`--add-opens` 仅 orika 引擎需要

默认 MethodHandle 引擎走 public getter/setter，不访问 JDK 内部类，**无需 `--add-opens`**。

仅当显式设置 `fun.mapper.engine=orika` 时，Orika 反射访问 JDK 内部类会被 JDK21 强封装拒绝（报 `InaccessibleObjectException`），此时业务项目须补充：

1. **IDEA 运行配置** `.run/Application.run.xml` VM options：
   ```
   --add-opens=java.base/java.lang=ALL-UNNAMED
   --add-opens=java.base/java.util=ALL-UNNAMED
   --add-opens=java.base/java.util.concurrent=ALL-UNNAMED
   ```

2. **surefire 单测** `argLine`：加上同样三行。

注意：业务项目若自定义 `argLine` 覆盖了脚手架默认值，且用 orika 引擎，须把这三行一并带上。

### 分层对象使用规范（重要）

#### 分页

| 层 | 入参 | 返回 |
|----|------|------|
| Controller | `PageRequest` | `PageDTO<DTO>` |
| Service | `PageRequest` 或 `PageCondition` | `PageDTO<DTO>` |
| DAO | `PageCondition` | `PageDTO<Entity>` |

DAO 层标准模式：
```java
default PageDTO<Entity> page(XxxPageCondition condition) {
    LambdaQueryWrapper<Entity> wrapper = buildWrapper(condition);
    return PageUtil.toPageResult(selectPage(PageUtil.toPage(condition), wrapper));
}
```

Service 层标准模式：
```java
public PageDTO<DTO> page(XxxPageRequest request) {
    XxxPageCondition condition = convertToCondition(request);
    PageDTO<Entity> entityPage = mapper.page(condition);
    return MapperFacadeUtil.page(entityPage, Entity.class, DTO.class);
}
```

#### Service 层返回值

- 禁止返回 Entity，必须 DTO/BO
- 转换：`MapperFacadeUtil.map(entity, DTO.class)`

| 返回类型 | 场景 |
|----------|------|
| DTO | Service → Controller（推荐） |
| BO | Service 内部流转 |
| `PageDTO<DTO>` | 分页 |
| `List<DTO>` | 列表 |

#### 禁止事项
- Controller 手动 `@RequestParam Integer current`（用 `PageRequest`）
- DAO 入参/返回 `Page`/`IPage`（用 `PageCondition`/`PageDTO`）
- Service 依赖 MyBatis-Plus 的 `Page`/`IPage`
- BO/DTO 手动定义 id/createTime/createUserId/tenantId
- Service 返回 Entity
- Controller 手动 `Result.success()`

#### 编码前检查清单
- [ ] Controller 用 `PageRequest` 子类
- [ ] Service 返回 DTO（非 Entity）
- [ ] DAO 用 `PageCondition` / `PageDTO<Entity>`
- [ ] DAO 内部用 `PageUtil.toPage()`/`toPageResult()`
- [ ] DTO/BO 继承对应 Base 类
- [ ] 未手动定义基础字段
- [ ] 泛型 P 与 Entity 主键一致

### 1.2 异常（fun-framework-core-model）

无独立异常模块，异常体系在 `core-model`，全局处理器在 `fun-framework-springboot-web`。

- `ServiceException`：业务异常（`com.github.fanzezhen.fun.framework.core.model.exception`）
- `IExceptionCode` / `ICodeTextEnum`：异常码枚举契约；各模块自建 `Fun*ExceptionEnum` 实现
- `DefaultExceptionHandler`：全局处理器（在 `springboot-web`，`@RestControllerAdvice`）
- 业务校验失败直接抛 `ServiceException`，框架自动返回标准错误格式

### 1.3 缓存（fun-framework-core-cache / cache-redis）
- `core-cache` 定义 `CacheService` / `LockService` 接口，`cache-redis` 提供 Redis 实现
  （`FunRedisCacheServiceImpl` / `FunRedisLockServiceImpl`）
- 场景：热点缓存、分布式锁、会话管理

### 1.4 线程（fun-framework-core-thread）

关键类：
- `ThreadPoolExecutorRepository`：管理 JDK `ThreadPoolExecutor`（包 `com.github.fanzezhen.fun.framework.core.thread`）
- `ThreadPoolTaskExecutorRepository`：管理 Spring `ThreadPoolTaskExecutor`（包 `thread.com.github.fanzezhen.fun.framework.core.springboot`，**位于 `fun-framework-core-springboot` 模块**）
- `ExecutorHolder`：异步任务执行器，支持批量任务和结果收集
- `ThreadDecorator`：装饰器接口（包 `com.github.fanzezhen.fun.framework.core.thread.decorator`），传递 traceId/用户上下文等

使用示例：
```java
// Spring Boot 环境
ThreadPoolTaskExecutor executor =
    ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("myPool", 5, 10);

// 非 Spring 环境
ThreadPoolExecutor executor =
    ThreadPoolExecutorRepository.newThreadPoolExecutor("myPool", 5, 10);

executor.execute(() -> {
    Long userId = ContextHolder.getUserId(); // 子线程继承父线程上下文
});

ThreadPoolTaskExecutorRepository.addDecorator(new CustomDecorator());
```

注意：
- `PoolExecutors` 已废弃，改用上述 Repository
- `ThreadPoolTaskExecutorRepository` 创建的线程池自动配置 TTL 装饰器（`TtlRunnable::get`）
- `addDecorator()` 添加后会异步重建所有线程池

### 1.5 上下文（fun-framework-core-context）

- `ContextHolder`：基于 `TransmittableThreadLocal`，支持父子线程传递
- `FunContextFilter`：Web 过滤器，自动从请求头放入上下文
- `@ContextHeader`：注解 + AOP 校验

```java
Long userId = ContextHolder.getUserId();
Long tenantId = ContextHolder.getTenantId();
String traceId = ContextHolder.getTraceId();
```

注意：v2.x 升级为 `TransmittableThreadLocal`，需引入 `com.alibaba:transmittable-thread-local`。

### 1.6 数据基础（已废弃）

`fun-framework-core-data` 已合并到 `core-model`：
- 包路径 `com.github.fanzezhen.fun.framework.core.data.util.*` → `com.github.fanzezhen.fun.framework.core.model.util.*`
- 仅需更新 import

数据访问通用抽象（在 `core-model`，各存储模块共用，禁止重复实现）：
- `ITemplate<P>`：`get`/`getById`/`listByIds`/`listByColumn`/`insert`/`deleteById`
- `BaseMultiDatasourceTemplate<T, C>` + `IDatasourceConfig`：多数据源索引、按 `@Entity(datasource)` 路由、默认回退、子模板惰性创建（需启动即创建则在子类构造器末尾调 `initAllTemplates()`）
- `IdentifierUtil`：表名/列名/图标签这类无法参数化的标识符白名单校验（`quote` / `requireLegal`），不合法抛异常不转义
- `@Column`：`name` 列名、`isPrimaryKey` 主键、`writable=false` 只读列、`deserializeResolver` 自定义反序列化

注意：`core-model` 刻意不依赖 Spring（仅 fastjson2/orika/lombok/hutool）。在其中取注解用 hutool `AnnotationUtil`，勿引入 `org.springframework.core.annotation.AnnotationUtils`。

### 1.7 日志（fun-framework-core-log）

- `FunLogTraceIdFilter`：自动生成 TraceId
- `LevelLogger`：分级日志
- 日志序列化器：字节数组、字符序列、IO 流
- 场景：分布式链路追踪、结构化日志

注意：Web 接口日志 `FunLogPrintFilter` 在 `fun-framework-springboot-web` 模块。

### 1.8 验证（fun-framework-core-verify）

- `@NoConcurrent`：防并发，基于分布式锁，确保相同请求同一时刻仅一个实例执行
- `@NoRepeat`：防重复提交，基于缓存实现幂等校验，拦截短时间内的重复请求

注意：JWT 认证（`FunJwtHandlerInterceptor`、`JwtService` 等）在 `fun-framework-springboot-web` 的 `core.springboot.web.jwt.*`。

### 1.9 Spring Boot 基础（fun-framework-core-springboot）

非 Web 应用使用。

关键类：
- `MapperFacadeUtil` 自动配置（零配置）
- `ThreadPoolTaskExecutorRepository`：Spring 线程池管理
- `FunJacksonConfig`：Jackson 统一配置
- 自动配置类：`config.com.github.fanzezhen.fun.framework.core.springboot.FunCoreSpringbootStarterAutoConfiguration`

适用：定时任务、消息消费者等纯后端服务。不含 Web 功能（异常处理等用 web 模块）。

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-core-springboot</artifactId>
</dependency>
```

---

## 2. Spring Boot 扩展（fun-framework-springboot）

### 2.1 Web（fun-framework-springboot-web）

关键类：
- `DefaultExceptionHandler`：全局异常 → `ActionResult`
  - 处理 `ServiceException`、`MethodArgumentNotValidException`、`ConstraintViolationException`、`ValidationException`，兜底 `Exception`
- JWT：`FunJwtHandlerInterceptor`、`JwtService`、`FunCoreVerifyTokenApi`（包 `core.springboot.web.jwt.*`）
- 日志：`FunLogPrintFilter`、请求/响应包装器
- MVC：`ResponseBodyWrapper`、`FunWebMvcRegistrations`
- 校验：`@BelongTo`、`@EnumsOf`、`@ValueIn`
- 工具：`ServletUtil`
- 自动配置：`com.github.fanzezhen.fun.framework.core.springboot.web.config.FunCoreSpringbootWebAutoConfiguration`

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-springboot-web</artifactId>
</dependency>
```

重要：Controller 直接返回业务对象，框架自动封装为 `Result<T>`。

异常示例：
```java
if (user == null) {
    throw new ServiceException("用户不存在");
}
// → {"code": 500, "message": "用户不存在", "data": null}
```

### 2.2 AI（fun-framework-springboot-ai）

状态：基础骨架已建立，具体功能实现中
- Spring AI MCP Server 集成
- AI Annotations 支持
- 适用：AI 增强应用、智能对话

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-springboot-ai</artifactId>
</dependency>
```

### 模块选择

| 应用类型 | 推荐模块 |
|---------|---------|
| 纯后端服务 | `fun-framework-core-springboot` |
| RESTful API | `fun-framework-springboot-web` |
| AI 应用 | `fun-framework-springboot-ai` |
| AI + Web | `web` + `ai`（同时使用） |

注意：框架同时支持 Spring Boot 2.x 和 3.x。

---

## 3. 数据持久化（fun-framework-data）

### 3.1 MyBatis-Plus（fun-framework-data-mp-starter）

错误码：120**

关键类：
- `BaseEntity` 基类（包 `com.github.fanzezhen.fun.framework.mp.base.entity`）
- `PageUtil` 分页转换（`com.github.fanzezhen.fun.framework.mp.PageUtil`）
  - `toPageResult(IPage<T>)` / `toPageResult(IPage<?>, List<R>)` / `toPage(PageCondition)` / `toPage(Long, Long)`

必需配置（否则分页不可用）：
```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor(DbType.MYSQL);
    }
}
```

#### Entity 继承

| 主键策略 | 路径 | 主键类型 |
|---------|------|---------|
| 自增 | `increment.BaseEntity` | Integer |
| UUID | `uuid.BaseEntity` | String |
| 雪花 | `snowflake.BaseEntity` | Long |
| 多租户 | `*.tenant.BaseTenantEntity` | 同主键 |
| 自定义 | `*.BaseGenericEntity<P>` | 泛型 P |

`BaseEntity` 自动填充字段：`id`、`createTime`、`createUserId`、（多租户）`tenantId`。

时间字段统一用 `java.time.LocalDateTime`（禁止 `java.util.Date`/`Calendar`）；Entity、Condition、Request、Response、BO/DTO 全链路保持一致，避免查询条件与实体字段类型不匹配。

```java
import com.github.fanzezhen.fun.framework.mp.base.entity.increment.BaseEntity;

@TableName("sys_user")
public class UserEntity extends BaseEntity {
    private String username;
}
```

### 3.2 操作日志（fun-framework-data-mp-trace / -trace-impl）
- SQL 执行日志、操作审计、数据变更记录

### 3.3 Elasticsearch（fun-framework-data-elasticsearch）
- ES 工具、索引管理、全文检索
- 聚合：`CountBucket`、`SumBucket`、滚动搜索
- 多数据源：`fun.data.elasticsearch.configs[]`，`default-datasource` 指定默认
- `uris` 两种等价写法：逗号分隔标量 `uris: http://h1:9200,http://h2:9200` 或 YAML 列表；均走 Spring 原生绑定，逐项去空白

### 3.4 图数据库（fun-framework-data-graph）
- `-base` 抽象层零驱动依赖，`-neo4j-starter` 为 Neo4j 实现；引 starter 即带入 base
- 模板 `BaseMultiDatasourceGraphTemplate`（接口 `IGraphTemplate extends ITemplate<String>`）
- 主键即图库内部标识 `elementId`（String）；driver 6.x 的 `id()` 已弃用，框架不用
- 图专属注解：`@GraphNode`、`@GraphRelationship`、`@GraphId`（图库内部标识）、`@GraphLabels`、`@GraphType`、`@StartNode`、`@EndNode`
- 属性名与业务主键复用 core-model 的 `@Column`：`name` 属性名、`isPrimaryKey` 业务主键、`writable=false` 只读不写入；不另立图专属注解
- 查询：`queryList`/`queryOne`/`queryObject`（首行首列）/`queryRecordList`（中间表示）/`queryNative`（原生，慎用）
- 写入：`insert`（新增并回填 elementId）、`merge`（按业务主键 MERGE，需 `@GraphId(business = true)`）、`deleteById`
- 多数据源：`fun.data.graph.configs[]`，按 `@Entity(datasource)` 路由；模板惰性创建
- 实体扫描：`fun.data.graph.entity-packages`，未配置取启动类所在包（不复用 JPA 的 `@EntityScan`）
- 注入防护：标签/关系类型/属性名过 `IdentifierUtil` 白名单校验（无法参数化），值一律参数绑定；自行拼 Cypher 须同样遵循
- 异常码：图特有 123**（12300 执行失败、12301 功能不支持、12302 缺图注解）；通用数据异常用 `FunCoreDataExceptionEnum`
- 扩展点：`IGraphMapper`（映射引擎）、`BaseMultiDatasourceGraphTemplate`（模板）、`BaseGraphTemplate`（接新图库，实现 3 个驱动原语）
- 测试：图库集成测试用 `neo4j-harness`（进程内 Neo4j，走真实 Bolt，无需 Docker），用法与注意事项见 `java.md`「测试图数据库」

---

## 4. 安全（fun-framework-security）

- `fun-framework-security-sa-token`：Sa-Token（登录、权限、Token、SSO）
- `fun-framework-security-spring-security`：Spring Security（OAuth2、RBAC）

---

## 5. 工具

- `fun-framework-jasypt`：配置文件加密
- `fun-framework-proxy`：代理增强（proxy-core/fastjson/mybatis/orika/method-handle）
- `fun-framework-sentinel`：限流、熔断、热点参数
- `fun-framework-spring-doc`：SpringDoc 接口文档
- `fun-framework-api-count-redis`：API 调用统计

---

## 常见错误速查

| 错误 | 正确 |
|------|------|
| Controller 手动 `Result.success()` | 直接返回 DTO |
| Service 返回 Entity | `MapperFacadeUtil.map()` 转 DTO |
| Service 用 `IPage`/`Page` | DAO 返回 `PageDTO`，Service 不依赖 MyBatis |
| DAO 返回 `IPage<Entity>` | `PageUtil.toPageResult()` 转 `PageDTO<Entity>` |
| 手动定义 id/createTime | 继承 `BaseDTO<P>`/`BaseBO<P>`/`BaseEntity` |
| 覆盖框架依赖版本 | 继承父 POM，不覆盖 |
| 手写对象映射 | `MapperFacadeUtil.map()/page()` |
| try-catch 处理业务异常 | 抛 `ServiceException` |

---

## 快速查询表

按模块归组，组内按使用频度排列；模块按依赖层次排列（核心 → 数据 → Web/安全 → 工具）。

### 核心模块

| 模块 | 需求 | 关键类/方法 |
|------|-----|-----------|
| core-model | 分页-Controller | `PageRequest` / `PageDTO<DTO>` |
| core-model | 分页-Service | `PageCondition` / `PageDTO<DTO>` |
| core-model | 基础对象 | `BaseDTO<P>` / `BaseBO<P>` / `BaseTenant*` |
| core-model | 对象映射 | `MapperFacadeUtil.map/page` |
| core-model | 业务异常 | `ServiceException` / `FunCoreDataExceptionEnum` |
| core-model | 多数据源路由 | `BaseMultiDatasourceTemplate` / `IDatasourceConfig` / `@Entity(datasource)` |
| core-model | 标识符防注入 | `IdentifierUtil.quote/requireLegal` |
| core-cache | 缓存 | `CacheService`（实现见 cache-redis）/ `@Cacheable` |
| core-cache | 分布式锁 | `LockService`（实现见 cache-redis） |
| core-context | 当前用户 | `ContextHolder` |
| core-thread | 异步任务 | `@Async` |
| core-verify | 防并发/防重提交 | `@NoConcurrent` / `@NoRepeat` |

### 数据访问模块

| 模块 | 需求 | 关键类/方法 |
|------|-----|-----------|
| data-mp | 分页-DAO | `PageCondition` / `PageDTO<Entity>` / `PageUtil` |
| data-mp | Entity 基类 | `increment/uuid/snowflake.BaseEntity` |
| data-elasticsearch7 | ES 操作 | `BaseMultiDatasourceElasticsearchTemplate` / `IElasticsearchTemplate` |
| data-graph-neo4j-starter | 图数据库操作 | `BaseMultiDatasourceGraphTemplate` / `IGraphTemplate` |

### Web 与安全模块

| 模块 | 需求 | 关键类/方法 |
|------|-----|-----------|
| springboot-web | 统一返回 | `ResponseBodyWrapper`（自动） |
| security-sa-token | 认证/权限 | `StpUtil` / `@SaCheckPermission` |

### 工具模块

| 模块 | 需求 | 关键类/方法 |
|------|-----|-----------|
| cache-redis | 缓存/锁的 Redis 实现 | `FunRedisCacheServiceImpl` / `FunRedisLockServiceImpl` |
| spring-doc | 接口文档 | `@Operation` / `@Schema` |

---

## 必查项

1. Service 返回值必须 DTO/BO，禁返 Entity
2. DAO 用 `PageCondition` 入参、`PageDTO<Entity>` 返回
3. Service 禁止依赖 MyBatis-Plus 的 `Page`/`IPage`
4. DTO/BO 必须继承 Base 类，禁止手写基础字段
5. 代码变更检查本文档是否需要更新
