fun-framework-proxy-orika
------------------------------------------
错误码格式 183**

基于 Orika 的对象映射代理组件

# 功能介绍

本组件提供以下功能：
1. 在使用 Orika 进行对象复制时实现字段代理功能（通过 `@ProxyField` 注解）
2. 提供 `MapperFacadeUtil` 工具类，封装 Orika MapperFacade 常用操作
3. 支持 `PageResult` 分页对象的便捷转换

# 快速开始

## 一、添加依赖

在 pom.xml 中添加以下依赖：

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-proxy-orika</artifactId>
</dependency>
```

## 二、配置

需要在 application.yml 中启用 orika 代理配置：

```yaml
fun:
  proxy:
    orika:
      enabled: true
```

# 使用指南

## 1. 使用 MapperFacadeUtil 进行对象转换

### 1.1 单个对象转换

```java
// 将 Entity 转换为 BO
UserEntity entity = userDao.selectById(1L);
UserBO bo = MapperFacadeUtil.map(entity, UserBO.class);
```

### 1.2 集合对象转换

```java
// 将 Entity 列表转换为 BO 列表
List<UserEntity> entityList = userDao.selectList(queryWrapper);
List<UserBO> boList = MapperFacadeUtil.mapAsList(entityList, UserBO.class);
```

### 1.3 分页对象转换

```java
// 方式一：使用 MapperFacadeUtil 工具类
PageResult<UserEntity> entityPage = userDao.selectPage(page, queryWrapper);
PageResult<UserBO> boPage = MapperFacadeUtil.page(entityPage, UserEntity.class, UserBO.class);

// 方式二：使用 PageResult 的 convert 方法
PageResult<UserBO> boPage = entityPage.convert(UserEntity.class, UserBO.class, mapperFacade);
```

### 1.4 空值处理

所有转换方法都内置了空值判断，源对象为 null 时返回 null：

```java
UserEntity entity = null;
UserBO bo = MapperFacadeUtil.map(entity, UserBO.class); // 返回 null，不会抛异常
```

## 2. 使用 @ProxyField 实现字段代理

当需要对特定字段进行代理处理时，可以使用 `@ProxyField` 注解：

```java
public class UserEntity {
    private Long id;
    private String username;
    
    @ProxyField // 标记需要代理的字段
    private String avatarUrl;
}

public class UserBO {
    private Long id;
    private String username;
    
    @ProxyField
    private String avatarUrl; // 对应字段也需要标记
}
```

配置自定义转换器：

```java
@Component("proxyOrikaConverter")
public class MyProxyOrikaConverter extends ProxyOrikaConverter {
    // 实现自定义的代理转换逻辑
}
```

## 3. 完整示例

### 3.1 Service 层使用示例

```java
@Service
public class UserServiceImpl implements IUserService {
    
    @Resource
    private UserMapper userMapper;
    
    @Override
    public UserBO getById(Long id) {
        UserEntity entity = userMapper.selectById(id);
        return MapperFacadeUtil.map(entity, UserBO.class);
    }
    
    @Override
    public List<UserBO> listByCondition(UserCondition condition) {
        List<UserEntity> entityList = userMapper.selectList(buildQueryWrapper(condition));
        return MapperFacadeUtil.mapAsList(entityList, UserBO.class);
    }
    
    @Override
    public PageResult<UserBO> pageByCondition(UserCondition condition) {
        Page<UserEntity> page = new Page<>(condition.getCurrentPage(), condition.getPageSize());
        PageResult<UserEntity> entityPage = userMapper.selectPage(page, buildQueryWrapper(condition));
        return MapperFacadeUtil.page(entityPage, UserEntity.class, UserBO.class);
    }
}
```

### 3.2 Controller 层使用示例

```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Resource
    private IUserService userService;
    
    @GetMapping("/{id}")
    public UserBO getById(@PathVariable Long id) {
        // Service 已返回 BO，Controller 直接返回
        // ResponseBodyWrapper 会自动封装为 Result<UserBO>
        return userService.getById(id);
    }
    
    @PostMapping("/page")
    public PageResult<UserBO> page(@RequestBody UserCondition condition) {
        return userService.pageByCondition(condition);
    }
}
```

# 技术细节

## MapperFacadeUtil 工作原理

1. **容器初始化时注入**: `MapperFacadeUtilConfig` 在 Spring 容器初始化完成后，自动将 `MapperFacade` 实例注入到 `MapperFacadeUtil` 静态字段中
2. **静态工具类设计**: 采用静态工具类设计，方便在任何地方直接调用，无需注入
3. **空值安全**: 所有方法都内置了空值判断，避免 NPE

## PageResult 转换机制

`PageResult.convert()` 方法通过反射调用 `MapperFacade.mapAsList()` 实现：
- 保持分页元数据不变（currentPage、pageSize、total、totalTime）
- 仅转换 rowList 中的数据类型
- 支持空列表的转换

## Java 17+ 模块化支持

本组件已配置 Surefire 插件添加必要的 JVM 参数，支持 Java 17+ 的模块化系统：

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <configuration>
    <argLine>
      --add-opens java.base/java.lang=ALL-UNNAMED
      --add-opens java.base/java.util=ALL-UNNAMED
    </argLine>
  </configuration>
</plugin>
```

# 注意事项

1. **容器初始化前不可用**: `MapperFacadeUtil` 依赖 Spring 容器初始化后注入 `MapperFacade`，在容器初始化前调用会抛出 NPE
2. **类型兼容性**: 源类型和目标类型的字段名称和类型需要兼容，否则可能出现转换失败
3. **性能考虑**: Orika 首次转换会生成字节码，有一定开销，但后续转换性能优异
4. **依赖版本**: 本组件依赖 `fun-framework-core-model` 和 `orika-core`，确保版本兼容

# 参考资料

- [Orika 官方文档](https://orika-mapper.github.io/orika-docs/)
- [fun-framework-core-model README](../../fun-framework-core/fun-framework-core-model/README.md)
- [MapperFacadeUtil 单元测试](src/test/java/com/github/fanzezhen/fun/framework/proxy/orika/util/MapperFacadeUtilTest.java)
