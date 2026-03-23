fun-framework-data-elasticsearch-base
------------------------------------------
错误码格式 122**

Elasticsearch 基础组件，提供注解、序列化、模板等核心功能

# 功能介绍

## 核心组件

### annotation（注解）
提供常用的 ES 操作注解，包括聚合、高亮、字段映射等

### adapter（适配器）
ES 客户端适配器，支持不同版本的 ES 客户端适配

### config（配置）
ES 自动配置类，支持自动装配

### constant（常量）
ES 相关常量定义，包括聚合字段、脚本常量等

### dao（数据访问对象）
基础 DAO 接口定义，提供通用的 ES 操作方法

### deserializer（反序列化器）
自定义反序列化器，支持注解驱动的结果反序列化

### serializer（序列化器）
自定义序列化器，支持文档序列化

### model（模型）
ES 文档模型定义，包括基础文档、搜索结果等

### template（模板）
ES 操作模板，封装常用的 ES 操作

### enums（枚举）
ES 相关枚举定义

# 快速开始

## 一、添加依赖

在 pom.xml 中添加以下依赖：

    <dependency>
        <groupId>com.github.fanzezhen</groupId>
        <artifactId>fun-framework-data-elasticsearch-base</artifactId>
    </dependency>

## 二、使用注解

在实体类上使用 ES 相关注解定义文档映射，例如：

- @Aggregation：定义聚合查询
- @HighlightField：定义高亮字段
- @BucketField：定义分桶字段

## 三、配置连接信息

在 application.yml 中配置 Elasticsearch 连接信息：

```properties
fun.data.elasticsearch.configs[0].index-prefix=
fun.data.elasticsearch.configs[0].uris=
fun.data.elasticsearch.configs[0].username=elastic
fun.data.elasticsearch.configs[0].password=
```
