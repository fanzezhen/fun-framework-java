fun-framework-security
------------------------------------------
错误码格式 15***

安全认证模块，提供多种安全框架集成方案

# 子模块说明

| 模块名 | 描述 | 错误码格式 |
|--------|------|-----------|
| [fun-framework-security-base](fun-framework-security-base) | 安全基础组件，定义统一接口和抽象类 | 91300 |
| [fun-framework-security-sa-token](fun-framework-security-sa-token) | Sa-Token 安全组件集成 | 91301 |
| [fun-framework-security-spring-security](fun-framework-security-spring-security) | Spring Security 安全组件集成 | 91302 |

# 快速开始

根据项目需求选择合适的安全组件：
- 轻量级应用推荐使用 Sa-Token
- 企业级应用推荐使用 Spring Security
