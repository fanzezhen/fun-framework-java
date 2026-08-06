package com.github.fanzezhen.fun.framework.data.graph.base.template;

import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.core.model.annotation.Column;
import com.github.fanzezhen.fun.framework.core.model.common.FunFunction;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.core.model.enums.FunCoreDataExceptionEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.EndNode;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphId;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphNode;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphRelationship;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.StartNode;
import com.github.fanzezhen.fun.framework.data.graph.base.config.FunGraphProperties;
import com.github.fanzezhen.fun.framework.data.graph.base.enums.FunDataGraphExceptionEnum;
import com.github.fanzezhen.fun.framework.data.graph.base.mapping.DefaultGraphMapper;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 图模板抽象基类测试
 * <p>
 * 用桩实现替代驱动，验证 CRUD 到 Cypher 的接线、参数绑定与写入回填。
 * </p>
 */
@DisplayName("图模板抽象基类")
class BaseGraphTemplateTest {

    /**
     * 被测模板
     */
    private StubGraphTemplate graphTemplate;

    @BeforeEach
    void setUp() {
        graphTemplate = new StubGraphTemplate();
    }

    @Test
    @DisplayName("按标识查询：生成 elementId 匹配语句并绑定标识参数")
    void testGetById_ShouldBuildElementIdMatchAndBindParam() {
        graphTemplate.getById("4:db:1", CityNode.class);

        assertAll(
            () -> assertEquals("MATCH (n:`City`) WHERE elementId(n) = $id RETURN n", graphTemplate.lastCypher),
            () -> assertEquals(Map.of("id", "4:db:1"), graphTemplate.lastParams)
        );
    }

    @Test
    @DisplayName("按属性查询：属性值走参数绑定，不进入语句文本")
    void testListByColumn_ShouldBindValueAsParam() {
        graphTemplate.listByColumn("name", "上海", CityNode.class);

        assertAll(
            () -> assertEquals("MATCH (n:`City`) WHERE n.`name` = $value RETURN n", graphTemplate.lastCypher),
            () -> assertEquals(Map.of("value", "上海"), graphTemplate.lastParams)
        );
    }

    @Test
    @DisplayName("按标识集合查询：空集合直接返回空列表，不发起查询")
    void testListByIds_EmptyIds_ShouldSkipQuery() {
        final List<CityNode> result = graphTemplate.listByIds(Collections.emptyList(), CityNode.class);

        assertAll(
            () -> assertTrue(result.isEmpty()),
            () -> assertEquals(0, graphTemplate.queryCount)
        );
    }

    @Test
    @DisplayName("新增节点：只带可写属性，图库标识回填到实体")
    void testInsert_ShouldWriteWritablePropertiesAndWriteBackElementId() {
        final CityNode city = new CityNode();
        city.setCityId("C001");
        city.setName("上海");
        city.setPopulation(2489);
        city.setRank(3);
        graphTemplate.nextRecordList = List.of(new GraphRecordData(Map.of("elementId(n)", "4:db:99")));

        graphTemplate.insert(List.<IEntity<String>>of(city));

        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> rowList = (List<Map<String, Object>>) graphTemplate.lastParams.get("rows");
        assertAll(
            () -> assertEquals("UNWIND $rows AS row CREATE (n:`City`) SET n = row RETURN elementId(n)",
                graphTemplate.lastCypher),
            () -> assertEquals(1, rowList.size()),
            () -> assertEquals("C001", rowList.get(0).get("city_id")),
            () -> assertEquals("上海", rowList.get(0).get("name")),
            () -> assertEquals(2489, rowList.get(0).get("population")),
            // rank 标注为只读，不参与写入
            () -> assertFalse(rowList.get(0).containsKey("rank")),
            // 图库标识回填后可直接用于后续按标识的操作
            () -> assertEquals("4:db:99", city.getElementId())
        );
    }

    @Test
    @DisplayName("新增节点：值为 null 的属性不写入，避免被当作清空属性")
    void testInsert_NullProperty_ShouldNotBeWritten() {
        final CityNode city = new CityNode();
        city.setCityId("C001");

        graphTemplate.insert(List.<IEntity<String>>of(city));

        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> rowList = (List<Map<String, Object>>) graphTemplate.lastParams.get("rows");
        assertAll(
            () -> assertEquals(1, rowList.get(0).size()),
            () -> assertEquals("C001", rowList.get(0).get("city_id"))
        );
    }

    @Test
    @DisplayName("合并节点：按业务主键 MERGE")
    void testMerge_ShouldMergeOnBusinessKey() {
        final CityNode city = new CityNode();
        city.setCityId("C001");

        graphTemplate.merge(List.of(city));

        assertEquals("UNWIND $rows AS row MERGE (n:`City` {`city_id`: row.`city_id`}) "
            + "SET n += row RETURN elementId(n)", graphTemplate.lastCypher);
    }

