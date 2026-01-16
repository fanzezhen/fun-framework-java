fun-framework-java
------------------------------------------
此项目目的是为了将常用的通用功能组件化，减负实际工作中的开发任务。
此项目仅后端组件，无web页面，具体应用示例可见 https://github.com/fanzezhen/demo
分支

| 分支名      | 说明                                        |
|----------|-------------------------------------------|
| master   | 主分支，目前基于“springboot3.5.x”持续迭代             |
| lasted   | 最新分支，基于最新的springboot版本                    |
| backup/* | 备份，例如“backup/jdk8-before-upgrade”为jdk8的版本 |

# 文件说明

|                                                                                                           | 说明                                        |
|-----------------------------------------------------------------------------------------------------------|-------------------------------------------|
| fun-framework-*                                                                                           | 子模块                                       |
| [support](support)/[docker](support%2Fdocker)/[docker-compose.yml](support%2Fdocker%2Fdocker-compose.yml) | docker-compose文件，可以使用docker一键搭建运行环境中所需的服务 |
| [support](support)/[docker](support%2Fdocker)/[app](support%2Fdocker%2Fapp)/*                             | docker-compose中各个服务的配置文件或数据文件             |
| [support](support)/[standard](support%2Fstandard)/*                                                       | 标准文件，用作数据参考                               |

# 结构说明

详见各模块中的 README.md

| 模块类          | 模块名                                                                                                                                        | 描述                   | 错误码前缀 | 手册                                                                                                                   |
|--------------|--------------------------------------------------------------------------------------------------------------------------------------------|----------------------|-------|----------------------------------------------------------------------------------------------------------------------|
| 核心模型         | [fun-framework-core-model](fun-framework-core/fun-framework-core-model)                                                                    | 各种数据模型和静态变量          | 900   | [README.md](fun-framework-core/fun-framework-core-model/README.md)                                                   |
| 异常处理         | [fun-framework-core-exception](fun-framework-core/fun-framework-core-exception)                                                            | 常见异常的处理类和相关工具类       | 901   | [README.md](fun-framework-core/fun-framework-core-exception/README.md)                                               |
| 缓存组件         | [fun-framework-core-cache](fun-framework-core/fun-framework-core-cache)                                                                    | 缓存组件                 | 90200 | [README.md](fun-framework-core/fun-framework-core-cache/README.md)                                                   |
| 缓存组件         | [fun-framework-cache-redis](fun-framework-cache-redis)                                                                                     | 基于Redis的缓存组件         | 90201 | [README.md](fun-framework-cache-redis/README.md)                                                                     |
| 线程组件         | [fun-framework-core-thread](fun-framework-core/fun-framework-core-thread)                                                                  | 线程组件                 | 903   | [README.md](fun-framework-core/fun-framework-core-thread/README.md)                                                  |
| 上下文组件        | [fun-framework-core-context](fun-framework-core/fun-framework-core-context)                                                                | 上下文组件                | 904   | [README.md](fun-framework-core/fun-framework-core-context/README.md)                                                 |
| 数据基础组件       | [fun-framework-core-data](fun-framework-core/fun-framework-core-data)                                                                      | 数据组件                 | 905   | [README.md](fun-framework-core/fun-framework-core-data/README.md)                                                    |
| 日志组件         | [fun-framework-core-log-base](fun-framework-core/fun-framework-core-log/fun-framework-core-log-base)                                       | 日志基础组件               | 90600 | [README.md](fun-framework-core/fun-framework-core-log/fun-framework-core-log-base/README.md)                         |
| 日志组件         | [fun-framework-core-log-web](fun-framework-core/fun-framework-core-log/fun-framework-core-log-web)                                         | 日志web组件              | 90601 | [README.md](fun-framework-core/fun-framework-core-log/fun-framework-core-log-web/README.md)                          |
| 验证组件         | [fun-framework-core-verify](fun-framework-core/fun-framework-core-verify)                                                                  | 验证组件                 | 907   | [README.md](fun-framework-core/fun-framework-core-verify/README.md)                                                  |
| Web组件        | [fun-framework-core-web](fun-framework-core/fun-framework-core-web)                                                                        | web组件                | 908   | [README.md](fun-framework-core/fun-framework-core-web/README.md)                                                     |
| Mybatis-plus | [fun-framework-data-mp-starter](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-starter)                                    | Mybatis-plus         | 90900 | [README.md](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-starter/README.md)                        |
| Mybatis-plus | [fun-framework-data-mp-trace](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-trace)                                        | Mybatis-plus  操作日志组件 | 90901 | [README.md](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-trace/README.md)                          |
| Mybatis-plus | [fun-framework-data-mp-trace-impl](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-trace-impl)                              | Mybatis-plus  操作日志组件 | 90902 | [README.md](fun-framework-data/fun-framework-data-mp/fun-framework-data-mp-trace-impl/README.md)                     |
| ES组件         | [fun-framework-data-elasticsearch-base](fun-framework-data/fun-framework-data-elasticsearch/fun-framework-data-elasticsearch-base)         | ES                   | 91000 | [README.md](fun-framework-data/fun-framework-data-elasticsearch/fun-framework-data-elasticsearch-base/README.md)     |
| ES组件         | [fun-framework-data-elasticsearch7-starter](fun-framework-data/fun-framework-data-elasticsearch/fun-framework-data-elasticsearch7-starter) | ES                   | 91001 | [README.md](fun-framework-data/fun-framework-data-elasticsearch/fun-framework-data-elasticsearch7-starter/README.md) |
| 配置加密         | [fun-framework-jasypt](fun-framework-jasypt)                                                                                               | 主要用于对配置文件加密          | 911   | [README.md](fun-framework-jasypt/README.md)                                                                          |
| 代理组件         | [fun-framework-proxy-core](fun-framework-proxy/fun-framework-proxy-core)                                                                   | 代理基础组件               | 91200 | [README.md](fun-framework-proxy/fun-framework-proxy-core/README.md)                                                  |
| 代理组件         | [fun-framework-proxy-fastjson](fun-framework-proxy/fun-framework-proxy-fastjson)                                                           | 代理fastjson组件         | 91201 | [README.md](fun-framework-proxy/fun-framework-proxy-fastjson/README.md)                                              |
| 代理组件         | [fun-framework-proxy-mybatis](fun-framework-proxy/fun-framework-proxy-mybatis)                                                             | 代理mybatis组件          | 91202 | [README.md](fun-framework-proxy/fun-framework-proxy-mybatis/README.md)                                               |
| 代理组件         | [fun-framework-proxy-orika](fun-framework-proxy/fun-framework-proxy-orika)                                                                 | 代理orika组件            | 91203 | [README.md](fun-framework-proxy/fun-framework-proxy-orika/README.md)                                                 |
| 安全组件         | [fun-framework-security-base](fun-framework-security/fun-framework-security-base)                                                          | 安全基础组件               | 91300 | [README.md](fun-framework-security/fun-framework-security-base/README.md)                                            |
| 安全组件         | [fun-framework-security-sa-token](fun-framework-security/fun-framework-security-sa-token)                                                  | 安全sa-token组件         | 91301 | [README.md](fun-framework-security/fun-framework-security-sa-token/README.md)                                        |
| 安全组件         | [fun-framework-security-spring-security](fun-framework-security/fun-framework-security-spring-security)                                    | 安全spring-security组件  | 91302 | [README.md](fun-framework-security/fun-framework-security-spring-security/README.md)                                 |
| 流量防控         | [fun-framework-sentinel](fun-framework-sentinel)                                                                                           | sentinel防控模块         | 914   | [README.md](fun-framework-sentinel/README.md)                                                                        |
| 接口文档         | [fun-framework-spring-doc](fun-framework-spring-doc)                                                                                       | 用于生成接口文档-基于springdoc | 915   | [README.md](fun-framework-spring-doc/README.md)                                                                      |
| 辅助模块         | [support](support)                                                                                                                         | 非maven模块的文件          |       | [README.md](support%2FREADME.md)                                                                                     |