上下文组件
------------------------------------------------------------------------------------------------------------------------
错误码格式 10***

# 功能介绍

## 自动提取WEB请求头数据放入上下文

### [FunContextFilter](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcontext%2FFunContextFilter.java)

web过滤器，用于自动提取WEB请求头数据放入[ContextHolder](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcontext%2FContextHolder.java)

### [ContextHolder.java](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcontext%2FContextHolder.java)

使用 `TransmittableThreadLocal` 存储上下文数据，支持父子线程间的上下文传递。内置了一些方法如：getUserId、getToken 等。

**特性**：
- 基于阿里巴巴 `TransmittableThreadLocal` 实现，支持线程池场景下的上下文传递
- 自动在异步任务中保留父线程的上下文信息（如用户ID、租户ID、traceId等）
- 与 `fun-framework-core-thread` 的线程池装饰器无缝集成

## 注解+AOP 实现上下文校验

- [FunContextAop.java](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcontext%2Faop%2FFunContextAop.java)
- [ContextHeader.java](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fcontext%2Faop%2FContextHeader.java)
  - required: 必须存在的请求头，为空时会抛出异常
  - hidden: 需隐藏的请求头，在此注解的方法下会置空，方法结束后会恢复

# 快速开始

## 1. 添加依赖

核心依赖

```xml
<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-core-context</artifactId>
</dependency>

<!-- TransmittableThreadLocal 支持（自动传递上下文到子线程） -->
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>transmittable-thread-local</artifactId>
</dependency>
```

可选配置参数

```yaml
fun:
  core:
    # 上下文参数，用于配置上下文参数
    context:
      # 上下文key
      key:
        # 请求头和上下文key的前缀
        prefix: fun-header-
        ## 特殊请求头和上下文key的命名
        # 租户id请求头和上下文key的命名
        tenant-id: TenantId

```
