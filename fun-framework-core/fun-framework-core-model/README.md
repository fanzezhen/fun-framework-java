模型组件
------------------------------------------   
提供了各种数据模型、工具类和静态变量

错误码格式 10***

# 功能介绍

## 基础模型

### BO (Business Object) 业务对象
- `BaseBO` - 基础业务对象（自增ID）
- `BaseGenericBO<ID>` - 泛型业务对象（支持自定义ID类型）
- `BaseTenantBO` - 租户业务对象（自增ID + 租户隔离）
- `BaseTenantGenericBO<ID>` - 泛型租户业务对象（自定义ID + 租户隔离）

### DTO (Data Transfer Object) 数据传输对象
- `BaseDTO` - 基础数据传输对象（自增ID）
- `BaseGenericDTO<ID>` - 泛型数据传输对象（支持自定义ID类型）
- `BaseTenantDTO` - 租户数据传输对象（自增ID + 租户隔离）
- `BaseTenantGenericDTO<ID>` - 泛型租户数据传输对象（自定义ID + 租户隔离）

### 统一响应对象
- `ActionResult<T>` - 统一 API 响应结果封装
- `ErrorInfo` - 错误信息封装
- `PageDTO<T>` - 分页数据传输对象

### 聚合查询条件
- `AggregationCondition` - 聚合查询条件基类
- `SumAggregationCondition` - 求和聚合条件
- `NestedAggregationCondition` - 嵌套聚合条件

## 工具类

本模块整合了原 `fun-framework-core-data` 模块的工具类：

### 数据处理工具
- `NumberUtil` - 数字处理工具（数字转中文、百分比转换等）
- `StringUtil` - 字符串处理工具
- `ValidUtil` - 数据校验工具
- `ObjUtil` - 对象处理工具

### 算法工具
- `SearchUtil` - 搜索算法工具
- `SortUtil` - 排序算法工具
- `MatrixUtil` - 矩阵运算工具

### 系统工具
- `ServerInfoUtil` - 服务器信息工具
- `StorageUtil` - 存储工具
- `StrFileUtil` - 文件字符串处理工具
- `YApiUtil` - YApi 文档工具

## 注解

- `@Entity` - 实体标注
- `@Column` - 列标注
- `@PrimaryKey` - 主键标注

## 常量与枚举

- `FunFrameworkCoreDataConstant` - 核心数据常量
- `FunCoreDataExceptionEnum` - 核心数据异常枚举

# 快速开始

## 1. 添加依赖

```xml
<dependency>
  <groupId>com.github.fanzezhen</groupId>
  <artifactId>fun-framework-core-model</artifactId>
</dependency>
```

## 2. 使用工具类

```java
import com.github.fanzezhen.fun.framework.core.model.util.*;

// 数字转中文
String chinese = NumberUtil.numToChinese("123"); // "一百二十三"

// 数据校验
ValidUtil.notEmpty(value, "参数不能为空");

// 对象映射
UserDTO dto = MapperFacadeUtil.map(entity, UserDTO.class);
```

## 3. 使用基础模型

```java
import com.github.fanzezhen.fun.framework.core.model.bo.*;

// 定义业务对象
public class UserBO extends BaseGenericBO<Long> {
    private String userName;
    private String email;
}

// 统一响应
return ActionResult.success(userBO);
```

# 迁移说明

**v2.x 重构说明**：
- 原 `fun-framework-core-data` 模块已合并到本模块
- 工具类包路径从 `com.github.fanzezhen.fun.framework.core.data.util` 迁移到 `com.github.fanzezhen.fun.framework.core.model.util`
- 所有功能保持不变，仅需更新 import 语句