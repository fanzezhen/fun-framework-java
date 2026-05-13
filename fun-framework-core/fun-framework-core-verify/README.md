# fun-framework-core-verify

验证工具组件

错误码格式: 10***

## 功能介绍

本模块提供纯验证工具功能，包括：
- 图片验证码生成
- 加密/解密工具
- 数据校验工具

## 模块定位

本模块专注于提供**纯验证工具**，不依赖 Web 环境，适用于所有类型的应用。

## JWT 认证功能

如果需要 JWT 认证功能，请使用 [fun-framework-springboot-web](../../../fun-framework-springboot/fun-framework-springboot-web/README.md) 模块，其中包含：
- `FunJwtHandlerInterceptor` - JWT 请求拦截器
- `FunCoreVerifyTokenApi` - Token 验证 API
- `JwtService` - JWT 服务接口
- `FunDefaultJwtServiceImpl` - JWT 默认实现

JWT 功能位于 `com.github.fanzezhen.fun.framework.core.springboot.web.jwt.*` 包。

## 相关模块

- [fun-framework-springboot-web](../../../fun-framework-springboot/fun-framework-springboot-web/README.md) - Web 应用功能（包含 JWT 认证）
- [fun-framework-core-model](../fun-framework-core-model/README.md) - 核心数据模型
