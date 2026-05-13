# fun-framework-springboot

Spring Boot 扩展功能模块（父模块）

## 模块概览

本模块是 Spring Boot 扩展功能的父模块，下设两个子模块：

```
fun-framework-springboot/
├── fun-framework-springboot-web     # Web 应用功能
└── fun-framework-springboot-ai      # AI 功能扩展
```

---

## 子模块说明

### fun-framework-springboot-web

**定位**: Web 应用专用功能集成

**主要功能**:
- ✅ 全局异常处理
- ✅ JWT 认证拦截
- ✅ Web 日志打印
- ✅ MVC 扩展配置
- ✅ 响应体包装
- ✅ 参数校验注解
- ✅ Servlet 工具类

**适用场景**:
- RESTful API 服务
- 传统 Web 应用
- 微服务 Web 节点

**快速开始**:
```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-springboot-web</artifactId>
</dependency>
```

**详细文档**: [fun-framework-springboot-web/README.md](fun-framework-springboot-web/README.md)

---

### fun-framework-springboot-ai

**定位**: Spring AI 功能扩展

**主要功能**:
- 🔄 Spring AI MCP Server 集成
- 🔄 AI Annotations 支持
- 🔄 统一的 AI 服务抽象（规划中）
- 🔄 多模型提供商切换支持（规划中）

**当前状态**: 基础骨架已建立，具体功能实现中

**适用场景**:
- AI 增强应用
- 智能对话系统
- AI 辅助决策系统
- 自然语言处理应用

**快速开始**:
```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-springboot-ai</artifactId>
</dependency>
```

**详细文档**: [fun-framework-springboot-ai/README.md](fun-framework-springboot-ai/README.md)

---

## 模块依赖关系

```
应用模块
    ├─ fun-framework-springboot-web (Web 应用)
    │   ├─ fun-framework-core-springboot (基础配置)
    │   ├─ fun-framework-core-log (日志)
    │   └─ fun-framework-core-verify (验证)
    │
    └─ fun-framework-springboot-ai (AI 应用)
        └─ fun-framework-core-springboot (基础配置)
```

---

## 快速选择指南

### 我应该使用哪个模块？

| 应用类型 | 推荐模块 | 说明 |
|---------|---------|------|
| **纯后端服务**（定时任务、消息消费者） | [fun-framework-core-springboot](../fun-framework-core/fun-framework-core-springboot/README.md) | 无 Web 依赖，提供基础配置和工具 |
| **RESTful API 服务** | `fun-framework-springboot-web` | 完整的 Web 应用功能 |
| **传统 Web 应用** | `fun-framework-springboot-web` | 包含异常处理、日志、MVC 扩展 |
| **AI 增强应用** | `fun-framework-springboot-ai` | Spring AI 集成，支持多模型 |
| **AI + Web 应用** | `fun-framework-springboot-web` + `fun-framework-springboot-ai` | 两个模块可同时使用 |

---

## 设计原则

### 1. 职责清晰

- **core-springboot**: 基础配置和工具，不依赖 Web 环境
- **springboot-web**: Web 应用专用功能
- **springboot-ai**: AI 功能扩展

### 2. 按需引入

各模块独立发布，应用可按需选择：
- 不需要 Web 功能 → 只引入 core-springboot
- 需要 Web 功能 → 引入 springboot-web（会传递依赖 core-springboot）
- 需要 AI 功能 → 引入 springboot-ai（会传递依赖 core-springboot）

### 3. 零配置启动

所有子模块均提供自动配置，添加依赖后自动生效，无需手动配置。

---

## 模块架构

### 模块功能

#### springboot-web 模块：
- 全局异常处理器 (`DefaultExceptionHandler`)
- MVC 扩展配置
- 响应体包装器
- 参数校验注解
- Servlet 工具类
- JWT 认证功能（拦截器、服务接口）
- Web 日志打印过滤器

#### springboot-ai 模块：
- Spring AI 2.0.0-M6 集成
- MCP Server 支持
- AI Annotations 支持

---

## 版本信息

- **当前版本**: 4.0.6.20260512
- **Spring Boot 版本**: 4.0.6
- **Spring AI 版本**: 2.0.0-M6

---

## 相关文档

- [fun-framework-springboot-web/README.md](fun-framework-springboot-web/README.md) - Web 应用功能详细文档
- [fun-framework-springboot-ai/README.md](fun-framework-springboot-ai/README.md) - AI 功能扩展详细文档
- [fun-framework-core-springboot/README.md](../fun-framework-core/fun-framework-core-springboot/README.md) - 基础配置文档
- [后端架构设计文档](../.agentdocs/backend/architecture.md) - 完整架构说明
- [模块整合变更记录](../.agentdocs/backend/changelog-260512-module-consolidation.md) - 详细变更记录

---

## 贡献与反馈

如有问题或建议，请提交 Issue 至 GitHub/Gitee 仓库。
