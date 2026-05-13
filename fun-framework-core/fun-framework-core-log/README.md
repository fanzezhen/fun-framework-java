# fun-framework-core-log

日志核心组件

错误码格式: 10***

## 功能介绍

### 自动生成 TraceId

`FunLogTraceIdFilter` - 为每个请求自动生成唯一的 traceId，便于日志追踪

**位置**: [FunLogTraceIdFilter.java](src/main/java/com/github/fanzezhen/fun/framework/core/log/support/FunLogTraceIdFilter.java)

### 日志打印序列化

支持多种类型的日志打印序列化器：
- `BytesPrintSerializer` - 字节数组序列化
- `CharSequencePrintSerializer` - 字符序列序列化
- `DefaultPrintSerializer` - 默认序列化器
- `IoPrintSerializer` - IO 流序列化
- `PartPrintSerializer` - 分段序列化

### 日志辅助工具

- `FunLogHelper` - 日志辅助方法
- `LevelLogger` - 分级日志记录器

## 模块定位

本模块提供日志核心功能，**不依赖 Web 环境**，适用于所有类型的应用。

## Web 应用日志

如果需要 Web 应用的日志功能（如自动打印接口请求/响应），请使用 [fun-framework-springboot-web](../../../fun-framework-springboot/fun-framework-springboot-web/README.md)，其中包含：
- `FunLogPrintFilter` - 接口日志自动打印过滤器
- `LoggingHttpServletRequestWrapper` - 可重复读取的请求包装器
- `LoggingHttpServletResponseWrapper` - 可重复读取的响应包装器
- `MultipartFilePrintSerializer` - 文件上传日志序列化

# 配置参数
```properties
# 痕迹的key，默认traceId
fun.log.key.trace-id=traceId
# 接口日志的打印级别，默认DEBUG
fun.log.print-level=DEBUG
# trace级别日志单个文件大小，默认1MB
fun.log.level.trace.max-file-size=1MB
# trace级别日志最久保存天数，默认30
fun.log.level.trace.max-history=31
# trace级别日志总文件大小，默认1GB
fun.log.level.trace.total-size-cap=100MB
# debug级别日志单个文件大小，默认1MB
fun.log.level.debug.max-file-size=1MB
# debug级别日志最久保存天数，默认30
fun.log.level.debug.max-history=7
# debug级别日志总文件大小，默认1GB
fun.log.level.debug.total-size-cap=10MB
# info级别日志单个文件大小，默认1MB
fun.log.level.info.max-file-size=1MB
# info级别日志最久保存天数，默认30
fun.log.level.info.max-history=31
# info级别日志总文件大小，默认1GB
fun.log.level.info.total-size-cap=1GB
# warn级别日志单个文件大小，默认1MB
fun.log.level.warn.max-file-size=1MB
# warn级别日志最久保存天数，默认30
fun.log.level.warn.max-history=185
# warn级别日志总文件大小，默认1TB
fun.log.level.warn.total-size-cap=1GB
# error级别日志单个文件大小，默认1MB
fun.log.level.error.max-file-size=1MB
# error级别日志最久保存天数，默认30
fun.log.level.error.max-history=366
# error级别日志总文件大小，默认1TB
fun.log.level.error.total-size-cap=1GB
# 日志文件路径
logging.file.path=fun-demo-framework/fun-demo-framework-log/target/logs
# 配置控制台日志显示格式
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{traceId}] %-5level %logger{36} - %msg%n
# 配置文件中日志显示格式
logging.pattern.file=${logging.pattern.console}

```