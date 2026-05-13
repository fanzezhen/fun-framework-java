# Fun Framework 后端架构设计

## 模块总览

```
fun-framework-java
├── fun-framework-core                     # 核心模块
│   ├── fun-framework-core-cache          # 缓存模块
│   ├── fun-framework-core-context        # 上下文管理模块
│   ├── fun-framework-core-log            # 日志模块（已合并，单一模块）
│   ├── fun-framework-core-model          # 核心模型
│   ├── fun-framework-core-springboot     # SpringBoot 核心集成（基础配置）
│   ├── fun-framework-core-thread         # 线程管理模块
│   ├── fun-framework-core-verify         # 验证工具模块（图片验证码、加密工具）
│   └── fun-framework-core-all            # 核心模块聚合器（传递依赖所有核心模块）
├── fun-framework-springboot              # SpringBoot 扩展模块（新增父模块）
│   ├── fun-framework-springboot-web     # Web 应用功能（异常处理、JWT、日志、MVC 扩展）
│   └── fun-framework-springboot-ai      # AI 功能扩展（Spring AI 集成）
└── ... (其他业务模块)
```

---

## 核心设计原则

### 1. 模块化分层
- **核心层 (core)**: 提供通用配置、工具类、不依赖 Web 环境（如 model、thread、log、verify）
- **SpringBoot 基础层 (core-springboot)**: SpringBoot 环境下的基础配置和工具（如线程池、Jackson 配置）
- **SpringBoot Web 层 (springboot-web)**: Web 应用专用功能，依赖 Servlet API（异常处理、JWT、日志过滤）
- **扩展层 (springboot-ai/其他)**: 特定领域功能，按需引入

### 2. 依赖管理策略
- 父模块作为 `pom` 聚合器，不包含实现代码
- 子模块按职责明确划分依赖范围
- 使用 BOM 统一管理第三方依赖版本

### 3. 自动配置原则
- 使用 Spring Boot 自动配置机制
- 兼容 Spring Boot 2.x 和 3.x
- 条件化装配，避免强制依赖

---

## 模块详细设计

### 模块职责矩阵

| 模块 | 职责 | 典型场景 | 依赖特点 |
|------|------|----------|----------|
| **core-springboot** | SpringBoot 基础配置和通用工具 | 对象映射、线程池、Jackson配置 | 无 Web 依赖，可用于非 Web 应用 |
| **springboot-web** | Web 应用专用功能 | 全局异常处理、JWT 认证、Web 日志、MVC 扩展 | 依赖 Servlet API、Spring Web |
| **springboot-ai** | AI 功能扩展 | Spring AI 集成（MCP Server、AI Annotations） | 依赖 Spring AI BOM |

## fun-framework-core-springboot 模块架构

### 核心功能组件
1. **MapperFacadeUtil 自动配置**
   - 自动装配 Orika MapperFacade
   - 通过 `@PostConstruct` 初始化静态工具类
   - 可选依赖，无 Bean 时不影响启动

2. **线程池管理**
   - `ThreadPoolTaskExecutorRepository`: 线程池仓库模式
   - 支持动态装饰器注册（如 TTL 上下文传递）
   - 优雅关闭机制

3. **Jackson 序列化配置**
   - 统一日期时间格式
   - 空值处理策略
   - 驼峰命名转换

### 自动配置类
```java
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.springboot.base")
public class FunCoreSpringbootStarterAutoConfiguration {
    @Autowired(required = false)
    private MapperFacade mapperFacade;
    
    @PostConstruct
    public void init() {
        if (mapperFacade != null) {
            MapperFacadeUtil.setMapperFacade(mapperFacade);
        }
    }
}
```

---

## fun-framework-springboot-web 模块架构

### 核心功能组件
1. **全局异常处理器**
   - `DefaultExceptionHandler`: 统一异常转 `ActionResult`
   - 支持的异常类型:
     - `ServiceException` - 业务异常
     - `MethodArgumentNotValidException` - 参数校验异常
     - `ConstraintViolationException` - 约束校验异常
     - `ValidationException` - 通用校验异常
     - `Exception` - 兜底处理

2. **JWT 认证功能**
   - `FunJwtHandlerInterceptor`: JWT 请求拦截器
   - `FunCoreVerifyTokenApi`: Token 验证 API 接口
   - `JwtService`: JWT 服务接口
   - `FunDefaultJwtServiceImpl`: 默认 JWT 实现

3. **Web 日志功能**
   - `FunLogPrintFilter`: 请求/响应日志打印过滤器
   - `LoggingHttpServletRequestWrapper`: 可重复读取的请求包装器
   - `LoggingHttpServletResponseWrapper`: 可重复读取的响应包装器
   - `MultipartFilePrintSerializer`: 文件上传序列化器

