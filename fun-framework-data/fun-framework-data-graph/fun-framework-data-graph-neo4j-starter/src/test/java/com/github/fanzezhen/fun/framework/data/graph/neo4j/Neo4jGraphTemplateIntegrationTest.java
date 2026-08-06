package com.github.fanzezhen.fun.framework.data.graph.neo4j;

import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.core.model.annotation.Column;
import com.github.fanzezhen.fun.framework.core.model.common.FunFunction;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.EndNode;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphId;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphLabels;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphNode;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphRelationship;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphType;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.StartNode;
import com.github.fanzezhen.fun.framework.data.graph.base.config.FunGraphProperties;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.DefaultGraphMapper;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;
import com.github.fanzezhen.fun.framework.data.graph.base.template.IGraphTemplate;
import com.github.fanzezhen.fun.framework.data.graph.neo4j.config.Neo4jDriverFactory;
import com.github.fanzezhen.fun.framework.data.graph.neo4j.template.Neo4jGraphTemplate;
import lombok.Data;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Neo4j 图模板集成测试
 * <p>
 * 用 {@code neo4j-harness} 启动进程内 Neo4j（走真实 Bolt 协议），验证 Mockito 桩测
 * 覆盖不到的部分：生成的 Cypher 能否被真实图库执行、真实驱动返回值能否正确归一化并映射。
 * </p>
 * <p>
 * 进程内服务启动为秒级，故整类共用一个实例（{@link BeforeAll}），
 * 每个用例前清库保证相互隔离。
 * </p>
 *
 * @since 4.1.1
 */
@DisplayName("Neo4j 图模板集成（进程内真实图库）")
class Neo4jGraphTemplateIntegrationTest {

    /**
     * 进程内 Neo4j 服务
     */
    private static Neo4j neo4j;

    /**
     * 驱动
     */
    private static Driver driver;

    /**
     * 被测模板
     */
    private static IGraphTemplate graphTemplate;

    @BeforeAll
    static void startNeo4j() {
        neo4j = Neo4jBuilders.newInProcessBuilder().withDisabledServer().build();
        final FunGraphProperties.Config config = new FunGraphProperties.Config();
        config.setUri(neo4j.boltURI().toString());
        driver = Neo4jDriverFactory.createDriver(config);
        graphTemplate = new Neo4jGraphTemplate(driver, config, passThroughLogHelper(), new DefaultGraphMapper());
    }

    @AfterAll
    static void stopNeo4j() {
        if (driver != null) {
            driver.close();
        }
        if (neo4j != null) {
            neo4j.close();
        }
    }

    @BeforeEach
    void clearDatabase() {
        graphTemplate.execute("MATCH (n) DETACH DELETE n");
    }

    @Test
    @DisplayName("新增节点：写入真实图库并回填 elementId，可据此查回")
    void testInsertNode_ShouldPersistAndWriteBackElementId() {
        final CityNode city = newCity("C001", "上海", 2489);

        graphTemplate.insert(List.<IEntity<String>>of(city));

        assertNotNull(city.getElementId(), "insert 后应回填图库标识");
        final CityNode loaded = graphTemplate.getById(city.getElementId(), CityNode.class);
        assertNotNull(loaded);
        assertAll(
            () -> assertEquals("C001", loaded.getCityId()),
            () -> assertEquals("上海", loaded.getName()),
            () -> assertEquals(2489, loaded.getPopulation()),
            () -> assertEquals(city.getElementId(), loaded.getElementId()),
            () -> assertEquals(Set.of("City"), loaded.getLabelSet())
        );
    }

    @Test
    @DisplayName("只读属性不参与写入，真实图库中不存在该属性")
    void testInsertNode_ReadOnlyColumn_ShouldNotBePersisted() {
        final CityNode city = newCity("C001", "上海", 2489);
        city.setRank(3);

        graphTemplate.insert(List.<IEntity<String>>of(city));

        final Long rankCount =
            graphTemplate.queryObject("MATCH (n:City) WHERE n.rank IS NOT NULL RETURN count(n)", Long.class);
        assertEquals(0L, rankCount);
    }

