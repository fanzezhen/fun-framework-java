support
------------------------------------------
非maven模块的文件

# 文件说明

- support
  - docker
    - docker-compose.yml： docker-compose文件，可以使用docker一键搭建运行环境中所需的服务
    - app：docker-compose中各个服务的配置文件或数据文件
      - [jhipster-registry](docker%2Fapp%2Fjhipster-registry)： 基于 Spring Cloud 的注册中心
      - [mongodb](docker%2Fapp%2Fmongodb)： mongodb数据库配置
      - [mysql](docker%2Fapp%2Fmysql)： MySQL数据库配置+初始化文件
      - [nacos](docker%2Fapp%2Fnacos)： nacos配置
      - [nginx](docker%2Fapp%2Fnginx)： nginx配置
      - [redis](docker%2Fapp%2Fredis)： redis配置
  - standard：标准文件，用作数据参考
