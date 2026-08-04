fun-framework-proxy-method-handle
------------------------------------------

面向默认 MethodHandle 映射引擎的对象映射代理组件（等价 proxy-orika 之于 Orika 引擎）

# 功能介绍

1. 在使用默认 MethodHandle 引擎进行对象映射时，对带 `@ProxyField` 注解的 String 字段自动进行代理装饰（URL 代理、脱敏等）
2. 通过 `FieldValueHook` 扩展点接入引擎，无需 `--add-opens`

# 快速开始

## 一、添加依赖

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-proxy-method-handle</artifactId>
</dependency>
```

无需引入 orika-core：默认引擎即 MethodHandle（`fun.mapper.engine` 缺省或 `method-handle`）。

## 二、配置

启用代理，并提供 `ProxyDecorator`（由 proxy-core 按 `fun.proxy.enabled=true` 装配）：

```yaml
fun:
  proxy:
    enabled: true
    api: http://localhost:${server.port}/proxy/static/
    address-list:
      - origin: origin1.com
        target: target1
```

## 三、使用

在目标类字段上标注 `@ProxyField`，映射时自动装饰：

```java
public class UserDTO {
    private Long id;

    @ProxyField
    private String avatarUrl; // 映射写入前自动进行 URL 代理
}
```

```java
UserDTO dto = MapperFacadeUtil.map(entity, UserDTO.class);
// dto.avatarUrl 已被代理装饰
```

# 技术细节

- `ProxyFieldValueHook` 实现 `FieldValueHook`，被框架自动配置收集为 bean 注入 MethodHandle 引擎
- `supports` 按 (目标类, 字段名) 缓存 @ProxyField 扫描结果，避免高并发重复反射
- `apply` 调用 `ProxyHelper.decorateStr` 完成装饰
- 通过 `@ConditionalOnBean(ProxyHelper.class)` 自动启用

# 与 proxy-orika 的区别

| 维度 | proxy-orika | proxy-method-handle |
|------|-------------|---------------------|
| 适配引擎 | Orika（`fun.mapper.engine=orika`） | MethodHandle（默认） |
| 扩展机制 | Orika CustomFilter | `FieldValueHook` |
| `--add-opens` | 需要 | 不需要 |

# 参考资料

- [fun-framework-core-model README](../../fun-framework-core/fun-framework-core-model/README.md)
- [fun-framework-proxy-orika README](../fun-framework-proxy-orika/README.md)
