# 项目文档索引

## 后端文档
- `backend/architecture.md` - 后端架构设计文档，记录模块划分、依赖关系、设计决策（修改任何后端架构时必读）
- `backend/changelog-260512-springboot-refactor.md` - SpringBoot 核心模块重构记录（2026-05-12）
- `backend/changelog-260512-module-consolidation.md` - 模块整合与架构优化记录（2026-05-12）：日志模块合并、创建 springboot 父模块、JWT 功能迁移

## 开发文档（项目中）
- `doc/dev/提示词/专业提示词/后端脚手架.md` - 框架能力索引和使用指南（已更新 SpringBoot 模块拆分说明）

## 全局重要记忆

### 模块化原则（2026-05-12 生效）
- `fun-framework-core-springboot` 已从单一 jar 拆分为三个子模块：
  - `fun-framework-core-springboot-base`: 基础配置和工具，无 Web 依赖
  - `fun-framework-core-springboot-web`: Web 应用功能（异常处理、Web 配置）
  - `fun-framework-core-springboot-ai`: AI 功能扩展（预留，基于 Spring AI 2.0.0-M6）
- 创建 `fun-framework-springboot` 父模块，整合 Web 和 AI 功能：
  - `fun-framework-springboot-web`: Web 应用专用功能（全局异常处理、JWT 认证、Web 日志、MVC 扩展、响应包装、参数校验）
  - `fun-framework-springboot-ai`: Spring AI 集成（MCP Server、AI Annotations）
- 日志模块简化：
  - 合并 `fun-framework-core-log-base` 和 `fun-framework-core-log-web` 为单一 `fun-framework-core-log` 模块
  - Web 相关日志功能（如 `FunLogPrintFilter`）迁移到 `springboot-web` 模块
- 删除 `fun-framework-core-web` 模块，功能迁移到 `fun-framework-springboot-web`

### 依赖选择策略
- 纯后端服务（定时任务、消息消费者）→ 使用 `fun-framework-core-springboot`
- RESTful API 服务 → 使用 `fun-framework-springboot-web`（会传递依赖 core-springboot）
- AI 应用 → 使用 `fun-framework-springboot-ai`（会传递依赖 core-springboot）
- 需要完整核心功能 → 使用 `fun-framework-core-all`（会传递依赖所有核心模块）

### 自动配置规范
- Spring Boot 3+ 使用 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- 保留旧版 `META-INF/spring.factories` 以兼容 Spring Boot 2.x
- 所有子模块均提供双标准配置文件

### 测试规范
- 优先使用 JUnit 5 (jupiter)
- 对象映射工具测试需在 `@BeforeEach` 方法中手动初始化 MapperFacade
- 测试类需要完整包名引用工具类（如 `com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil`）

### 线程池管理
- Spring Boot 环境使用 `ThreadPoolTaskExecutorRepository`（位于 `fun-framework-core-springboot` 模块）
- 非 Spring 环境使用 `ThreadPoolExecutorRepository`（位于 `core-thread` 模块）
- 线程池自动配置 TTL 上下文传递装饰器
- 可通过 `addDecorator()` 动态添加自定义装饰器

### JWT 认证功能
- JWT 相关类已从 `fun-framework-core-verify` 迁移到 `fun-framework-springboot-web`
- 包名变更: `core.verify.jwt.*` → `core.springboot.web.jwt.*`
- 主要类: `FunJwtHandlerInterceptor`、`FunCoreVerifyTokenApi`、`JwtService`
- 依赖 `fun-framework-core-verify` 模块提供的验证工具

### Web 日志功能
- Web 日志过滤器位于 `fun-framework-springboot-web` 模块
- 主要类: `FunLogPrintFilter`、`LoggingHttpServletRequestWrapper`、`LoggingHttpServletResponseWrapper`
- 支持请求/响应体打印序列化（`MultipartFilePrintSerializer`）
- 自动记录请求 TraceId
