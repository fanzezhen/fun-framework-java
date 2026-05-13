# fun-framework-springboot-web

Spring Boot Web 应用功能集成模块

## 功能概览

本模块提供 Spring Boot Web 应用专用功能，包括：
- ✅ 全局异常处理
- ✅ JWT 认证拦截
- ✅ Web 日志打印
- ✅ MVC 扩展配置
- ✅ 响应体包装
- ✅ 参数校验注解
- ✅ Servlet 工具类

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>com.github.fanzezhen</groupId>
    <artifactId>fun-framework-springboot-web</artifactId>
</dependency>
```

**依赖说明**: 本模块会自动传递依赖以下模块：
- `fun-framework-core-springboot` - 基础配置
- `fun-framework-core-log` - 日志功能
- `fun-framework-core-verify` - 验证工具

### 自动配置原理

通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 自动加载 `FunCoreSpringbootWebAutoConfiguration`，自动注册以下组件：
- `DefaultExceptionHandler` - 全局异常处理器
- `FunLogPrintFilter` - 接口日志打印过滤器
- `FunJwtHandlerInterceptor` - JWT 认证拦截器（如果配置启用）
- `FunWebMvcRegistrations` - MVC 组件自定义注册

**兼容性说明**: 同时保留 `META-INF/spring.factories` 配置文件以兼容 Spring Boot 2.x

---

## 核心功能详解

### 1. 全局异常处理

`DefaultExceptionHandler` 提供统一的异常处理，自动将异常转换为 `ActionResult` 格式返回。

**支持的异常类型**:
- `ServiceException` - 业务异常
- `MethodArgumentNotValidException` - 参数校验异常（@Valid）
- `ConstraintViolationException` - 约束校验异常
- `ValidationException` - 通用校验异常
- `Exception` - 兜底处理

**使用示例**:
```java
@Service
public class UserService {
    public void createUser(UserDTO user) {
        if (user.getAge() < 0) {
            throw new ServiceException("年龄不能为负数");
        }
        // 业务逻辑...
    }
}
```

异常会自动转换为：
```json
{
  "code": 500,
  "message": "年龄不能为负数",
  "data": null
}
```

---

### 2. JWT 认证功能

提供完整的 JWT 认证拦截器和服务接口。

**核心类**:
- `FunJwtHandlerInterceptor` - JWT 请求拦截器
- `FunCoreVerifyTokenApi` - Token 验证 API 接口
- `JwtService` - JWT 服务接口
- `FunDefaultJwtServiceImpl` - 默认 JWT 实现

**使用示例**:

#### 步骤 1: 配置拦截器
```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private FunJwtHandlerInterceptor jwtInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login", "/api/register");
    }
}
```

#### 步骤 2: 实现 JwtService（可选）
```java
@Service
public class MyJwtService implements JwtService {
    @Override
    public String generateToken(Map<String, Object> claims) {
        // 自定义 Token 生成逻辑
    }
    
    @Override
    public boolean validateToken(String token) {
        // 自定义 Token 验证逻辑
    }
}
```

**配置属性**: 详见 `FunSpringbootWebProperties`

---

### 3. Web 日志打印

自动打印接口请求和响应日志，支持请求体和响应体内容记录。

**核心类**:
- `FunLogPrintFilter` - 接口日志打印过滤器
- `LoggingHttpServletRequestWrapper` - 可重复读取的请求包装器
- `LoggingHttpServletResponseWrapper` - 可重复读取的响应包装器
- `MultipartFilePrintSerializer` - 文件上传日志序列化

**配置属性**:
```properties
# 接口日志的打印级别，默认 DEBUG
fun.log.print-level=DEBUG
```

**日志格式示例**:
```
2026-05-12 10:30:00.123 [http-nio-8080-exec-1] [abc123] DEBUG com.example.controller - 
Request: POST /api/users
Headers: {Content-Type=application/json, Authorization=Bearer xxx}
Body: {"name":"张三","age":25}

Response: 200 OK
Body: {"code":200,"message":"success","data":{"id":1}}
Duration: 15ms
```

---

### 4. MVC 扩展

提供 MVC 相关的扩展功能。

**核心类**:
- `FunRequestMappingHandlerMapping` - 自定义路径映射处理
- `FunWebMvcRegistrations` - MVC 组件注册
- `ResponseBodyWrapHandler` - 响应体包装处理器
- `ResponseBodyWrapFactoryBean` - 响应包装工厂
- `ResponseBodyWrapper` - 响应体包装注解

**响应体自动包装**: 使用 `@ResponseBodyWrapper` 注解，自动将返回值包装为 `Result<T>` 格式

**使用示例**:
```java
@RestController
@RequestMapping("/api/users")
@ResponseBodyWrapper
public class UserController {
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.getById(id);
        // 自动包装为 Result.success(userDTO)
    }
}
```

---

### 5. 参数校验注解

提供额外的参数校验注解，增强 Bean Validation。

**可用注解**:
- `@BelongTo` - 值属于指定集合
- `@EnumsOf` - 枚举值校验
- `@ValueIn` - 值范围校验

**使用示例**:
```java
public class UserDTO {
    @ValueIn(values = {"male", "female"}, message = "性别只能是 male 或 female")
    private String gender;
    
    @EnumsOf(enumClass = UserStatus.class, message = "用户状态不合法")
    private String status;
    
    @BelongTo(collection = {"admin", "user", "guest"}, message = "角色不合法")
    private String role;
}
```

---

### 6. Servlet 工具类

`ServletUtil` 提供常用的 Servlet 操作工具方法。

**主要方法**:
- `getRequest()` - 获取当前请求对象
- `getResponse()` - 获取当前响应对象
- `getRequestBody()` - 获取请求体内容
- `getClientIp()` - 获取客户端 IP
- `downloadFile()` - 文件下载

**使用示例**:
```java
@RestController
public class FileController {
    @GetMapping("/download")
    public void download() throws IOException {
        byte[] content = fileService.getContent();
        ServletUtil.downloadFile(ServletUtil.getResponse(), 
                                 content, 
                                 "文件.pdf");
    }
}
```

---

## 配置属性

`FunSpringbootWebProperties` 提供可配置的属性：

```properties
# 示例配置（具体属性请查看源码）
fun.web.enabled=true
```

---

## 模块定位

本模块专注于提供 **Spring Boot Web 应用专用功能**，适用于：
- RESTful API 服务
- 传统 Web 应用
- 微服务 Web 节点

如果您的应用不需要 Web 功能（如纯后端服务、定时任务），请使用 [fun-framework-core-springboot](../../fun-framework-core/fun-framework-core-springboot/README.md)。

---

## Java 9+ 兼容性

本模块依赖 Orika 进行对象映射，在 Java 9+ 需要开放反射权限：

```bash
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     -jar app.jar
```

Maven Surefire 插件已配置测试时自动开放权限。

---

## 相关模块

- [fun-framework-core-springboot](../../fun-framework-core/fun-framework-core-springboot/README.md) - Spring Boot 基础配置
- [fun-framework-core-log](../../fun-framework-core/fun-framework-core-log/README.md) - 日志核心功能
- [fun-framework-core-verify](../../fun-framework-core/fun-framework-core-verify/README.md) - 验证工具
- [fun-framework-springboot-ai](../fun-framework-springboot-ai/README.md) - AI 功能扩展
- [后端架构设计文档](../../.agentdocs/backend/architecture.md) - 完整架构说明

---

## 贡献与反馈

如有问题或建议，请提交 Issue 至 GitHub/Gitee 仓库。
