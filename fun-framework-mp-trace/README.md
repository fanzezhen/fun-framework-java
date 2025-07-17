fun-framework-mp-trace
------------------------------------------

-
基于Mybatis-plus拦截器实现了痕迹收集功能，启动前需要实现[IFunTraceService](..%2Ffun-framework-mp-trace%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Ftrace%2Fservice%2FIFunTraceService.java)
- 不属于核心模块，需要单独引入依赖，引入依赖自动生效

# 快速开始

## 1. 添加依赖

```xml

<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-mp-trace</artifactId>
</dependency>
```

## 2. 添加配置

```yaml
fun:
  trace:
    rules:
      # 需要记录痕迹的表
      # 示例：字典表
      sys_dict:
        # 痕迹主体的名字（表名）
        name: 字典
        # 痕迹主体的展示名字段（大写）
        name-key: NAME
        # 痕迹明细需要记录的字段，会在明细中记录旧值和新值
        detail:
          # 字段名（大写）
          NAME: 字典名
      # 示例：字典项表
      sys_dict_item:
        # 痕迹主体的名字（表名）
        name: 字典项
        # 痕迹主体的展示名字段（大写）
        name-key: NAME
        # 痕迹明细需要记录的字段，会在明细中记录旧值和新值
        detail:
          # 字段名（大写）
          NAME: 字典项名
```

## 3. 实现[IFunTraceService](..%2Ffun-framework-mp-trace%2Fsrc%2Fmain%2Fjava%2Fcom%2Fgithub%2Ffanzezhen%2Ffun%2Fframework%2Ftrace%2Fservice%2FIFunTraceService.java)


