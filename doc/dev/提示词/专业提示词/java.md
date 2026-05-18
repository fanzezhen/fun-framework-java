# Java后端补充规范
与全局提示词配合使用

## 代码格式规范

### 枚举类格式规范

**枚举常量的标点符号**：
- 枚举常量之间使用**逗号**分隔
- 最后一个枚举常量后使用**逗号 + 分号**（独立成行）
- 即使只有一个枚举常量，也要保留逗号和分号，便于未来扩展

**正确示例**：
```java
public enum FunContextExceptionEnum implements IExceptionCode<FunContextExceptionEnum> {
    /**
     * 请求头中缺失参数异常.
     */
    CONTEXT_HEADER_MISSING(100200, "请求头中缺失参数 %s"),
    ;

    private final Integer code;
    private final String text;

    FunContextExceptionEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }
}

// 多个枚举常量的示例
public enum StatusEnum {
    ACTIVE(1, "激活"),
    INACTIVE(0, "未激活"),
    DELETED(-1, "已删除"),
    ;

    private final Integer code;
    private final String name;

    StatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
```

**错误示例**（禁止）：
```java
// 错误1 - 最后一个常量直接用分号，没有逗号
public enum StatusEnum {
    ACTIVE(1, "激活");  // ❌ 错误

    private final Integer code;
}

// 错误2 - 逗号和分号在同一行
public enum StatusEnum {
    ACTIVE(1, "激活"),;  // ❌ 错误，分号应该独立成行

    private final Integer code;
}
```

**原因说明**：
1. **扩展性**：保留逗号使得添加新枚举常量时，只需添加新行，无需修改现有行
2. **Git diff 友好**：添加新常量时，Git 只显示新增行，不会因为修改标点符号而显示旧行的修改
3. **Java 规范**：这是 Java 枚举类的标准写法，与 Effective Java 推荐一致

## 测试驱动开发(TDD)实践

遵循"先测试,后实现"的开发原则

### 添加新功能的 TDD 流程

示例：实现用户注册功能

1. 先写测试用例（Given-When-Then模式）
2. 运行测试，确认失败（红灯）- 验证测试有效性
3. 实现最小化代码使测试通过
   - 校验用户名是否已存在
   - 加密密码
   - 保存用户
   - 转换返回
4. 运行测试，确认通过（绿灯）
5. 重构优化（可选），保持测试通过

### 修复 Bug 的 TDD 流程

示例：修复用户名重复注册的 Bug

1. 先写能复现 Bug 的测试
   - 注册第一个用户
   - 尝试用相同用户名注册第二个用户
   - 断言抛出 ServiceException
2. 运行测试，确认失败（证明成功捕获了 Bug）
3. 修改代码修复 Bug（添加用户名存在性检查）
4. 运行测试，确认通过（证明 Bug 已修复）
5. 运行完整测试套件，确保没有引入新问题

### 测试最佳实践

单元测试命名规范：
- 方法名格式：should[ExpectedBehavior]When[Condition]
- 示例：shouldReturnEmptyListWhenNoDataExists
- 示例：shouldThrowExceptionWhenParameterIsNull

测试结构（Given-When-Then）：
- Given：准备测试数据
- When：执行被测试的方法
- Then：验证结果

测试隔离性：
- 每个测试方法独立运行，互不影响
- 单元测试优先使用 H2 内存数据库
- 如不能使用内存数据库，使用 @BeforeEach 初始化和 @AfterEach 清理测试数据

H2 内存数据库配置（src/test/resources/application.properties）：
- spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL
- spring.jpa.hibernate.ddl-auto=create-drop

## 框架能力查询

在编写 Java 后端代码前，确认是否需要查阅脚手架能力索引

必须查阅 后端脚手架.md 的场景：
- 统一返回格式（框架已自动封装，Controller 直接返回业务对象）
- 全局异常处理或抛出业务异常（框架自动处理 ServiceException）
- 分页查询、数据校验、对象转换
- 缓存、分布式锁、异步任务
- 用户上下文（获取当前登录用户）、租户隔离
- 数据库操作（MyBatis-Plus 增强功能）
- 日志记录（操作日志、访问日志、链路追踪）
- 认证鉴权（Sa-Token、Spring Security）

无需查阅的场景：
- 纯业务逻辑实现（与通用框架能力无关）
- 已确认使用框架能力，正在编写具体代码

查阅方式（优雅降级）：
1. 本地文件：doc/dev/提示词/专业提示词/后端脚手架.md
2. GitHub Raw（本地不存在时）
3. Gitee Raw（GitHub 超时时）

## 典型问题的三层穿梭示例

