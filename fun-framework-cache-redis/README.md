fun-framework-cache-redis
------------------------------------------
Redis缓存模块

# 快速开始

## 1. 添加依赖

```xml

<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-cache-redis</artifactId>
</dependency>
```

## 2. 使用注解

```java
package demo;

import com.github.fanzezhen.fun.framework.redis.EnableFunRedisAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFunRedisAutoConfiguration
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(SysWebApplication.class, args);
  }
}

```
# 功能介绍
## CacheService
使用redis缓存实现了[fun-framework-core](..%2Ffun-framework-core)中的CacheService

## LockService
基于Redisson实现了[fun-framework-core](..%2Ffun-framework-core)中的LockService，使用前需要先引入Redisson