    @Test
    @DisplayName("合并节点：未声明业务主键时抛出业务异常")
    void testMerge_WithoutBusinessId_ShouldThrowServiceException() {
        final NoBusinessIdNode node = new NoBusinessIdNode();
        node.setName("无主键");
        final List<NoBusinessIdNode> nodeList = List.of(node);

        final ServiceException exception =
            assertThrows(ServiceException.class, () -> graphTemplate.merge(nodeList));

        assertEquals(FunCoreDataExceptionEnum.PRIMARY_KEY_MISSING.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("混合类型写入：按实体类型分组，各自生成语句")
    void testInsert_MixedEntityTypes_ShouldGroupByClass() {
        final CityNode city = new CityNode();
        city.setCityId("C001");
        final NoBusinessIdNode other = new NoBusinessIdNode();
        other.setName("其他");

        graphTemplate.insert(List.<IEntity<String>>of(city, other));

        assertAll(
            () -> assertEquals(2, graphTemplate.queryCount),
            () -> assertTrue(graphTemplate.cypherList.stream().anyMatch(cypher -> cypher.contains("`City`"))),
            () -> assertTrue(graphTemplate.cypherList.stream()
                .anyMatch(cypher -> cypher.contains("`NoBusinessIdNode`")))
        );
    }

    @Test
    @DisplayName("新增关系：按起止节点标识定位两端")
    void testInsert_Relationship_ShouldUseEndpointElementIds() {
        final CityNode start = new CityNode();
        start.setElementId("4:db:1");
        final CityNode end = new CityNode();
        end.setElementId("4:db:2");
        final RouteRelationship route = new RouteRelationship();
        route.setStartNode(start);
        route.setEndNode(end);
        route.setDistance(1200);

        graphTemplate.insert(List.<IEntity<String>>of(route));

        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> rowList = (List<Map<String, Object>>) graphTemplate.lastParams.get("rows");
        assertAll(
            () -> assertTrue(graphTemplate.lastCypher.contains("CREATE (s)-[r:`ROUTE`]->(t)")),
            () -> assertEquals("4:db:1", rowList.get(0).get("startId")),
            () -> assertEquals("4:db:2", rowList.get(0).get("endId"))
        );
    }

    @Test
    @DisplayName("新增关系：端点缺少图库标识时跳过该行，不发起查询且返回 false")
    void testInsert_RelationshipWithoutEndpointId_ShouldSkipAndReturnFalse() {
        final RouteRelationship route = new RouteRelationship();
        route.setStartNode(new CityNode());
        route.setEndNode(new CityNode());

        final boolean inserted = graphTemplate.insert(List.<IEntity<String>>of(route));

        assertAll(
            () -> assertEquals(0, graphTemplate.queryCount),
            // 返回值让调用方能察觉"部分关系未写入"，而非静默丢失
            () -> assertFalse(inserted, "被跳过的关系应使返回值为 false")
        );
    }

    @Test
    @DisplayName("新增节点：全部写入时返回 true")
    void testInsert_AllWritten_ShouldReturnTrue() {
        final CityNode city = new CityNode();
        city.setCityId("C001");
        graphTemplate.nextRecordList = List.of(new GraphRecordData(Map.of("elementId(n)", "4:db:99")));

        assertTrue(graphTemplate.insert(List.<IEntity<String>>of(city)));
    }

    @Test
    @DisplayName("新增节点：写入行数少于实体数时返回 false")
    void testInsert_PartiallyWritten_ShouldReturnFalse() {
        final CityNode first = new CityNode();
        first.setCityId("C001");
        final CityNode second = new CityNode();
        second.setCityId("C002");
        // 图库只回传一行，表示仅一个节点写入成功
        graphTemplate.nextRecordList = List.of(new GraphRecordData(Map.of("elementId(n)", "4:db:99")));

        assertFalse(graphTemplate.insert(List.<IEntity<String>>of(first, second)));
    }

    @Test
    @DisplayName("删除节点：DETACH DELETE 连带清理关系")
    void testDeleteById_Node_ShouldDetachDelete() {
        graphTemplate.deleteById(List.of("4:db:1"), CityNode.class);

        assertAll(
            () -> assertEquals("MATCH (n:`City`) WHERE elementId(n) IN $ids DETACH DELETE n",
                graphTemplate.lastCypher),
            () -> assertEquals(Map.of("ids", List.of("4:db:1")), graphTemplate.lastParams)
        );
    }

    @Test
    @DisplayName("删除关系：只删关系不动两端节点")
    void testDeleteById_Relationship_ShouldDeleteRelationshipOnly() {
        graphTemplate.deleteById(List.of("5:db:1"), RouteRelationship.class);

        assertEquals("MATCH ()-[r:`ROUTE`]->() WHERE elementId(r) IN $ids DELETE r", graphTemplate.lastCypher);
    }

    @Test
    @DisplayName("未标注图注解的类型执行 CRUD 时抛出业务异常")
    void testCrud_NonGraphEntity_ShouldThrowServiceException() {
        final ServiceException exception =
            assertThrows(ServiceException.class, () -> graphTemplate.getById("1", PlainDto.class));

        assertEquals(FunDataGraphExceptionEnum.GRAPH_ENTITY_ANNOTATION_MISSING.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("按 null 条件查询：正常绑定 null 参数，不抛空指针")
    void testGetByNullValue_ShouldBindNullParamWithoutNpe() {
        graphTemplate.getById(null, CityNode.class);

        assertAll(
            () -> assertEquals("MATCH (n:`City`) WHERE elementId(n) = $id RETURN n", graphTemplate.lastCypher),
            () -> assertTrue(graphTemplate.lastParams.containsKey("id")),
            () -> assertNull(graphTemplate.lastParams.get("id"))
        );
    }

    @Test
    @DisplayName("参数为 null 时归一化为空映射，不透传给驱动")
    void testQueryRecordList_NullParams_ShouldNormalizeToEmptyMap() {
        graphTemplate.queryRecordList("MATCH (n) RETURN n", null);

        assertEquals(Collections.emptyMap(), graphTemplate.lastParams);
    }

    /**
     * 图模板桩实现，记录最后一次执行的语句与参数
     */
    private static final class StubGraphTemplate extends BaseGraphTemplate {

        /**
         * 最后一次执行的语句
         */
        private String lastCypher;

        /**
         * 最后一次执行的参数
         */
        private Map<String, Object> lastParams;

        /**
         * 历次执行的语句
         */
        private final List<String> cypherList = new ArrayList<>();

        /**
         * 查询执行次数
         */
        private int queryCount;

        /**
         * 下次查询返回的结果
         */
        private List<GraphRecordData> nextRecordList = Collections.emptyList();

        private StubGraphTemplate() {
            super(new FunGraphProperties.Config(), passThroughLogHelper(), new DefaultGraphMapper());
        }

        /**
         * 构造透传的日志辅助桩
         * <p>
         * {@link FunLogHelper} 的默认日志器由 Spring 生命周期初始化，
         * 纯单测环境下直接 new 会缺失初始化，故用桩透传执行。
         * </p>
         *
         * @return 日志辅助桩
         */
        @SuppressWarnings("unchecked")
        private static FunLogHelper passThroughLogHelper() {
            final FunLogHelper funLogHelper = mock(FunLogHelper.class);
            when(funLogHelper.executeByLog(anyString(), any(), any()))
                .thenAnswer(invocation -> {
                    final FunFunction<Object, Object> invoker = invocation.getArgument(1);
                    return invoker.call(invocation.getArgument(2));
                });
            return funLogHelper;
        }

        @Override
        protected List<GraphRecordData> doQuery(final String cypher, final Map<String, Object> params) {
            capture(cypher, params);
            queryCount++;
            return nextRecordList;
        }

        @Override
        protected long doExecute(final String cypher, final Map<String, Object> params) {
            capture(cypher, params);
            return 1L;
        }

        @Override
        public <R> R queryNative(final String cypher,
                                 final Map<String, Object> params,
                                 final Function<Object, R> resultHandler) {
            capture(cypher, params);
            return resultHandler.apply(nextRecordList);
        }

        /**
         * 记录执行的语句与参数
         *
         * @param cypher 语句
         * @param params 参数
         */
        private void capture(final String cypher, final Map<String, Object> params) {
            this.lastCypher = cypher;
            this.lastParams = params;
            this.cypherList.add(cypher);
        }
    }

    /**
     * 城市节点测试实体
     */
    @Data
    @GraphNode(label = "City")
    private static class CityNode implements IEntity<String> {

        /**
         * 图库内部标识
         */
        @GraphId
        private String elementId;

        /**
         * 业务主键，用框架统一的列注解声明
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
         * 图库侧计算的只读属性
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
     * 无业务主键的节点测试实体
     */
    @Data
    @GraphNode
    private static class NoBusinessIdNode implements IEntity<String> {

        /**
         * 图库内部标识
         */
        @GraphId
        private String elementId;

        /**
         * 名称
         */
        private String name;

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
    private static class RouteRelationship implements IEntity<String> {

        /**
         * 图库内部标识
         */
        @GraphId
        private String elementId;

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
     * 未标注图注解的普通类型
     */
    @Data
    private static class PlainDto {

        /**
         * 名称
         */
        private String name;
    }
}
