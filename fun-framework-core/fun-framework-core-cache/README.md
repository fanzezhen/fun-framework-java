缓存组件
------------------------------------------------------------------------------------------------------------------------

# 功能介绍

## 缓存相关Service

### CacheService

缓存接口，提供了常规的缓存方法，并基于内存做了简单实现[MemoryCacheServiceImpl](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcache%2Fimpl%2FMemoryCacheServiceImpl.java)

### LockService

常规锁相关接口，一般用来防止并发操作

- lockAndExecute方法接受需要执行的方法和锁的key，在执行方法前会先获取锁，执行完成后会释放锁，规定时间内获取不到锁则抛出异常

## 基于内存（HuTool）的“spring-boot-starter-cache”实现

[HuToolCacheManager.java](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcache%2FHuToolCacheManager.java)

# 快速开始

## 1. 添加依赖

核心依赖

```xml

<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-core-cache</artifactId>
</dependency>
```

可选依赖，用于使用Redisson实现[LockService](..%2Ffun-framework-core%2Ffun-framework-core-cache%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcache%2FLockService.java)

```xml

<dependency>
  <groupId>org.redisson</groupId>
  <artifactId>redisson</artifactId>
</dependency>
```
