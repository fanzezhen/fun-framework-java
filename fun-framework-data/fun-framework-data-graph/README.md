fun-framework-data-graph
------------------------------------------
错误码格式 123**

图数据库集成模块，提供统一的图数据库操作抽象与具体实现

# 子模块说明

| 模块名 | 描述 | 错误码格式 |
|--------|------|-----------|
| [fun-framework-data-graph-base](fun-framework-data-graph-base) | 图操作抽象层：注解、映射引擎、模板抽象，不依赖任何图数据库驱动 | 123** |
| [fun-framework-data-graph-neo4j-starter](fun-framework-data-graph-neo4j-starter) | Neo4j 实现，基于 neo4j-java-driver | 123** |

# 设计要点

抽象层与驱动通过中间表示解耦：starter 负责把驱动返回值归一化为
`GraphRecordData` / `GraphNodeData` / `GraphRelationshipData`，映射引擎与模板层不出现任何驱动类型。

因此新增图数据库实现只需两步：

1. 实现 `BaseGraphTemplate` 的三个驱动原语（`doQuery`、`doExecute`、`queryNative`）
2. 把驱动结果归一化为中间表示

注解体系、Cypher 生成、结果映射、CRUD 语义、多数据源路由全部复用，无需重复实现。
使用私有驱动的图数据库（无法进入公开仓库的商业图库）可在业务侧按同一方式接入。

# 快速开始

引入 Neo4j 实现（自动带入抽象层）：

    <dependency>
        <groupId>com.github.fanzezhen</groupId>
        <artifactId>fun-framework-data-graph-neo4j-starter</artifactId>
    </dependency>

版本由框架父 POM 统一管理，无需声明。

配置数据源：

    fun:
      data:
        graph:
          default-datasource: default
          configs:
            - name: default
              uri: bolt://localhost:7687
              username: neo4j
              password: password

注入模板即可使用：

    @Resource
    private BaseMultiDatasourceGraphTemplate graphTemplate;

    List<PersonNode> personList = graphTemplate.queryList(
        "MATCH (n:Person) WHERE n.city = $city RETURN n",
        Map.of("city", "上海"),
        PersonNode.class);

完整用法见 [fun-framework-data-graph-base](fun-framework-data-graph-base) 与
[fun-framework-data-graph-neo4j-starter](fun-framework-data-graph-neo4j-starter) 的 README。