    @Test
    @DisplayName("按属性查询：参数绑定在真实图库上生效")
    void testListByColumn_ShouldBindParamAgainstRealDatabase() {
        graphTemplate.insert(List.<IEntity<String>>of(newCity("C001", "上海", 2489), newCity("C002", "北京", 2189)));

        final List<CityNode> cityList = graphTemplate.listByColumn("name", "上海", CityNode.class);

        assertAll(
            () -> assertEquals(1, cityList.size()),
            () -> assertEquals("C001", cityList.getFirst().getCityId())
        );
    }

    @Test
    @DisplayName("按业务主键合并：重复执行不产生重复节点，属性被更新")
    void testMerge_RepeatedExecution_ShouldNotDuplicateNode() {
        final CityNode first = newCity("C001", "上海", 2489);
        graphTemplate.merge(List.of(first));

        final CityNode second = newCity("C001", "上海市", 2500);
        graphTemplate.merge(List.of(second));

        final Long total = graphTemplate.queryObject("MATCH (n:City) RETURN count(n)", Long.class);
        final CityNode loaded = graphTemplate.get("city_id", "C001", CityNode.class);
        assertAll(
            () -> assertEquals(1L, total, "合并应更新而非新增"),
            () -> assertEquals("上海市", loaded.getName()),
            () -> assertEquals(2500, loaded.getPopulation())
        );
    }

    @Test
    @DisplayName("新增关系：真实图库中建立连接，查询时两端节点被回填")
    void testInsertRelationship_ShouldLinkEndpointsAndMapBack() {
        final CityNode start = newCity("C001", "上海", 2489);
        final CityNode end = newCity("C002", "北京", 2189);
        graphTemplate.insert(List.<IEntity<String>>of(start, end));
        final RouteRelationship route = new RouteRelationship();
        route.setStartNode(start);
        route.setEndNode(end);
        route.setDistance(1200);

        graphTemplate.insert(List.<IEntity<String>>of(route));

        assertNotNull(route.getElementId(), "insert 后应回填关系标识");
        final RouteRelationship loaded = graphTemplate.queryOne(
            "MATCH (s:City)-[r:ROUTE]->(t:City) RETURN s, r, t", RouteRelationship.class);
        assertNotNull(loaded);
        assertAll(
            () -> assertEquals("ROUTE", loaded.getType()),
            () -> assertEquals(1200, loaded.getDistance()),
            () -> assertNotNull(loaded.getStartNode(), "同行返回节点时起点应被回填"),
            () -> assertEquals("上海", loaded.getStartNode().getName()),
            () -> assertNotNull(loaded.getEndNode()),
            () -> assertEquals("北京", loaded.getEndNode().getName())
        );
    }

    @Test
    @DisplayName("只返回关系时两端节点为空，标识仍可用")
    void testQueryRelationshipOnly_ShouldKeepEndpointIdsWithoutNodes() {
        final CityNode start = newCity("C001", "上海", 2489);
        final CityNode end = newCity("C002", "北京", 2189);
        graphTemplate.insert(List.<IEntity<String>>of(start, end));
        final RouteRelationship route = new RouteRelationship();
        route.setStartNode(start);
        route.setEndNode(end);
        graphTemplate.insert(List.<IEntity<String>>of(route));

        final List<GraphRecordData> recordList =
            graphTemplate.queryRecordList("MATCH ()-[r:ROUTE]->() RETURN r", null);

        assertEquals(1, recordList.size());
        final var relationshipData =
            (com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRelationshipData)
                recordList.getFirst().get("r");
        assertAll(
            () -> assertNull(relationshipData.getStartNode()),
            () -> assertEquals(start.getElementId(), relationshipData.getStartNodeElementId()),
            () -> assertEquals(end.getElementId(), relationshipData.getEndNodeElementId())
        );
    }

