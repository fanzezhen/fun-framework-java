fun-framework-proxy-core
------------------------------------------
错误码格式 180**

静态资源代理基础组件，提供代理核心功能和注解定义

# 功能介绍

## 核心功能

- 提供@ProxyField 注解用于标记需要代理的字段
- 定义代理核心接口和服务
- 支持多种序列化框架的代理实现

# 快速开始

## 一、添加依赖

在 pom.xml 中添加以下依赖：

    <dependency>
        <groupId>com.github.fanzezhen</groupId>
        <artifactId>fun-framework-proxy-core</artifactId>
    </dependency>

## 二、使用注解

在需要代理的字段上标注@ProxyField 注解
