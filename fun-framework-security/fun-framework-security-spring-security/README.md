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

继承 WebSecurityConfigurerAdapter 并配置安全规则