4. **MVC 扩展**
   - `FunRequestMappingHandlerMapping`: 自定义路径映射处理
   - `FunWebMvcRegistrations`: MVC 组件注册
   - `ResponseBodyWrapHandler`: 响应体包装处理器
   - `ResponseBodyWrapFactoryBean`: 响应包装工厂

5. **参数校验注解**
   - `@BelongTo`: 值属于指定集合
   - `@EnumsOf`: 枚举值校验
   - `@ValueIn`: 值范围校验

6. **工具类**
   - `ServletUtil`: Servlet 工具方法
   - `FunSpringbootWebProperties`: Web 配置属性

#### 异常处理流程
```
Controller 抛出异常
    ↓
@RestControllerAdvice 拦截
    ↓
根据异常类型选择处理器
    ↓
转换为 ActionResult 统一格式
    ↓
@ResponseStatus(HttpStatus.OK) 返回
```

### 自动配置类
```java
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.core.springboot.web")
public class FunCoreSpringbootWebAutoConfiguration {
    // 自动扫描并注册以下组件：
    // - DefaultExceptionHandler
    // - FunLogPrintFilter
    // - FunJwtHandlerInterceptor (如果配置启用)
    // - FunWebMvcRegistrations
}
```

---

## fun-framework-springboot-ai 模块架构

### 设计目标
- 集成 Spring AI 2.0.0-M6
- 提供统一的 AI 服务抽象
- 支持多模型提供商切换

### 当前状态
- 基础骨架已建立
- 自动配置类已创建
- 依赖 Spring AI 2.0.0-M6
- 集成 `spring-ai-starter-mcp-server-webmvc`
- 集成 `spring-ai-mcp-annotations`
- 具体功能待实现

---

## 依赖关系图

```
┌─────────────────────────────────────┐
│   应用模块 (Application Module)     │
└─────────────────────────────────────┘
              │
              ├─── 引入 core-springboot (基础应用必选)
              ├─── 引入 springboot-web (Web 应用必选)
              └─── 引入 springboot-ai (使用 AI 功能时)
              │
┌─────────────┴────────────────────────┐
│  fun-framework-core-springboot       │
│  ├─ fun-framework-core-model         │
│  ├─ fun-framework-core-thread        │
│  ├─ fun-framework-core-context       │
│  ├─ Orika                            │
│  ├─ Jakarta Annotation API           │
│  └─ Spring Boot Autoconfigure        │
└──────────────────────────────────────┘
              │
┌─────────────┴──────────────────────────┐
│  fun-framework-springboot-web          │
│  ├─ fun-framework-core-springboot      │
│  ├─ fun-framework-core-log             │
│  ├─ fun-framework-core-verify          │
│  ├─ Jakarta Servlet API                │
│  ├─ Spring Web                         │
│  └─ Spring Boot WebMVC                 │
└────────────────────────────────────────┘
              │
┌─────────────┴──────────────────────────┐
│  fun-framework-springboot-ai           │
│  ├─ fun-framework-core-springboot      │
│  ├─ spring-ai-starter-mcp-server-webmvc│
│  └─ spring-ai-mcp-annotations          │
└────────────────────────────────────────┘
```

---

## Spring Boot 自动配置机制

### 配置文件双标准

#### Spring Boot 3.x 标准
文件路径: `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
```
# core-springboot 模块
config.com.github.fanzezhen.fun.framework.core.springboot.FunCoreSpringbootStarterAutoConfiguration
thread.com.github.fanzezhen.fun.framework.core.springboot.FunCoreThreadAutoConfiguration

# springboot-web 模块
com.github.fanzezhen.fun.framework.core.springboot.web.config.FunCoreSpringbootWebAutoConfiguration

# springboot-ai 模块
com.github.fanzezhen.fun.framework.core.springboot.web.config.FunCoreSpringbootAiAutoConfiguration
```

#### Spring Boot 2.x 兼容
文件路径: `META-INF/spring.factories`
```properties
# core-springboot 模块
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
config.com.github.fanzezhen.fun.framework.core.springboot.FunCoreSpringbootStarterAutoConfiguration,\
thread.com.github.fanzezhen.fun.framework.core.springboot.FunCoreThreadAutoConfiguration

# springboot-web 模块
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.github.fanzezhen.fun.framework.core.springboot.web.config.FunCoreSpringbootWebAutoConfiguration