    @Test
    @DisplayName("投影查询：按结果列名映射到非图实体")
    void testProjectionQuery_ShouldMapByColumnName() {
        graphTemplate.insert(List.<IEntity<String>>of(newCity("C001", "上海", 2489), newCity("C002", "北京", 2189)));

        final List<CitySummary> summaryList = graphTemplate.queryList(
            "MATCH (n:City) RETURN n.name AS name, n.population AS population ORDER BY n.population DESC",
            CitySummary.class);

        assertAll(
            () -> assertEquals(2, summaryList.size()),
            () -> assertEquals("上海", summaryList.getFirst().getName()),
            () -> assertEquals(2489, summaryList.getFirst().getPopulation())
        );
    }

    @Test
    @DisplayName("集合属性：List 属性在真实图库上往返一致")
    void testCollectionProperty_ShouldRoundTrip() {
        final CityNode city = newCity("C001", "上海", 2489);
        city.setAliasList(List.of("沪", "申城"));

        graphTemplate.insert(List.<IEntity<String>>of(city));

        final CityNode loaded = graphTemplate.getById(city.getElementId(), CityNode.class);
        assertEquals(List.of("沪", "申城"), loaded.getAliasList());
    }

    @Test
    @DisplayName("按标识批量查询节点")
    void testListByIds_ShouldReturnMatchedNodes() {
        final CityNode first = newCity("C001", "上海", 2489);
        final CityNode second = newCity("C002", "北京", 2189);
        graphTemplate.insert(List.<IEntity<String>>of(first, second));

        final List<CityNode> cityList =
            graphTemplate.listByIds(List.of(first.getElementId(), second.getElementId()), CityNode.class);

        assertEquals(2, cityList.size());
    }

    @Test
    @DisplayName("删除节点：DETACH DELETE 连带清理关系")
    void testDeleteById_ShouldRemoveNodeAndItsRelationships() {
        final CityNode start = newCity("C001", "上海", 2489);
        final CityNode end = newCity("C002", "北京", 2189);
        graphTemplate.insert(List.<IEntity<String>>of(start, end));
        final RouteRelationship route = new RouteRelationship();
        route.setStartNode(start);
        route.setEndNode(end);
        graphTemplate.insert(List.<IEntity<String>>of(route));

        final boolean deleted = graphTemplate.deleteById(List.of(start.getElementId()), CityNode.class);

        assertAll(
            () -> assertTrue(deleted, "确有记录被删除时应返回 true"),
            () -> assertNull(graphTemplate.getById(start.getElementId(), CityNode.class)),
            () -> assertEquals(0L,
                graphTemplate.queryObject("MATCH ()-[r:ROUTE]->() RETURN count(r)", Long.class),
                "DETACH DELETE 应连带删除关系"),
            () -> assertNotNull(graphTemplate.getById(end.getElementId(), CityNode.class), "另一端节点应保留")
        );
    }

    @Test
    @DisplayName("删除不存在的标识：返回 false 表示未匹配到记录")
    void testDeleteById_NonExistentId_ShouldReturnFalse() {
        final boolean deleted = graphTemplate.deleteById(List.of("4:not-exists:0"), CityNode.class);

        assertFalse(deleted);
    }

    @Test
    @DisplayName("新增：全部写入成功时返回 true")
    void testInsert_AllWritten_ShouldReturnTrue() {
        final boolean inserted =
            graphTemplate.insert(List.<IEntity<String>>of(newCity("C001", "上海", 2489)));

        assertTrue(inserted);
    }

    @Test
    @DisplayName("新增关系：两端节点未落库时跳过并返回 false")
    void testInsertRelationship_EndpointsNotPersisted_ShouldReturnFalse() {
        final RouteRelationship route = new RouteRelationship();
        route.setStartNode(newCity("C001", "上海", 2489));
        route.setEndNode(newCity("C002", "北京", 2189));

        final boolean inserted = graphTemplate.insert(List.<IEntity<String>>of(route));

        assertAll(
            () -> assertFalse(inserted, "端点无图库标识应返回 false"),
            () -> assertEquals(0L,
                graphTemplate.queryObject("MATCH ()-[r:ROUTE]->() RETURN count(r)", Long.class))
        );
    }

