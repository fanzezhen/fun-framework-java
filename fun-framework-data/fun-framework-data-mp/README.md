fun-framework-data-mp
------------------------------------------
错误码格式 121**

MyBatis-Plus 集成模块，提供基础实体、代码生成器和痕迹追踪功能

# 子模块说明

| 模块名 | 描述 | 错误码格式 |
|--------|------|-----------|
| [fun-framework-data-mp-starter](fun-framework-data-mp-starter) | MyBatis-Plus 启动器，提供基础实体类、多租户隔离和配置 | 1210* |
| [fun-framework-data-mp-trace](fun-framework-data-mp-trace) | MyBatis-Plus 痕迹追踪组件 | 1211* |
| [fun-framework-data-mp-trace-impl](fun-framework-data-mp-trace-impl) | 痕迹追踪默认实现 | 1212* |

# 已用错误码

| 错误码 | 枚举项 | 说明 |
|-------|-------|------|
| 12100 | `FunDataMpExceptionEnum.TENANT_CONTEXT_MISSING` | 租户上下文缺失且策略为 `reject` |
| 12101 | `FunDataMpExceptionEnum.TENANT_ID_NOT_NUMERIC` | `value-type=long` 但租户号无法解析为数值 |
