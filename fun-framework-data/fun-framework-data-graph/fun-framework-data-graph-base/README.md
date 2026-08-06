fun-framework-data-graph-base
------------------------------------------
错误码格式 123**

图数据库基础组件，提供注解、映射引擎、模板抽象与 Cypher 构建，不依赖任何图数据库驱动

# 功能介绍

## 核心组件

### annotation（注解）
图实体与图元素的映射注解，驱动无关，各图数据库实现复用同一套

### model（模型）
中间表示（`GraphRecordData` / `GraphNodeData` / `GraphRelationshipData`）与实体基类
（`BaseNode` / `BaseRelationship`）

### mapping（映射）
可插拔映射引擎（`IGraphMapper`）、默认反射实现、实体元数据与标签反查缓存

### template（模板）
统一操作接口（`IGraphTemplate`）、抽象基类、多数据源模板、Cypher 构建器

### config（配置）
配置属性绑定与自动装配

### constant（常量）
中间表示的约定键名，供各实现归一化时复用

### enums（枚举）
图模块异常码

# 注解说明

图特有的注解：

| 注解 | 作用位置 | 说明 |
|------|---------|------|
| `@GraphNode` | 类 | 标记节点实体，`label` 为空时取类简名 |
| `@GraphRelationship` | 类 | 标记关系实体，`type` 为空时取类简名 |
| `@GraphId` | 字段 | 图库内部标识（Neo4j 的 elementId），由图库生成，写入后回填 |
| `@GraphLabels` | 字段 | 承载节点全部标签，类型为 `Collection<String>` |
| `@GraphType` | 字段 | 承载关系实际类型，类型为 `String` |
| `@StartNode` / `@EndNode` | 字段 | 关系的起止节点 |

属性名映射与业务主键复用框架统一的 `@Column`（来自 fun-framework-core-model），不另立图专属注解：

| 注解用法 | 说明 |
|---------|------|
| `@Column(name = "xxx")` | 属性名映射；未标注时按字段名映射 |
| `@Column(isPrimaryKey = true)` | 业务主键，落为节点的普通属性，`merge` 按其合并 |
| `@Column(writable = false)` | 只读属性：查询会映射，写入不带该属性 |

# 快速开始

## 一、添加依赖

一般无需直接引入，选用具体图数据库 starter 时会自动带入：

    <dependency>
        <groupId>com.github.fanzezhen</groupId>
        <artifactId>fun-framework-data-graph-base</artifactId>
    </dependency>

版本由框架父 POM 统一管理。

## 二、定义实体

    @Data
    @GraphNode(label = "Person")
    public class PersonNode {

        /** 图库内部标识，由图库生成 */
        @GraphId
        private String elementId;

        /** 业务主键，落为普通属性 */
        @Column(name = "person_id", isPrimaryKey = true)
        private String personId;

        private String name;

        /** 结果为字符串时也会自动转为 Integer */
        private Integer age;

        @Column(name = "alias_list")
        private List<String> aliasList;

        @GraphLabels
        private Set<String> labelSet;

        /** 图库侧计算的派生属性，不参与写入 */
        @Column(name = "degree", writable = false)
        private Integer degree;
    }

关系实体：

    @Data
    @GraphRelationship(type = "KNOWS")
    public class KnowsRelationship {

        @GraphId
        private String elementId;

        @GraphType
        private String type;

        @StartNode
        private PersonNode startNode;

        @EndNode
        private PersonNode endNode;

        @Column(name = "since_year")
        private Integer sinceYear;
    }

## 三、查询

    // 返回节点，目标类型为图实体时直接映射节点列
    List<PersonNode> personList = graphTemplate.queryList(
        "MATCH (n:Person) WHERE n.city = $city RETURN n",
        Map.of("city", "上海"),
        PersonNode.class);

    // 投影查询，按结果列名映射到同名字段
    List<PersonSummary> summaryList = graphTemplate.queryList(
        "MATCH (n:Person) RETURN n.name AS name, count(*) AS total",
        PersonSummary.class);

    // 单值查询，取首行首列
    Long total = graphTemplate.queryObject("MATCH (n:Person) RETURN count(n)", Long.class);

    // 结果结构不固定时取中间表示，无需为一次性投影定义类型
    List<GraphRecordData> recordList = graphTemplate.queryRecordList(cypher, params);

关系两端节点在同一行返回时会自动关联：

    // r.getStartNode() / r.getEndNode() 已填充
    KnowsRelationship relationship = graphTemplate.queryOne(
        "MATCH (a:Person)-[r:KNOWS]->(b:Person) RETURN a, r, b",
        KnowsRelationship.class);

## 四、写入

    // 新增，写入后图库标识回填到实体
    PersonNode person = new PersonNode();
    person.setPersonId("P001");
    person.setName("小明");
    graphTemplate.insert(List.of(person));
    String elementId = person.getElementId();

    // 按业务主键合并，可重复执行不产生重复节点
    graphTemplate.merge(List.of(person));

    // 删除节点，连带清理其关系
    graphTemplate.deleteById(List.of(elementId), PersonNode.class);