    @Test
    @DisplayName("写语句返回受影响记录数")
    void testExecute_ShouldReturnAffectedCount() {
        final long affected = graphTemplate.execute(
            "CREATE (n:City {city_id: $id, name: $name})", Map.of("id", "C001", "name", "上海"));

        assertTrue(affected > 0, "建节点并设属性应有受影响计数，实际：" + affected);
    }

    @Test
    @DisplayName("查不到时返回 null 与空列表，不抛异常")
    void testQueryMissing_ShouldReturnNullOrEmpty() {
        assertAll(
            () -> assertNull(graphTemplate.get("city_id", "NOT_EXISTS", CityNode.class)),
            () -> assertTrue(graphTemplate.listByColumn("name", "NOT_EXISTS", CityNode.class).isEmpty()),
            () -> assertTrue(graphTemplate.queryRecordList("MATCH (n:NotExists) RETURN n", null).isEmpty())
        );
    }

    @Test
    @DisplayName("原生结果访问：处理器可直接消费驱动 Result")
    void testQueryNative_ShouldExposeDriverResult() {
        graphTemplate.insert(List.<IEntity<String>>of(newCity("C001", "上海", 2489)));

        final Long count = graphTemplate.queryNative("MATCH (n:City) RETURN count(n)", null,
            result -> ((Result) result).single().get(0).asLong());

        assertEquals(1L, count);
    }

    /**
     * 构造城市节点
     *
     * @param cityId     业务主键
     * @param name       名称
     * @param population 人口
     * @return 城市节点
     */
    private static CityNode newCity(final String cityId, final String name, final Integer population) {
        final CityNode city = new CityNode();
        city.setCityId(cityId);
        city.setName(name);
        city.setPopulation(population);
        return city;
    }

    /**
     * 构造透传的日志辅助桩
     * <p>
     * {@link FunLogHelper} 的默认日志器由 Spring 生命周期初始化，非容器环境下直接 new 会缺失初始化。
     * </p>
     *
     * @return 日志辅助桩
     */
    private static FunLogHelper passThroughLogHelper() {
        final FunLogHelper funLogHelper = mock(FunLogHelper.class);
        when(funLogHelper.executeByLog(anyString(), any(), any()))
            .thenAnswer(invocation -> {
                final FunFunction<Object, Object> invoker = invocation.getArgument(1);
                return invoker.call(invocation.getArgument(2));
            });
        return funLogHelper;
    }

    /**
     * 城市节点测试实体
     */
    @Data
    @GraphNode(label = "City")
    public static class CityNode implements IEntity<String> {

        /**
         * 图库内部标识
         */
        @GraphId
        private String elementId;

        /**
         * 业务主键
         */
        @Column(name = "city_id", isPrimaryKey = true)
        private String cityId;

        /**
         * 名称
         */
        private String name;

        /**
         * 人口
         */
        private Integer population;

        /**
         * 别名列表
         */
        @Column(name = "alias_list")
        private List<String> aliasList;

        /**
         * 标签集合
         */
        @GraphLabels
        private Set<String> labelSet;

        /**
         * 只读属性，不参与写入
         */
        @Column(name = "rank", writable = false)
        private Integer rank;

        @Override
        public String getId() {
            return elementId;
        }

        @Override
        public IEntity<String> setId(final String id) {
            this.elementId = id;
            return this;
        }
    }

    /**
     * 航线关系测试实体
     */
    @Data
    @GraphRelationship(type = "ROUTE")
    public static class RouteRelationship implements IEntity<String> {

        /**
         * 图库内部标识
         */
        @GraphId
        private String elementId;

        /**
         * 关系类型
         */
        @GraphType
        private String type;

        /**
         * 起始节点
         */
        @StartNode
        private CityNode startNode;

        /**
         * 结束节点
         */
        @EndNode
        private CityNode endNode;

        /**
         * 距离
         */
        private Integer distance;

        @Override
        public String getId() {
            return elementId;
        }

        @Override
        public IEntity<String> setId(final String id) {
            this.elementId = id;
            return this;
        }
    }

    /**
     * 投影结果测试实体
     */
    @Data
    public static class CitySummary {

        /**
         * 名称
         */
        private String name;

        /**
         * 人口
         */
        private Integer population;
    }
}
