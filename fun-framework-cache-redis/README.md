基于Redis实现的[fun-framework-core-cache](..%2Ffun-framework-core%2Ffun-framework-core-cache)
------------------------------------------
错误码前缀 1100**

# 功能介绍

## CacheService

使用redis缓存实现了[fun-framework-core](..%2Ffun-framework-core)
中的[CacheService](..%2Ffun-framework-core%2Ffun-framework-core-cache%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcache%2FCacheService.java)

## LockService

基于Redisson实现了[fun-framework-core](..%2Ffun-framework-core)
中的[LockService](..%2Ffun-framework-core%2Ffun-framework-core-cache%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcache%2FLockService.java)
，使用前需要先引入Redisson

# 快速开始

## 1. 添加依赖

* 核心依赖

```xml

<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-cache-redis</artifactId>
</dependency>
```

*
可选依赖，用于使用Redisson实现[LockService](..%2Ffun-framework-core%2Ffun-framework-core-cache%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcache%2FLockService.java)

```xml

<dependency>
  <groupId>org.redisson</groupId>
  <artifactId>redisson</artifactId>
</dependency>
```