# springboot-ai 模块
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.github.fanzezhen.fun.framework.core.springboot.web.config.FunCoreSpringbootAiAutoConfiguration
```

### 配置类加载顺序
1. Spring Boot 启动扫描 `META-INF/spring` 目录
2. 根据版本选择对应配置文件格式
3. 加载自动配置类
4. 根据 `@Conditional` 条件决定是否激活
5. 执行 `@PostConstruct` 初始化方法

---

## 最佳实践

### 使用建议

#### 场景 1: 纯后端服务（无 Web）
```xml
<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-core-springboot</artifactId>
</dependency>
```

#### 场景 2: RESTful API 服务
```xml
<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-springboot-web</artifactId>
</dependency>
<!-- web 模块会传递依赖 core-springboot、core-log、core-verify 模块 -->
```

#### 场景 3: AI 增强应用
```xml
<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-springboot-ai</artifactId>
</dependency>
<!-- ai 模块会传递依赖 core-springboot 模块 -->
```

#### 场景 4: 完整核心功能
```xml
<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-core-all</artifactId>
</dependency>
<!-- 传递依赖所有核心模块（不包含 Web 和 AI 扩展） -->
```

### 扩展指南

#### 添加新的自动配置
1. 在对应模块创建 `@Configuration` 类
2. 添加到 `AutoConfiguration.imports` 文件
3. 添加到 `spring.factories` 文件（兼容老版本）
4. 编写单元测试验证自动装配

#### 添加新的子模块
1. 在父 `pom.xml` 中添加 `<module>` 声明
2. 创建子模块 `pom.xml`，继承父模块
3. 明确模块职责和依赖范围
4. 更新本文档的模块总览部分

---

## 设计决策记录

### 为什么创建 fun-framework-springboot 父模块？

**背景**: 原 `fun-framework-core-web` 模块混合了基础配置和 Web 专用功能，JWT 功能位于 `core-verify` 模块但依赖 Web 环境。

**问题**:
- Web 功能分散在 core 层级的多个模块中，职责不清晰
- JWT 拦截器依赖 Servlet API，但位于 verify 模块（应该是纯验证工具）
- 日志模块过度拆分（base/web），实际体量不需要
- 非 Web 应用被迫引入 Web 相关依赖

**决策**: 
1. 创建 `fun-framework-springboot` 父模块，下设 `springboot-web` 和 `springboot-ai` 子模块
2. 删除 `fun-framework-core-web` 模块，功能迁移到 `springboot-web`
3. 合并日志模块子模块为单一 `fun-framework-core-log` 模块
4. JWT 功能从 `core-verify` 迁移到 `springboot-web`

**收益**:
- SpringBoot 扩展功能集中管理，层次清晰
- Web 专用功能（异常处理、JWT、日志过滤、MVC 扩展）归属统一
- `core-verify` 回归纯验证工具定位，不再依赖 Web 环境
- 减少模块数量，简化依赖管理
- 按需引入，非 Web 应用不会引入 Web 依赖

**代价**:
- 破坏性变更：包名和依赖坐标变更，需要迁移
- 需要维护新的模块结构和文档

### 为什么保留双标准配置文件？

**背景**: Spring Boot 3.x 改用新的 `AutoConfiguration.imports` 格式。

**问题**: 现有用户可能使用 Spring Boot 2.x。

**决策**: 同时保留 `spring.factories` 和 `AutoConfiguration.imports`

**收益**:
- 兼容 Spring Boot 2.x 和 3.x
- 平滑升级路径

**代价**:
- 需维护两份配置文件
- 文件格式不一致

---

## 未来规划

### 短期目标
- [ ] 完善 ai 模块的 Spring AI 集成
- [ ] 补充各子模块的使用文档
- [ ] 提升单元测试覆盖率至 80%+

### 中期目标
- [ ] 添加可观测性模块 (Micrometer/OpenTelemetry)
- [ ] 添加安全增强模块 (认证/授权/加密)
- [ ] 引入 GraalVM Native Image 支持

### 长期愿景
- [ ] 构建完整的微服务框架
- [ ] 支持响应式编程范式
- [ ] 提供 CLI 脚手架工具

---

## 维护指南

### 文档更新时机
- 新增/删除模块时
- 修改模块职责时
- 变更依赖关系时
- 重大架构重构时

### 相关文档
- `changelog-260512-springboot-refactor.md` - SpringBoot 核心模块重构记录
- `changelog-260512-module-consolidation.md` - 模块整合与架构优化记录（本次变更）
- `doc/dev/提示词/专业提示词/后端脚手架.md` - 框架使用指南
- 各模块 `README.md` - 模块级文档

### 联系方式
- 项目负责人: fanzezhen
- GitHub: https://github.com/fanzezhen/fun-framework-java