写入关系要求两端节点已持有图库标识（先写节点再写关系）：

    KnowsRelationship relationship = new KnowsRelationship();
    relationship.setStartNode(personA);   // personA.elementId 非空
    relationship.setEndNode(personB);
    boolean allWritten = graphTemplate.insert(List.of(relationship));

两端标识为空的关系无法定位节点而被跳过，此时 `insert` 返回 false ——
据此可察觉"部分关系未写入"，不必逐个检查实体标识是否回填。

## 五、继承 ITemplate 的通用方法

`IGraphTemplate` 继承框架统一的 `ITemplate<String>`，主键即图库内部标识：

| 方法 | 语义 |
|------|------|
| `getById(id, clz)` | 按图库标识查单个节点 |
| `listByIds(ids, clz)` | 按图库标识批量查节点 |
| `get(column, value, clz)` | 按属性查单个节点 |
| `listByColumn(column, value(s), clz)` | 按属性查节点列表 |
| `insert(entities)` | 批量新增，回填图库标识。全部写入返回 true，有实体被跳过返回 false |
| `deleteById(ids, clz)` | 按图库标识删除。有记录被实际删除返回 true，标识均未匹配返回 false |

返回值约定：执行出错一律抛 `ServiceException`，不以返回 false 表示错误。
因此 false 只意味着"没做成这件事"（关系两端未落库而跳过、标识未匹配到记录），
而非"执行失败"。

# 配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `fun.data.graph.default-datasource` | 默认数据源名称 | 空字符串 |
| `fun.data.graph.entity-packages` | 图实体扫描包 | 启动类所在包 |
| `fun.data.graph.configs[].name` | 数据源名称 | 空字符串 |
| `fun.data.graph.configs[].uri` | 连接地址 | `bolt://localhost:7687` |
| `fun.data.graph.configs[].username` | 用户名，为空时免认证 | 无 |
| `fun.data.graph.configs[].password` | 密码 | 无 |
| `fun.data.graph.configs[].database` | 数据库名，为空时用图库默认库 | 无 |
| `fun.data.graph.configs[].connect-timeout` | 连接超时 | 驱动默认 |
| `fun.data.graph.configs[].connection-acquisition-timeout` | 连接获取超时 | 驱动默认 |
| `fun.data.graph.configs[].max-connection-lifetime` | 连接最大存活时间 | 驱动默认 |
| `fun.data.graph.configs[].max-connection-pool-size` | 连接池最大连接数 | 驱动默认 |
| `fun.data.graph.configs[].fetch-size` | 单次拉取记录数 | 驱动默认 |
| `fun.data.graph.configs[].encrypted` | 是否启用传输加密 | 驱动默认 |

多数据源按实体上的 `@Entity(datasource = "xxx")` 路由，未声明则走默认数据源。
不涉及实体类型的方法（直接执行 Cypher）走默认数据源，需指定时用 `findTemplate(name)` 取具体模板。

# 扩展点

| 扩展点 | 覆盖方式 |
|--------|---------|
| `IGraphMapper` | 注册自定义 bean 即覆盖默认反射映射引擎 |
| `BaseMultiDatasourceGraphTemplate` | 注册自定义 bean 即覆盖框架默认模板 |
| `BaseGraphTemplate` | 继承并实现三个驱动原语，接入新的图数据库 |

多数据源的索引、路由、回退、子模板缓存由 core-model 的
`BaseMultiDatasourceTemplate` 承担，本模块的 `BaseMultiDatasourceGraphTemplate`
只做方法委派，因此路由行为与其他数据模块一致。

# 安全说明

标签、关系类型、属性名无法通过 Cypher 参数绑定，只能进入语句文本，
因此 `CypherBuilder` 在拼接前经 core-model 的 `IdentifierUtil` 强制白名单校验
（字母或下划线开头，其后为字母、数字、下划线），不合法直接抛业务异常而非转义。
属性值一律参数绑定，不做字符串拼接。

自行拼接 Cypher 时请遵循同样约束：值走 `params`，标识符过 `IdentifierUtil`，
勿把外部输入拼进语句。

# 异常码

图特有异常：

| 错误码 | 说明 |
|--------|------|
| 12300 | 图查询执行失败 |
| 12301 | 图数据库功能不支持 |
| 12302 | 实体未标注 `@GraphNode` 或 `@GraphRelationship` |

通用数据异常复用 core-model 的 `FunCoreDataExceptionEnum`：

| 错误码 | 说明 |
|--------|------|
| 100603 | 指定数据源不存在 |
| 100604 | 未配置任何数据源 |
| 100605 | 结果映射失败 |
| 100606 | 标识符非法（标签/关系类型/属性名） |
| 100607 | 实体未声明业务主键 |