### 空指针异常（NPE）
- 现象：NullPointerException、判空代码冗长
- 本质：契约式设计缺失、防御性编程不足、Optional使用不当
- 哲学："信任但要验证"、让类型系统表达意图
- 方案：使用Optional返回值、@NonNull/@Nullable注解、边界处验证

### 并发问题（线程安全）
- 现象：数据不一致、死锁、高并发性能下降
- 本质：竞态条件（check-then-act非原子）、锁粒度过粗、可见性问题
- 哲学："共享可变状态是根源"、不变性是最简单的线程安全
- 方案：synchronized/Lock保护临界区、使用并发集合、不可变对象、ThreadLocal

### 性能问题（内存与GC）
- 现象：OutOfMemoryError、Full GC频繁、内存泄漏
- 本质：对象生命周期失控、大对象直接进Old区、资源未关闭
- 哲学："内存有限"、GC有代价、局部性原则
- 方案：try-with-resources、对象池化、流式处理、选择合适数据结构

### Spring循环依赖
- 现象：BeanCurrentlyInCreationException、启动失败
- 本质：架构违反单一职责、两个类互相依赖说明职责不清
- 哲学："循环依赖是设计坏味道"、依赖图应为DAG
- 方案：@Lazy延迟加载、提取共同依赖到第三方Service、事件驱动解耦

### 事务管理问题
- 现象：数据不一致、@Transactional不生效、回滚失败
- 本质：自调用绕过AOP代理、异常被吞掉、传播行为配置错误
- 哲学："事务是原子性承诺"、ACID基石
- 方案：方法public且外部调用、catch块重新抛异常、提取事务方法到独立Service

### Stream API滥用
- 现象：链式调用难调试、性能比for循环差、可读性下降
- 本质：过度追求函数式风格、嵌套map/filter降低可读性、并行stream误用
- 哲学："可读性优于炫技"、惰性求值是核心优势
- 方案：简单遍历用for-each、复杂聚合用Stream、大数据集才parallel()

## 常量管理规范

### 通用常量使用规范

**所有非业务相关的通用常量必须使用 `com.github.fanzezhen.fun.framework.core.model.constant` 包下的常量类**

#### 常量分类

1. **时间相关常量** → `NormalTypeConstant`
   - `INT_MILLIS_PER_SECOND` - 秒转毫秒倍数 (1000)
   - `INT_ONE_MINUTE_SECONDS` - 一分钟秒数 (60)
   - `INT_ONE_HOUR_SECONDS` - 一小时秒数 (3600)
   - `INT_ONE_HOUR_MILLIS` - 一小时毫秒数
   - `INT_TWELVE_HOURS_MILLIS` - 12小时毫秒数
   - `LONG_ONE_HOUR_MILLIS` - 一小时毫秒数（long类型）

2. **数值常量** → `NormalTypeConstant`
   - `INT_1024` - 标准缓冲区大小（1KB）

3. **字符串常量** → `NormalTypeConstant`
   - `STR_RECORDS` - 记录列表字段名 ("records")
   - `STR_UNDERLINE_COUNT` - 计数字段后缀 ("_count")

4. **数据源常量** → `FunFrameworkCoreDataConstant`
   - `DEFAULT_DATASOURCE_NAME` - 默认数据源名称

#### 使用原则

**禁止行为**:
- ❌ 在业务代码中定义 `1000`、`60`、`3600` 等魔法数字
- ❌ 重复定义相同含义的常量（如多个类都定义 `MILLIS_PER_SECOND`）
- ❌ 在 Service/Controller 中定义通用常量

**推荐行为**:
- ✅ 优先检查 `NormalTypeConstant` 是否已有所需常量
- ✅ 如需新增通用常量，统一添加到 `NormalTypeConstant` 并提交 PR
- ✅ 业务特定常量（如业务状态码）定义在业务模块的常量类中

#### 示例

```java
// ❌ 错误示例：定义重复常量
public class UserService {
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int ONE_HOUR_SECONDS = 3600;
    
    public void expire(int seconds) {
        cache.expire(key, seconds * MILLIS_PER_SECOND);
    }
}

// ✅ 正确示例：使用统一常量
import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;

public class UserService {
    public void expire(int seconds) {
        cache.expire(key, seconds * NormalTypeConstant.INT_MILLIS_PER_SECOND);
    }
}
```

## Maven 构建优化

### 多线程构建规范

**所有 Maven 命令必须使用 `-T` 参数启用多线程构建**

**线程数计算**: `max(CPU核心数, 3)`

**注意**:
1. 使用 `-T 数字`，不要使用 `-T 1C` 或 `-T 2C`
2. 最低保证 3 个线程
3. 不要跳过单元测试（除非明确要求）

---