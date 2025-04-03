日志组件
------------------------------------------------------------------------------------------------------------------------

# 功能介绍

## 自动生成traceId

[FunLogTraceIdFilter](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Flog%2Fsupport%2Fweb%2FFunLogTraceIdFilter.java)

## 自动打印接口日志

[FunLogPrintFilter](src%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fcore%2Flog%2Fsupport%2Fweb%2FFunLogPrintFilter.java)

# 配置参数
```properties
# 痕迹的key，默认traceId
fun.core.log.key.trace-id=traceId
# 接口日志的打印级别，默认DEBUG
fun.core.log.print-level=DEBUG
# trace级别日志单个文件大小，默认1MB
fun.core.log.level.trace.max-file-size=1MB
# trace级别日志最久保存天数，默认30
fun.core.log.level.trace.max-history=30
# trace级别日志总文件大小，默认1GB
fun.core.log.level.trace.total-size-cap=1GB
# debug级别日志单个文件大小，默认1MB
fun.core.log.level.debug.max-file-size=1MB
# debug级别日志最久保存天数，默认30
fun.core.log.level.debug.max-history=30
# debug级别日志总文件大小，默认1GB
fun.core.log.level.debug.total-size-cap=1GB
# info级别日志单个文件大小，默认1MB
fun.core.log.level.info.max-file-size=1MB
# info级别日志最久保存天数，默认30
fun.core.log.level.info.max-history=30
# info级别日志总文件大小，默认1GB
fun.core.log.level.info.total-size-cap=1GB
# warn级别日志单个文件大小，默认1MB
fun.core.log.level.warn.max-file-size=1MB
# warn级别日志最久保存天数，默认30
fun.core.log.level.warn.max-history=30
# warn级别日志总文件大小，默认1TB
fun.core.log.level.warn.total-size-cap=1TB
# error级别日志单个文件大小，默认1MB
fun.core.log.level.error.max-file-size=1MB
# error级别日志最久保存天数，默认30
fun.core.log.level.error.max-history=30
# error级别日志总文件大小，默认1TB
fun.core.log.level.error.total-size-cap=1TB
# 日志文件路径
logging.file.path=fun-demo-framework/fun-demo-framework-log/target/logs
# 配置控制台日志显示格式
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{traceId}] %-5level %logger{36} - %msg%n
# 配置文件中日志显示格式
logging.pattern.file=${logging.pattern.console}

```