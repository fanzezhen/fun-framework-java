fun-framework-data-graph-neo4j-starter
------------------------------------------
错误码格式 123**

Neo4j 图数据库 Starter，基于 neo4j-java-driver 实现图操作模板

# 功能介绍

## 核心组件

### config（配置）
驱动工厂与自动装配。驱动工厂只对显式配置项做设置，未配置项保持驱动默认值

### convert（转换）
Neo4j 驱动与框架抽象层之间的唯一转换点，把驱动类型归一化为中间表示

### template（模板）
单数据源模板与多数据源模板。多数据源模板惰性创建，未使用的数据源不建连接池

# 快速开始

## 一、添加依赖

    <dependency>
        <groupId>com.github.fanzezhen</groupId>
        <artifactId>fun-framework-data-graph-neo4j-starter</artifactId>
    </dependency>

抽象层随之带入，无需另行声明。版本由框架父 POM 统一管理。

## 二、配置

    fun:
      data:
        graph:
          default-datasource: default
          configs:
            - name: default
              uri: bolt://localhost:7687
              username: neo4j
              password: password
              database: neo4j

配置项清单见 [fun-framework-data-graph-base](../fun-framework-data-graph-base/README.md)。

## 三、使用

    @Resource
    private BaseMultiDatasourceGraphTemplate graphTemplate;

    List<PersonNode> personList = graphTemplate.queryList(
        "MATCH (n:Person) WHERE n.city = $city RETURN n",
        Map.of("city", "上海"),
        PersonNode.class);

实体定义与完整用法见抽象层 README。

# 类型归一化

驱动返回的类型全部归一化为中间表示或 JDK 类型，业务代码不接触任何 Neo4j 类型：

| 驱动类型 | 归一化结果 |
|---------|-----------|
| `Node` | `GraphNodeData` |
| `Relationship` | `GraphRelationshipData` |
| `Path` | `Map`，含 `nodes`、`relationships` 两个键 |
| `Point` | `Map`，含 `srid`、`x`、`y`、`z` |
| `IsoDuration` | `Map`，含 `months`、`days`、`seconds`、`nanoseconds` |
| 数值/字符串/布尔/时间 | 原样保留（本就是 JDK 类型） |
| 列表/映射 | 逐元素递归归一化 |

键名常量见抽象层的 `FunGraphConstant`。

驱动的关系对象只携带两端节点标识而不含节点数据，转换时会在同一行结果内按标识回填，
因此 `MATCH (a)-[r]->(b) RETURN a, r, b` 的关系可直接取到起止节点；
只返回关系（`RETURN r`）时两端节点为空，标识字段仍可用。

# 节点标识说明

以驱动的 `elementId()`（字符串）作为节点与关系的标识。驱动 6.x 起 `id()`（long）已弃用，
框架不使用该方法。`ITemplate` 的主键类型相应为 `String`。

# 原生结果访问

框架能力不足时可直接访问驱动原生结果，处理器入参为 `org.neo4j.driver.Result`：

    Long count = graphTemplate.queryNative(cypher, params,
        result -> ((Result) result).single().get(0).asLong());

此方式会让调用方绑定到 Neo4j，优先考虑 `queryRecordList`。

# 资源释放

多数据源模板实现 `DisposableBean`，容器销毁时关闭已创建的驱动，释放连接池与后台线程。
自行构造模板（不经 Spring 容器）时需自行调用 `destroy()`。

# 测试说明

两层测试：

- 单元测试：结果转换与驱动配置翻译，Mockito 桩化驱动接口，毫秒级
- 集成测试：`neo4j-harness` 启动进程内 Neo4j（走真实 Bolt，无需 Docker），覆盖节点/关系的增查改删、
  投影查询、集合属性往返、`DETACH DELETE` 连带清理、原生结果访问，验证生成的 Cypher
  能被真实图库执行且驱动返回值能正确归一化

`neo4j-harness` 为 `test` scope。它引入的 Neo4j 内核是 **GPL-3.0-or-later**，
不随本模块产物分发，使用方无需承担该许可。

集成测试整类共用一个进程内实例，每个用例前清库，整类耗时约 30 秒（含内核启动）。
