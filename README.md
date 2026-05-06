# Fun Framework Java

<div align=”center”>

[![GitHub license](https://img.shields.io/github/license/fanzezhen/fun-framework-java)](https://github.com/fanzezhen/fun-framework-java/blob/master/LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Maven Central](https://img.shields.io/badge/Maven)](https://search.maven.org/search?q=g:com.github.fanzezhen)

基于 Spring Boot 4.0.5 的企业级通用功能组件库，提供开箱即用的后端基础设施解决方案

[快速开始](#快速开始) • [文档](doc/dev/提示词) • [示例项目](https://github.com/fanzezhen/demo) • [问题反馈](https://github.com/fanzezhen/fun-framework-java/issues)

</div>

---

## 📖 项目简介

Fun Framework Java 是一个持续迭代的开源企业级 Java 后端组件库，旨在将常用的通用功能组件化，减少实际工作中的重复开发任务。项目专注于后端组件实现，不包含 Web 前端页面。

### ✨ 核心特性

- 🚀 **现代技术栈**: 基于 Spring Boot 4.0.5、MyBatis Plus 3.5.16 等最新稳定版本
- 🔧 **开箱即用**: 提供缓存、日志、安全、数据访问等 20+ 个开箱即用的组件模块
- 🎯 **分层设计**: 遵循企业级分层架构规范 (Entity/BO/VO/Condition)
- 🔐 **安全增强**: 集成 Spring Security、Sa-Token 等多种安全框架支持
- 📊 **可观测性**: 内置日志追踪、操作审计、流量监控等能力
- 🌐 **云原生**: 支持 Nacos、Sentinel、Redis、Elasticsearch 等主流中间件
- 📦 **模块化**: 灵活的模块依赖管理，按需引入组件

### 🎯 适用场景

- 中小型互联网应用快速开发
- 企业内部管理系统基础框架
- 微服务架构基础组件库
- Spring Boot 项目脚手架

---

## 🌿 分支说明

| 分支名      | 说明                                             | 状态    |
|----------|------------------------------------------------|-------|
| master   | 主分支，基于 Spring Boot 4.0.5 持续迭代                 | ✅ 稳定 |
| lasted   | 最新实验分支，追踪 Spring Boot 最新版本                    | 🧪 实验 |
| backup/* | 历史版本备份，如 `backup/jdk8-before-upgrade` 为 JDK 8 版本 | 📦 归档 |

---

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- Spring Boot 4.0.5

### Maven 依赖

在您的 Spring Boot 项目 `pom.xml` 中添加依赖（根据需要选择模块）:

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-core-all</artifactId>
</dependency>
```

### 快速体验

使用 Docker Compose 一键启动开发环境所需中间件:

```bash
cd support/docker
docker-compose up -d
```

完整的使用示例请参考: [Demo 项目](https://github.com/fanzezhen/demo)

---

## 📁 项目结构

### 核心目录说明

| 路径                       | 说明                           |
|--------------------------|------------------------------|
| `fun-framework-*`        | 各功能模块                        |
| `support/docker/`        | Docker Compose 环境配置          |
| `support/docker/app/`    | Docker 各服务的配置文件和数据文件         |
| `support/standard/`      | 标准化参考文件                      |
| `doc/dev/提示词/`          | 项目文档和开发规范                    |
| `.agentdocs/`            | AI 代理工作文档 (任务跟踪、恢复计划等)      |

### Maven 模块树

```
fun-framework-java
├── fun-framework-core           # 核心模块集合
│   ├── fun-framework-core-model        # 数据模型和静态变量
│   ├── fun-framework-core-exception    # 异常处理
│   ├── fun-framework-core-cache        # 缓存抽象
│   ├── fun-framework-core-thread       # 线程组件
│   ├── fun-framework-core-context      # 上下文管理
│   ├── fun-framework-core-data         # 数据基础
│   ├── fun-framework-core-log          # 日志组件
│   ├── fun-framework-core-verify       # 验证组件
│   ├── fun-framework-core-web          # Web 基础组件
│   └── fun-framework-core-all          # 核心模块全家桶
├── fun-framework-data              # 数据访问层
│   ├── fun-framework-data-mp           # MyBatis Plus 增强
│   └── fun-framework-data-elasticsearch # Elasticsearch 支持
├── fun-framework-security          # 安全模块
│   ├── fun-framework-security-base     # 安全基础
│   ├── fun-framework-security-sa-token # Sa-Token 集成
│   └── fun-framework-security-spring-security # Spring Security 集成
├── fun-framework-proxy             # 代理模块
│   ├── fun-framework-proxy-core        # 代理核心
│   ├── fun-framework-proxy-fastjson    # Fastjson 代理
│   ├── fun-framework-proxy-mybatis     # MyBatis 代理
│   └── fun-framework-proxy-orika       # Orika 对象映射
├── fun-framework-cache-redis       # Redis 缓存实现
├── fun-framework-jasypt            # 配置文件加密
├── fun-framework-sentinel          # Sentinel 流量防控
├── fun-framework-spring-doc        # SpringDoc API 文档
└── fun-framework-api-count-redis   # API 调用统计
```

---

## 📦 模块详解

详见各模块中的 README.md

### 核心模块 (fun-framework-core-*)

| 模块名                                                                                                           | 描述                | 错误码      | 文档                                                                                   |
|---------------------------------------------------------------------------------------------------------------|-------------------|----------|--------------------------------------------------------------------------------------|
| [fun-framework-core-model](fun-framework-core/fun-framework-core-model)                                       | 数据模型和静态变量         | 10***    | [📖](fun-framework-core/fun-framework-core-model/README.md)                           |
| [fun-framework-core-exception](fun-framework-core/fun-framework-core-exception)                               | 异常处理和工具类          | 10***    | [📖](fun-framework-core/fun-framework-core-exception/README.md)                       |
| [fun-framework-core-cache](fun-framework-core/fun-framework-core-cache)                                       | 缓存抽象接口            | 10***    | [📖](fun-framework-core/fun-framework-core-cache/README.md)                           |
| [fun-framework-core-thread](fun-framework-core/fun-framework-core-thread)                                     | 线程池和异步任务          | 10***    | [📖](fun-framework-core/fun-framework-core-thread/README.md)                          |
| [fun-framework-core-context](fun-framework-core/fun-framework-core-context)                                   | 上下文管理 (用户、租户等)   | 10***    | [📖](fun-framework-core/fun-framework-core-context/README.md)                         |
| [fun-framework-core-data](fun-framework-core/fun-framework-core-data)                                         | 数据访问基础设施          | 10***    | [📖](fun-framework-core/fun-framework-core-data/README.md)                            |
| [fun-framework-core-log-base](fun-framework-core/fun-framework-core-log/fun-framework-core-log-base)          | 日志增强基础            | 10***    | [📖](fun-framework-core/fun-framework-core-log/fun-framework-core-log-base/README.md) |
| [fun-framework-core-log-web](fun-framework-core/fun-framework-core-log/fun-framework-core-log-web)            | Web 日志拦截和追踪       | 10***    | [📖](fun-framework-core/fun-framework-core-log/fun-framework-core-log-web/README.md)  |
| [fun-framework-core-verify](fun-framework-core/fun-framework-core-verify)                                     | 参数验证和校验           | 10***    | [📖](fun-framework-core/fun-framework-core-verify/README.md)                          |
| [fun-framework-core-web](fun-framework-core/fun-framework-core-web)                                           | Web 基础能力 (统一响应等)  | 10***    | [📖](fun-framework-core/fun-framework-core-web/README.md)                             |
| [fun-framework-core-all](fun-framework-core/fun-framework-core-all)                                           | 核心模块全家桶 (一站式引入)   | 10***    | [📖](fun-framework-core/fun-framework-core-all/README.md)                             |

### 数据访问模块 (fun-framework-data-*)

| 模块名                                                                                                                                  | 描述                   | 错误码      | 文档                                                                                                     |
|--------------------------------------------------------------------------------------------------------------------------------------|----------------------|----------|--------------------------------------------------------------------------------------------------------|
| [fun-framework-data-mp-starter](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-starter)                             | MyBatis Plus 增强 Starter | 120**   | [📖](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-starter/README.md)                 |
| [fun-framework-data-mp-trace](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-trace)                                 | 数据库操作日志追踪            | 1211*     | [📖](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-trace/README.md)                   |
| [fun-framework-data-mp-trace-impl](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-trace-impl)                       | 操作日志追踪实现             | 1212*     | [📖](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-trace-impl/README.md)              |
| [fun-framework-data-elasticsearch-base](fun-framework-data/fun-framework-data-elasticsearch/fun-framework-data-elasticsearch-base)   | Elasticsearch 抽象基础    | 122**    | [📖](fun-framework-data/fun-framework-data-elasticsearch/fun-framework-data-elasticsearch-base/README.md) |
| [fun-framework-data-elasticsearch7-starter](fun-framework-data/fun-framework-data-elasticsearch/fun-framework-data-elasticsearch7-starter) | Elasticsearch 7 Starter | 122**    | [📖](fun-framework-data/fun-framework-data-elasticsearch/fun-framework-data-elasticsearch7-starter/README.md) |

### 安全模块 (fun-framework-security-*)

| 模块名                                                                                                            | 描述                     | 错误码    | 文档                                                                                  |
|----------------------------------------------------------------------------------------------------------------|------------------------|--------|------------------------------------------------------------------------------------|
| [fun-framework-security-base](fun-framework-security/fun-framework-security-base)                              | 安全基础抽象                 | 150**  | [📖](fun-framework-security/fun-framework-security-base/README.md)                 |
| [fun-framework-security-sa-token](fun-framework-security/fun-framework-security-sa-token)                      | Sa-Token 集成 (轻量级认证授权)  | 151**  | [📖](fun-framework-security/fun-framework-security-sa-token/README.md)             |
| [fun-framework-security-spring-security](fun-framework-security/fun-framework-security-spring-security)        | Spring Security 集成     | 152**  | [📖](fun-framework-security/fun-framework-security-spring-security/README.md)      |

### 代理模块 (fun-framework-proxy-*)

| 模块名                                                                                  | 描述                   | 错误码    | 文档                                                                   |
|--------------------------------------------------------------------------------------|----------------------|--------|----------------------------------------------------------------------|
| [fun-framework-proxy-core](fun-framework-proxy/fun-framework-proxy-core)            | 代理模式基础框架             | 180**  | [📖](fun-framework-proxy/fun-framework-proxy-core/README.md)         |
| [fun-framework-proxy-fastjson](fun-framework-proxy/fun-framework-proxy-fastjson)    | Fastjson 序列化代理       | 181**  | [📖](fun-framework-proxy/fun-framework-proxy-fastjson/README.md)     |
| [fun-framework-proxy-mybatis](fun-framework-proxy/fun-framework-proxy-mybatis)      | MyBatis 拦截器代理        | 182**  | [📖](fun-framework-proxy/fun-framework-proxy-mybatis/README.md)      |
| [fun-framework-proxy-orika](fun-framework-proxy/fun-framework-proxy-orika)          | Orika 对象映射代理         | 183**  | [📖](fun-framework-proxy/fun-framework-proxy-orika/README.md)        |

### 其他模块

| 模块名                                                                                  | 描述                       | 错误码    | 文档                                                                   |
|--------------------------------------------------------------------------------------|--------------------------|--------|----------------------------------------------------------------------|
| [fun-framework-cache-redis](fun-framework-cache-redis)                              | Redis 缓存实现              | 11***  | [📖](fun-framework-cache-redis/README.md)                            |
| [fun-framework-api-count-redis](fun-framework-api-count-redis)                      | API 调用统计 (基于 Redis)     | 13***  | [📖](fun-framework-api-count-redis/README.md)                        |
| [fun-framework-jasypt](fun-framework-jasypt)                                        | 配置文件加密 (Jasypt)         | 14***  | [📖](fun-framework-jasypt/README.md)                                 |
| [fun-framework-sentinel](fun-framework-sentinel)                                    | Sentinel 流量防控和熔断        | 16***  | [📖](fun-framework-sentinel/README.md)                               |
| [fun-framework-spring-doc](fun-framework-spring-doc)                                | SpringDoc API 文档生成      | 17***  | [📖](fun-framework-spring-doc/README.md)                             |
| [support](support)                                                                  | 辅助工具和标准文件 (非 Maven 模块) | -      | [📖](support/README.md)                                              |

---

## 🛠️ 技术栈

### 核心框架

| 框架                 | 版本      | 说明                  |
|--------------------|---------|---------------------|
| Spring Boot        | 4.0.5   | 核心应用框架              |
| Spring Cloud       | 2025.1.1 | 微服务基础设施             |
| MyBatis Plus       | 3.5.16  | 持久层增强框架             |
| Sa-Token           | 1.45.0  | 轻量级认证授权框架           |
| Sentinel           | 1.8.9   | 流量防控和熔断降级           |

### 中间件支持

| 组件                | 版本      | 说明                  |
|-------------------|---------|---------------------|
| Redis             | -       | 缓存、分布式锁、会话管理        |
| Elasticsearch     | 7.x     | 全文检索和日志分析           |
| Nacos             | 2025.1.0.0 | 配置中心和服务发现           |
| MySQL             | 9.5.0   | 关系型数据库              |

### 工具库

| 工具                | 版本      | 说明                  |
|-------------------|---------|---------------------|
| Hutool            | 5.8.44  | Java 工具类库           |
| Guava             | 33.5.0-jre | Google 核心库         |
| Fastjson2         | 2.0.60  | JSON 序列化/反序列化       |
| Apache POI        | 5.5.1   | Office 文档处理         |
| Orika             | 1.5.4   | 对象映射框架              |

---

## 📚 开发规范

本项目遵循严格的企业级开发规范,详见:

- [全局提示词](doc/dev/提示词/全局提示词.md) - 通用工作原则、沟通规范、文档管理
- [Java 规范](doc/dev/提示词/专业提示词/java.md) - Java 核心规范 (NPE、并发、GC 等)
- [Spring Boot 规范](doc/dev/提示词/专业提示词/java-springboot.md) - Spring Boot 专项规范
- [后端规范](doc/dev/提示词/专业提示词/后端.md) - 后端 API 设计、分层对象规范

### 关键设计原则

#### 分层对象规范

```
Controller → Service: 单实体用 Entity, 多参数/复杂查询用 Condition
Service → Dao:        新增/编辑传 Entity, 查询根据参数数量决定
Dao → Service:        普通查询返回 Entity, 聚合/连表查询返回 BO
Service → Controller: 统一使用 BO 返回业务数据
```

#### 代码质量要求

- 单个代码文件 ≤ 1000 行
- 单个方法 ≤ 80 行
- 优先复用现有成熟代码
- 保持向后兼容性

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request!

### 开发流程

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交变更 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

### 编码规范

- 遵循项目既有的代码风格
- 编写必要的单元测试
- 更新相关文档
- 提交信息应清晰描述变更内容和动机

---

## 📄 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证

```
Copyright 2018-2026 fanzezhen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 📞 联系方式

- **作者**: fanzezhen
- **邮箱**: fanzezhen@foxmail.com
- **GitHub**: [@fanzezhen](https://github.com/fanzezhen)
- **Issues**: [提交问题](https://github.com/fanzezhen/fun-framework-java/issues)

---

## 🌟 Star History

如果这个项目对您有帮助,欢迎给个 Star ⭐️

[![Star History Chart](https://api.star-history.com/svg?repos=fanzezhen/fun-framework-java&type=Date)](https://star-history.com/#fanzezhen/fun-framework-java&Date)

---

<div align="center">

**[⬆ 回到顶部](#fun-framework-java)**

Made with ❤️ by [fanzezhen](https://github.com/fanzezhen)

</div>