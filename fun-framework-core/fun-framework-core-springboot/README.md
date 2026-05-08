# fun-framework-core-springboot

Spring Boot 自动配置核心组件

## 功能

- **MapperFacade 自动注入** - 将 Orika MapperFacade 自动注入到 `MapperFacadeUtil`
- **零配置启动** - 添加依赖后自动生效

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-core-springboot</artifactId>
</dependency>

<!-- 对象映射需额外引入 Orika -->
<dependency>
    <groupId>ma.glasnost.orika</groupId>
    <artifactId>orika-core</artifactId>
</dependency>
```

### 自动配置原理

通过 `spring.factories` 自动加载 `FunCoreSpringbootAutoConfiguration`，检测并注入容器中的 `MapperFacade` Bean 到 `MapperFacadeUtil`

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

### 非 Spring 环境

`MapperFacadeUtil` 支持非 Spring 环境，使用内置默认 MapperFacade

## 配置说明

- **自动配置类**: `FunCoreSpringbootAutoConfiguration`
- **依赖**: `fun-framework-core-model` (必需), `orika-core` (可选), `spring-boot-starter` (必需)
- **测试**: `mvn test -pl fun-framework-core/fun-framework-core-springboot`

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

## 相关模块

- [fun-framework-core-model](../fun-framework-core-model/README.md)
- [fun-framework-proxy-orika](../../fun-framework-proxy/fun-framework-proxy-orika/README.md)
