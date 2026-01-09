fun-framework-api-count-redis
------------------------------------------------------------------------------------------------------------------------
错误码前缀 1300

- 基于Redis实现的接口字段统计功能，用于统计接口各字段的空置率
- 不属于核心模块，需要单独引入依赖，引入依赖自动生效

# 快速开始

## 1. 添加依赖

```xml

<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-api-count-redis</artifactId>
</dependency>
```

## 2. 自定义返回值的包装类

参考[FunApiCountResultResolveImpl.java](src%2Ftest%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Fapi%2Fcount%2FFunApiCountResultResolveImpl.java)

## 3. 导出统计报告

1. 从YApi导出json数据
2. 调用“/export/excel-by-y-api”接口，参数为YApi的json数据
3. 导出的excel文件为YApi中每个接口的访问次数和返回值各字段的空值率

# 原理

使用AOP切面将Controller的接口请求进行统计，并记录接口返回值中各字段的空值率