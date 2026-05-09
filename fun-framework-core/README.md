# fun-framework-core

Fun Framework 核心组件库

错误码格式 10***

## 模块说明

| 模块名 | 描述 | 错误码 |
|--------|------|--------|
| [fun-framework-core-all](fun-framework-core-all) | 核心组件聚合模块 | - |
| [fun-framework-core-cache](fun-framework-core-cache) | 缓存组件（缓存接口、锁服务、HuTool缓存实现） | 10*** |
| [fun-framework-core-context](fun-framework-core-context) | 上下文组件（请求头提取、ThreadLocal上下文管理） | 10*** |
| [fun-framework-core-log](fun-framework-core-log) | 日志组件（traceId生成、Web请求日志） | 10*** |
| [fun-framework-core-model](fun-framework-core-model) | 模型组件（BO/DTO/Entity基类、工具类、统一响应） | 10*** |
| [fun-framework-core-springboot](fun-framework-core-springboot) | Spring Boot自动配置（MapperFacade注入、线程池管理） | 10*** |
| [fun-framework-core-thread](fun-framework-core-thread) | 线程组件（线程池仓库、异步工具、上下文装饰器） | 10*** |
| [fun-framework-core-verify](fun-framework-core-verify) | 校验组件（数据校验工具） | 10*** |
| [fun-framework-core-web](fun-framework-core-web) | Web组件（Servlet工具类） | 10*** |

## 快速开始

### 添加依赖

引入所有核心组件：
```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-core-all</artifactId>
</dependency>
```

或按需引入单个组件：
```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-core-model</artifactId>
</dependency>
```

### Spring Boot 集成

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-core-springboot</artifactId>
</dependency>
```

自动配置包括：
- MapperFacade 对象映射
- Jackson JSON 序列化
- 全局异常处理
- 线程池管理

## 核心特性

### 1. 统一响应模型
```java
import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;

return ActionResult.success(data);
return ActionResult.fail("错误信息");
```

### 2. 上下文管理
```java
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;

// 获取当前用户ID
Long userId = ContextHolder.getUserId();

// 获取租户ID
Long tenantId = ContextHolder.getTenantId();

// 获取 traceId
String traceId = ContextHolder.getTraceId();
```

支持 `TransmittableThreadLocal`，自动在异步任务中传递上下文。

### 3. 线程池管理
```java
import com.github.fanzezhen.fun.framework.core.springboot.thread.ThreadPoolTaskExecutorRepository;

// 创建线程池（自动传递上下文）
ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
    "myThreadPool", 
    5,  // 核心线程数
    10  // 最大线程数
);

// 异步执行
executor.execute(() -> {
    // 子线程自动继承父线程的上下文
    Long userId = ContextHolder.getUserId();
});
```

### 4. 对象映射
```java
import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;

// 单个对象
UserDTO dto = MapperFacadeUtil.map(entity, UserDTO.class);

// 集合
List<UserDTO> list = MapperFacadeUtil.mapAsList(entityList, UserDTO.class);

// 分页
PageDTO<UserDTO> page = MapperFacadeUtil.page(entityPage, UserEntity.class, UserDTO.class);
```

### 5. 数据工具类
```java
import com.github.fanzezhen.fun.framework.core.model.util.*;

// 数字转中文
String chinese = NumberUtil.numToChinese("123"); // "一百二十三"

// 数据校验
ValidUtil.notEmpty(value, "参数不能为空");

// 字符串处理
String result = StringUtil.toCamelCase("user_name"); // "userName"
```

## 迁移指南

### v2.x 重构说明

1. **模块合并**：
   - `fun-framework-core-data` 已合并到 `fun-framework-core-model`
   - 工具类包路径从 `com.github.fanzezhen.fun.framework.core.data.util` 迁移到 `com.github.fanzezhen.fun.framework.core.model.util`

2. **线程池重构**：
   - `PoolExecutors` 已废弃，拆分为：
     - `ThreadPoolExecutorRepository`（位于 `fun-framework-core-thread`）
     - `ThreadPoolTaskExecutorRepository`（位于 `fun-framework-core-springboot`）

3. **上下文增强**：
   - `ContextHolder` 升级为 `TransmittableThreadLocal`，支持父子线程上下文传递
   - 需引入 `transmittable-thread-local` 依赖

### 迁移示例

```java
// 旧代码
import com.github.fanzezhen.fun.framework.core.data.util.NumberUtil;
import com.github.fanzezhen.fun.framework.core.thread.PoolExecutors;

ThreadPoolTaskExecutor executor = PoolExecutors.newThreadPoolTaskExecutor("name", 5, 10);

// 新代码
import com.github.fanzezhen.fun.framework.core.model.util.NumberUtil;
import com.github.fanzezhen.fun.framework.core.springboot.thread.ThreadPoolTaskExecutorRepository;

ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("name", 5, 10);
```

## 配置参数

### 上下文配置
```yaml
fun:
  core:
    context:
      key:
        prefix: fun-header-  # 请求头前缀
        tenant-id: TenantId  # 租户ID请求头名称
```

## 相关文档

- [模型组件文档](fun-framework-core-model/README.md)
- [上下文组件文档](fun-framework-core-context/README.md)
- [线程组件文档](fun-framework-core-thread/README.md)
- [Spring Boot集成文档](fun-framework-core-springboot/README.md)

## 常见问题

### 1. 异步任务中获取不到上下文？

确保：
- 引入了 `transmittable-thread-local` 依赖
- 使用 `ThreadPoolTaskExecutorRepository` 创建线程池
- `fun-framework-core-context` 已正确配置

### 2. MapperFacade 注入失败？

检查：
- 是否引入了 `orika-core` 依赖
- Spring Boot 应用启动时是否扫描到了自动配置类
- Java 9+ 需要添加 JVM 参数开放反射权限

### 3. 工具类找不到？

v2.x 版本工具类包路径已调整：
- 旧路径：`com.github.fanzezhen.fun.framework.core.data.util.*`
- 新路径：`com.github.fanzezhen.fun.framework.core.model.util.*`
