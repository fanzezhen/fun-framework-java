fun-framework-data-elasticsearch7-starter
------------------------------------------
错误码格式 122**

Elasticsearch 7.x 启动器，自动配置 Elasticsearch 7.x

# 功能介绍

自动配置 Elasticsearch 7.x 客户端，简化 ES 集成。
本模块基于 Spring Boot 自动配置机制，提供了开箱即用的 ES 7.x 支持。

# 快速开始

## 一、添加依赖

在 pom.xml 中添加以下依赖：

    <dependency>
        <groupId>com.github.fanzezhen</groupId>
        <artifactId>fun-framework-data-elasticsearch7-starter</artifactId>
    </dependency>

## 二、配置连接信息

在 application.yml 中配置 Elasticsearch 连接信息：

```properties
fun.data.elasticsearch.configs[0].index-prefix=
fun.data.elasticsearch.configs[0].uris=
fun.data.elasticsearch.configs[0].username=elastic
fun.data.elasticsearch.configs[0].password=
```

配置完成后即可自动注入 ElasticsearchTemplate 等 Bean
