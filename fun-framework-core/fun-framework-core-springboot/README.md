fun-framework-core-springboot
------------------------------------------

Spring Boot 自动配置核心组件

# 功能介绍

本组件为 fun-framework-core 提供 Spring Boot 自动配置支持，主要功能包括：

1. **MapperFacade 自动注入** - 自动将 Orika MapperFacade 实例注入到 `MapperFacadeUtil` 工具类
2. **Spring Boot Starter 集成** - 提供开箱即用的 Spring Boot 自动配置
3. **零配置启动** - 依赖后自动生效，无需手动配置

# 快速开始

## 一、添加依赖

在 Spring Boot 项目的 pom.xml 中添加以下依赖：

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-core-springboot</artifactId>
</dependency>
```

**注意**: 如果需要使用 MapperFacadeUtil 进行对象映射，需要额外添加 Orika 依赖：

```xml
<dependency>
    <groupId>ma.glasnost.orika</groupId>
    <artifactId>orika-core</artifactId>
</dependency>
```

## 二、自动配置原理

本组件通过 Spring Boot 的 `spring.factories` 机制实现自动配置：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.github.fanzezhen.fun.framework.core.springboot.config.FunCoreSpringbootAutoConfiguration
```

Spring Boot 应用启动时会自动扫描并加载该配置类，完成以下初始化工作：

1. 检测容器中是否存在 `MapperFacade` Bean
2. 若存在，则自动注入到 `MapperFacadeUtil.setMapperFacade()`
3. 使 `MapperFacadeUtil` 在任何地方都可通过静态方法调用

# 使用指南

## 1. MapperFacadeUtil 对象映射

添加本依赖后，可以直接使用 `MapperFacadeUtil` 进行对象映射：

### 1.1 单个对象转换

```java
import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;

@Service
public class UserService {
    
    public UserBO getUserById(Long id) {
        UserEntity entity = userDao.selectById(id);
        // 自动使用容器中的 MapperFacade 进行转换
        return MapperFacadeUtil.map(entity, UserBO.class);
    }
}
```

### 1.2 集合对象转换

```java
public List<UserBO> listUsers() {
    List<UserEntity> entityList = userDao.selectList(queryWrapper);
    return MapperFacadeUtil.mapAsList(entityList, UserBO.class);
}
```

### 1.3 分页对象转换

```java
import com.github.fanzezhen.fun.framework.core.model.result.PageResult;

public PageResult<UserBO> pageUsers(Long pageNum, Long pageSize) {
    PageResult<UserEntity> entityPage = userDao.selectPage(pageNum, pageSize);
    return MapperFacadeUtil.page(entityPage, UserEntity.class, UserBO.class);
}
```

## 2. 自定义 MapperFacade 配置

如果需要自定义 MapperFacade 配置（如字段映射规则、转换器等），可以在项目中声明 Bean：

```java
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrikaConfig {
    
    @Bean
    public MapperFacade mapperFacade() {
        DefaultMapperFactory factory = new DefaultMapperFactory.Builder()
                .mapNulls(false) // 不映射 null 值
                .build();
        
        // 自定义字段映射规则
        factory.classMap(UserEntity.class, UserBO.class)
                .field("userName", "name") // 字段名不一致时的映射
                .byDefault()
                .register();
        
        return factory.getMapperFacade();
    }
}
```

自定义的 Bean 会自动替换默认的 MapperFacade，并注入到 `MapperFacadeUtil` 中。

## 3. 容器外使用（非 Spring 环境）

如果在非 Spring 环境中使用 `fun-framework-core-model` 模块的 `MapperFacadeUtil`，该工具类会自动使用内置的默认 MapperFacade：

```java
// 非 Spring 环境下也可以直接调用
UserBO bo = MapperFacadeUtil.map(entity, UserBO.class);
```

# 配置说明

## 自动配置类

- **FunCoreSpringbootAutoConfiguration** - 核心自动配置类
  - 作用：在容器初始化后（`@PostConstruct`）自动注入 MapperFacade
  - 注入条件：`@Autowired(required = false)` - MapperFacade 为可选依赖，不存在时不影响启动

## 依赖关系

```
fun-framework-core-springboot
├── fun-framework-core-model (必需)
├── orika-core (可选，使用 MapperFacadeUtil 时需要)
└── spring-boot-starter (必需)
```

# 测试

本组件包含完整的单元测试，覆盖以下场景：

- ✅ 单个对象映射（包含 null 值处理）
- ✅ 集合对象映射（包含空列表处理）
- ✅ 分页对象映射（包含元数据保持验证）
- ✅ 字段自动匹配（同名字段自动映射）

运行测试：

```bash
mvn test -pl fun-framework-core/fun-framework-core-springboot
```

# Java 9+ 模块系统兼容性

本组件使用 Orika 框架，在 Java 9+ 环境下需要开放 `java.base` 模块的反射访问权限。已在 `pom.xml` 中配置 Surefire 插件：

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <configuration>
    <argLine>
      -Dfile.encoding=UTF-8 
      --add-opens java.base/java.lang=ALL-UNNAMED 
      --add-opens java.base/java.util=ALL-UNNAMED 
      --add-opens java.base/java.lang.reflect=ALL-UNNAMED
    </argLine>
  </configuration>
</plugin>
```

生产环境部署时，需要在 JVM 启动参数中添加相同配置：

```bash
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     -jar your-application.jar
```

或通过 `JAVA_OPTS` 环境变量配置：

```bash
export JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED"
```

# 注意事项

1. **容器初始化时机**: MapperFacade 的注入发生在 `@PostConstruct` 阶段，确保在应用启动完成后才调用 `MapperFacadeUtil`
2. **可选依赖**: Orika 依赖是可选的（`optional=true`），如果项目不需要对象映射功能，无需引入
3. **线程安全**: `MapperFacadeUtil` 内部使用静态字段持有 MapperFacade 实例，线程安全由 Orika 保证
4. **性能考虑**: Orika 首次转换时会生成字节码，有一定初始化开销，但后续转换性能优异

# 相关模块

- [fun-framework-core-model](../fun-framework-core-model/README.md) - 提供基础模型类和 MapperFacadeUtil 工具类
- [fun-framework-proxy-orika](../../fun-framework-proxy/fun-framework-proxy-orika/README.md) - 提供 Orika 字段代理增强功能

# 版本要求

- Java: 21+
- Spring Boot: 4.0.5+
- Orika: 1.5.4+
