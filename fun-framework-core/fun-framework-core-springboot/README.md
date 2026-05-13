# fun-framework-core-springboot

Spring Boot 基础自动配置组件

## 功能

- **MapperFacade 自动注入** - 将 Orika MapperFacade 自动注入到 `MapperFacadeUtil`
- **线程池管理** - 提供 `ThreadPoolTaskExecutorRepository` 用于管理 Spring 线程池，自动集成上下文传递
- **Jackson 配置** - 提供 `FunJacksonConfig` 统一 JSON 序列化配置
- **零配置启动** - 添加依赖后自动生效

## 模块定位

本模块提供 Spring Boot 环境下的**基础配置和通用工具**，不依赖 Web 环境，适用于：
- 纯后端服务（定时任务、消息消费者）
- 需要 Spring Boot 基础功能但无需 Web 能力的应用

如果需要 Web 应用功能（全局异常处理、JWT 认证、Web 日志），请使用 [fun-framework-springboot-web](../../../fun-framework-springboot/fun-framework-springboot-web/README.md)。

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-core-springboot</artifactId>
</dependency>

<!-- 对象映射需额外引入 Orika（可选） -->
<dependency>
    <groupId>ma.glasnost.orika</groupId>
    <artifactId>orika-core</artifactId>
</dependency>

<!-- 线程池上下文传递（可选） -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>transmittable-thread-local</artifactId>
</dependency>
```

### 自动配置原理

通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 自动加载：
- `FunCoreSpringbootStarterAutoConfiguration` - 检测并注入容器中的 `MapperFacade` Bean 到 `MapperFacadeUtil`，配置 `FunJacksonConfig` 用于 JSON 序列化
- `FunCoreThreadAutoConfiguration` - 自动配置线程池管理

**兼容性说明**: 同时保留 `META-INF/spring.factories` 配置文件以兼容 Spring Boot 2.x

## 使用示例

### MapperFacadeUtil 对象映射

```java
import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;

// 单个对象
UserDTO dto = MapperFacadeUtil.map(entity, UserDTO.class);

// 集合
List<UserDTO> list = MapperFacadeUtil.mapAsList(entityList, UserDTO.class);

// 分页
PageDTO<UserDTO> page = MapperFacadeUtil.page(entityPage, UserEntity.class, UserDTO.class);
```

### 自定义 MapperFacade 配置

```java
@Configuration
public class OrikaConfig {
    @Bean
    public MapperFacade mapperFacade() {
        DefaultMapperFactory factory = new DefaultMapperFactory.Builder()
                .mapNulls(false)
                .build();
        
        factory.classMap(UserEntity.class, UserDTO.class)
                .field("userName", "name")
                .byDefault()
                .register();
        
        return factory.getMapperFacade();
    }
}
```

### ThreadPoolTaskExecutorRepository 线程池管理

```java
import thread.com.github.fanzezhen.fun.framework.core.springboot.ThreadPoolTaskExecutorRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// 创建线程池（自动集成上下文传递）
ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
        "myThreadPool",
        5,  // 核心线程数
        10  // 最大线程数
);

        // 创建带队列容量的线程池
        ThreadPoolTaskExecutor executorWithQueue = ThreadPoolTaskExecutorRepository.computeThreadPoolTaskExecutor(
                "myThreadPool",
                5,   // 核心线程数
                10,  // 最大线程数
                100  // 队列容量
        );

        // 获取已注册的线程池
        ThreadPoolTaskExecutor registeredExecutor = ThreadPoolTaskExecutorRepository.get("myThreadPool");

        // 检查线程池是否存在
        boolean exists = ThreadPoolTaskExecutorRepository.contains("myThreadPool");

        // 获取所有线程池名称
        Set<String> poolNames = ThreadPoolTaskExecutorRepository.getPoolNames();

        // 销毁指定线程池（优雅关闭，等待 10 秒）
        boolean destroyed = ThreadPoolTaskExecutorRepository.destroy("myThreadPool", 10);

// 销毁所有线程池
ThreadPoolTaskExecutorRepository.

        destroy(10);
```

**线程池生命周期管理**：

`destroy` 方法实现了优雅关闭策略：
1. **停止接收新任务** - 调用 `shutdown()` 方法
2. **等待任务完成** - 等待指定时间（如 10 秒）
3. **强制关闭** - 超时后调用 `shutdownNow()` 中断正在执行的任务
4. **资源释放** - 调用 Spring 的 `destroy()` 释放线程池资源

### 非 Spring 环境

`MapperFacadeUtil` 支持非 Spring 环境，使用内置默认 MapperFacade

## 配置说明

- **自动配置类**: 
  - `FunCoreSpringbootStarterAutoConfiguration` - 基础配置和工具
  - `FunCoreThreadAutoConfiguration` - 线程池管理
- **依赖**: `fun-framework-core-model` (必需), `fun-framework-core-thread` (必需), `orika-core` (可选)
- **测试**: `mvn test -pl fun-framework-core/fun-framework-core-springboot`

## 扩展模块

如果需要更多功能，请参考：
- [fun-framework-springboot-web](../../../fun-framework-springboot/fun-framework-springboot-web/README.md) - Web 应用功能（全局异常处理、JWT 认证、Web 日志、MVC 扩展）
- [fun-framework-springboot-ai](../../../fun-framework-springboot/fun-framework-springboot-ai/README.md) - AI 功能扩展（Spring AI 集成）

## Java 9+ 兼容性

Orika 在 Java 9+ 需要开放反射权限，生产环境需添加 JVM 参数：

```bash
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     -jar app.jar
```

## 注意事项

- MapperFacade 注入在 `@PostConstruct` 阶段，应用启动完成后才可用
- Orika 依赖可选 (`optional=true`)，不使用对象映射可不引入
- 线程安全由 Orika 保证
- 使用 `ThreadPoolTaskExecutorRepository` 创建的线程池会自动配置 `ThreadPoolTaskDecorator`，支持上下文传递
- 建议配合 `fun-framework-core-context` 使用，实现完整的上下文管理

## 相关模块

- [fun-framework-core-model](../fun-framework-core-model/README.md) - 核心数据模型
- [fun-framework-core-thread](../fun-framework-core-thread/README.md) - 线程管理
- [fun-framework-core-context](../fun-framework-core-context/README.md) - 上下文管理
- [fun-framework-springboot-web](../../../fun-framework-springboot/fun-framework-springboot-web/README.md) - Web 应用功能
- [fun-framework-springboot-ai](../../../fun-framework-springboot/fun-framework-springboot-ai/README.md) - AI 功能扩展
- [fun-framework-proxy-orika](../../fun-framework-proxy/fun-framework-proxy-orika/README.md) - Orika 代理
