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

| 模块名                                                    | 描述                   | 手册                                                 |
|--------------------------------------------------------|----------------------|----------------------------------------------------|
| [fun-framework-all](fun-framework-all)                 | 用于简化引用所有模块           | [README.md](fun-framework-all%2FREADME.md)         |
| [fun-framework-core](fun-framework-core)               | 核心基础代码               | [README.md](fun-framework-core%2FREADME.md)        |
| [fun-framework-cache-redis](fun-framework-cache-redis) | redis缓存模块            | [README.md](fun-framework-cache-redis%2FREADME.md) |
| [fun-framework-jasypt](fun-framework-jasypt)           | 主要用于对配置文件加密          | [README.md](fun-framework-jasypt%2FREADME.md)      |
| [fun-framework-mp](fun-framework-mp)                   | MyBatis-Plus         | [README.md](fun-framework-mp%2FREADME.md)          |
| [fun-framework-sentinel](fun-framework-sentinel)       | sentinel防控模块         | [README.md](fun-framework-sentinel%2FREADME.md)    |
| [fun-framework-spring-doc](fun-framework-spring-doc)   | 用于生成接口文档-基于springdoc | [README.md](fun-framework-spring-doc%2FREADME.md)  |
| [fun-framework-web](fun-framework-web)                 | web模块，通用的mvc配置       | [README.md](fun-framework-web%2FREADME.md)         |
| [support](support)                                     | 非maven模块的文件          | [README.md](support%2FREADME.md)                   |

