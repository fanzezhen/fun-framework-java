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

### 安全工具
- `IdentifierUtil` - 数据标识符白名单校验与引用包裹

表名、列名、图标签、关系类型这类标识符无法通过参数绑定，只能拼接进语句文本，是注入的主要入口。
拼接前用本工具校验，不合法直接抛业务异常而非转义：

    // 校验并用反引号包裹，不合法抛 ServiceException
    String quoted = IdentifierUtil.quote(tableName, IdentifierUtil.CATEGORY_TABLE);

    // 仅校验
    IdentifierUtil.requireLegal(columnName, IdentifierUtil.CATEGORY_COLUMN);

## 数据访问抽象

- `ITemplate<P>` - 数据访问模板接口，定义 `get` / `getById` / `listByIds` / `listByColumn` / `insert` / `deleteById` 通用语义
- `IDatasourceConfig` - 数据源配置契约，只要求配置对象能给出名称
- `BaseMultiDatasourceTemplate<T, C>` - 多数据源模板抽象基类

`BaseMultiDatasourceTemplate` 承载与具体存储无关的多数据源能力：按名称索引配置、
按实体上的 `@Entity(datasource)` 路由、默认数据源回退、子模板创建与缓存。
Elasticsearch、图数据库等多数据源模板可共用，无需各自重复实现路由逻辑。

子模板默认惰性创建（首次使用才创建），需要启动即创建的实现可在自身构造器末尾调用
`initAllTemplates()`。之所以不在基类构造期创建：子类的 `createTemplate` 通常要用到子类字段，
而 Java 的初始化顺序是父类构造器先于子类字段赋值，构造期回调子类方法会读到未初始化的字段。

## 注解

- `@Entity` - 实体标注（含 `datasource` 用于多数据源路由）
- `@Column` - 列标注（`name` 列名、`isPrimaryKey` 主键、`writable` 是否参与写入、`deserializeResolver` 自定义反序列化）
- `@PrimaryKey` - 主键标注

`@Column(writable = false)` 表示该列只读：查询结果会映射到该字段，但写入语句不带该列，
适用于由存储侧计算得出的派生列。

## 常量与枚举

- `FunFrameworkCoreDataConstant` - 核心数据常量
- `RegexConstant` - 正则表达式常量
- `FunCoreDataExceptionEnum` - 核心数据异常枚举（多数据源模板、标识符校验、主键缺失、结果解析）

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