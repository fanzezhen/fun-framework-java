# fun-framework-springboot-ai

Spring Boot AI 功能扩展模块

## 功能概览

本模块提供 Spring AI 集成和 AI 相关功能扩展，包括：
- 🔄 Spring AI MCP Server 集成
- 🔄 AI Annotations 支持
- 🔄 统一的 AI 服务抽象（规划中）
- 🔄 多模型提供商切换支持（规划中）

**当前状态**: 基础骨架已建立，具体功能实现中

---

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-springboot-ai</artifactId>
</dependency>
```

**依赖说明**: 本模块会自动传递依赖以下组件：
- `fun-framework-core-springboot` - 基础配置
- `spring-ai-starter-mcp-server-webmvc` - Spring AI MCP Server（基于 WebMVC）
- `spring-ai-mcp-annotations` - Spring AI Annotations

### 自动配置原理

通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 自动加载 `FunCoreSpringbootAiAutoConfiguration`。

**兼容性说明**: 同时保留 `META-INF/spring.factories` 配置文件以兼容 Spring Boot 2.x

---

## Spring AI 版本

本模块基于 **Spring AI 2.0.0-M6** 构建，支持最新的 AI 功能特性。

Spring AI BOM 在父 pom.xml 中统一管理：
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-bom</artifactId>
    <version>2.0.0-M6</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

---

## 核心功能（规划中）

### 1. MCP Server 集成

基于 Spring AI MCP Server 提供 AI 模型上下文协议支持。

**待实现功能**:
- AI 模型上下文管理
- 多模型提供商适配
- 统一的 AI 调用接口

### 2. AI Annotations 支持

使用 Spring AI Annotations 简化 AI 功能开发。

**待实现功能**:
- `@AiService` - AI 服务声明
- `@AiPrompt` - 提示词模板
- `@AiFunction` - AI 函数调用

### 3. 统一 AI 服务抽象

提供统一的 AI 服务接口，屏蔽底层模型提供商差异。

**待实现功能**:
- 文本生成
- 对话管理
- 嵌入向量生成
- 图像生成

---

## 使用示例（待补充）

```java
// 示例代码将在功能实现后补充
```

---

## 配置属性（待补充）

```properties
# AI 相关配置
# spring.ai.openai.api-key=your-api-key
```

---

## 模块定位

本模块专注于提供 **Spring Boot AI 功能扩展**，适用于：
- AI 增强应用
- 智能对话系统
- AI 辅助决策系统
- 自然语言处理应用

如果您的应用不需要 AI 功能，请使用：
- [fun-framework-core-springboot](../../fun-framework-core/fun-framework-core-springboot/README.md) - 纯后端服务
- [fun-framework-springboot-web](../fun-framework-springboot-web/README.md) - Web 应用

---

## 开发计划

### 短期目标（1-2周）
- [ ] 完善 MCP Server 集成
- [ ] 实现基础的 AI 服务接口
- [ ] 补充使用示例和文档

### 中期目标（1个月）
- [ ] 支持多种 AI 模型提供商（OpenAI、Azure OpenAI、本地模型）
- [ ] 实现对话管理功能
- [ ] 提供嵌入向量生成工具

### 长期目标（3个月）
- [ ] 支持 AI Agent 开发
- [ ] 提供 AI 可观测性工具
- [ ] 集成更多 AI 能力（图像、音频、视频）

---

## Java 9+ 兼容性

本模块依赖 Orika 进行对象映射，在 Java 9+ 需要开放反射权限：

```bash
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     -jar app.jar
```

Maven Surefire 插件已配置测试时自动开放权限。

---

## 相关资源

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)
- [fun-framework-core-springboot](../../fun-framework-core/fun-framework-core-springboot/README.md) - 基础配置
- [fun-framework-springboot-web](../fun-framework-springboot-web/README.md) - Web 应用功能
- [后端架构设计文档](../../.agentdocs/backend/architecture.md) - 完整架构说明

---

## 贡献与反馈

本模块正在积极开发中，欢迎贡献代码或提出建议。

如有问题或建议，请提交 Issue 至 GitHub/Gitee 仓库。
