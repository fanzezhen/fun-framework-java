线程组件
------------------------------------------------------------------------------------------------------------------------
错误码格式 10***

# 功能介绍

## 线程池仓库

### [ThreadPoolExecutorRepository](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fthread%2FThreadPoolExecutorRepository.java)
用于快捷创建和管理 `ThreadPoolExecutor`，提供线程池的注册、获取、销毁等功能。

**Spring Boot 集成**：需配合 [ThreadPoolTaskExecutorRepository](../../fun-framework-core-springboot/src/main/java/com/github/fanzezhen/fun/framework/core/springboot/thread/ThreadPoolTaskExecutorRepository.java) 使用，该类位于 `fun-framework-core-springboot` 模块。

## 异步工具

### [ExecutorHolder](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fthread%2FExecutorHolder.java)
用于异步并发执行任务，可以指定线程池，支持批量任务执行和结果收集。

## 线程池装饰器

### [ThreadPoolTaskDecorator](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Fthread%2Fdecorator%2FThreadPoolTaskDecorator.java)
自动传递上下文信息（如 traceId、用户信息等）到子线程。

# 快速开始

## 1. 添加依赖

核心依赖：
```xml
<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-core-thread</artifactId>
</dependency>
```

可选依赖（使用 TransmittableThreadLocal）：
```xml
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>transmittable-thread-local</artifactId>
</dependency>
```

## 2. 使用线程池仓库

```java
import com.github.fanzezhen.fun.framework.core.thread.ThreadPoolExecutorRepository;
import java.util.concurrent.ExecutorService;

// 创建线程池
ExecutorService executor = ThreadPoolExecutorRepository.computeThreadPoolExecutor(
    "myThreadPool", 
    5,  // 核心线程数
    10, // 最大线程数
    60L, // 空闲线程存活时间
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100) // 队列
);

// 获取已注册的线程池
ExecutorService registeredExecutor = ThreadPoolExecutorRepository.get("myThreadPool");

// 检查线程池是否存在
boolean exists = ThreadPoolExecutorRepository.contains("myThreadPool");

// 获取所有线程池名称
Set<String> poolNames = ThreadPoolExecutorRepository.getPoolNames();

// 销毁指定线程池（优雅关闭，等待 10 秒）
boolean destroyed = ThreadPoolExecutorRepository.destroy("myThreadPool", 10);

// 销毁所有线程池
ThreadPoolExecutorRepository.destroy(10);
```

## 3. 使用 ExecutorHolder

```java
import com.github.fanzezhen.fun.framework.core.thread.ExecutorHolder;

// 执行单个任务
ExecutorHolder.execute(() -> {
    // 异步任务逻辑
});

// 执行多个任务并等待完成
List<Callable<String>> tasks = Arrays.asList(
    () -> "Task 1",
    () -> "Task 2"
);
List<String> results = ExecutorHolder.invokeAll(tasks);
```

# Spring Boot 集成

在 Spring Boot 环境下，推荐使用 `fun-framework-core-springboot` 模块提供的 `ThreadPoolTaskExecutorRepository`：

```xml
<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-core-springboot</artifactId>
</dependency>
```

```java
import com.github.fanzezhen.fun.framework.core.springboot.thread.ThreadPoolTaskExecutorRepository;

// 创建 Spring 线程池（自动集成 TransmittableThreadLocal）
ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
    "mySpringThreadPool", 
    5,  // 核心线程数
    10  // 最大线程数
);
```

# 迁移说明

**v2.x 重构说明**：
- 原 `PoolExecutors` 类已废弃，功能拆分为：
  - `ThreadPoolExecutorRepository` - 管理 `ThreadPoolExecutor`（本模块）
  - `ThreadPoolTaskExecutorRepository` - 管理 Spring `ThreadPoolTaskExecutor`（位于 `fun-framework-core-springboot` 模块）
- 新增线程池注册机制，支持线程池的生命周期管理
- 自动集成 TransmittableThreadLocal 支持父子线程上下文传递

**迁移示例**：
```java
// 旧代码
ThreadPoolTaskExecutor executor = PoolExecutors.newThreadPoolTaskExecutor("name", 5, 10);

// 新代码
import com.github.fanzezhen.fun.framework.core.springboot.thread.ThreadPoolTaskExecutorRepository;
ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("name", 5, 10);
```
