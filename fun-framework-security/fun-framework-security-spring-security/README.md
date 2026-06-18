fun-framework-security-spring-security
------------------------------------------
错误码格式 152**

Spring Security 安全组件集成

# 功能介绍

基于 Spring Security 实现的企业级安全认证：
- 用户认证
- 权限控制
- CSRF 防护
- Session 管理

# 快速开始

## 一、添加依赖

在 pom.xml 中添加以下依赖：

    <dependency>
        <groupId>com.github.fanzezhen</groupId>
        <artifactId>fun-framework-security-spring-security</artifactId>
    </dependency>

## 二、配置安全配置类

声明 `UserDetailsService` 后，框架自动装配 `FunSpringSecurityConfig`，
通过 `SecurityFilterChain` Bean 配置安全规则（Spring Security 6+ 推荐方式，
已弃用的 `WebSecurityConfigurerAdapter` 不再使用）。

如需自定义，可提供自己的 `SecurityFilterChain` Bean 覆盖默认配置。
