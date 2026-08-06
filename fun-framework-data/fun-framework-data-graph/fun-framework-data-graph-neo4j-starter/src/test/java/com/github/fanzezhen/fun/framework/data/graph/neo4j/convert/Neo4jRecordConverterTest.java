package com.github.fanzezhen.fun.framework.data.graph.neo4j.convert;

import com.github.fanzezhen.fun.framework.data.graph.base.constant.FunGraphConstant;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphNodeData;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRelationshipData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.IsoDuration;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Point;
import org.neo4j.driver.types.Relationship;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Neo4j 结果转换器测试
 * <p>
 * 转换器是驱动与抽象层的唯一边界，重点验证转换后不再残留任何驱动类型。
 * </p>
 */
@DisplayName("Neo4j 结果转换器")
class Neo4jRecordConverterTest {

    @Test
    @DisplayName("节点转换：标识、标签、属性齐备")
    void testToNodeData_ShouldCarryElementIdLabelsAndProperties() {
        final Node node = mockNode("4:db:1", List.of("Person", "Employee"),
            Map.of("name", "小明", "age", 30L));

        final GraphNodeData nodeData = Neo4jRecordConverter.toNodeData(node);

        assertAll(
            () -> assertEquals("4:db:1", nodeData.getElementId()),
            () -> assertEquals(java.util.Set.of("Person", "Employee"), nodeData.getLabels()),
            () -> assertEquals("小明", nodeData.getProperty("name")),
            () -> assertEquals(30L, nodeData.getProperty("age"))
        );
    }

    @Test
    @DisplayName("关系转换：类型与两端标识齐备，节点数据待行内回填")
    void testToRelationshipData_ShouldCarryTypeAndEndpointIds() {
        final Relationship relationship =
            mockRelationship("5:db:9", "KNOWS", "4:db:1", "4:db:2", Map.of("since", 2019L));

        final GraphRelationshipData relationshipData = Neo4jRecordConverter.toRelationshipData(relationship);

        assertAll(
            () -> assertEquals("5:db:9", relationshipData.getElementId()),
            () -> assertEquals("KNOWS", relationshipData.getType()),
            () -> assertEquals("4:db:1", relationshipData.getStartNodeElementId()),
            () -> assertEquals("4:db:2", relationshipData.getEndNodeElementId()),
            () -> assertEquals(2019L, relationshipData.getProperty("since")),
            () -> assertNull(relationshipData.getStartNode())
        );
    }

    @Test
    @DisplayName("行内回填：同行返回节点与关系时，关系两端节点被关联")
    void testToRecordData_ShouldLinkRelationshipEndpointsWithinRecord() {
        final Node start = mockNode("4:db:1", List.of("Person"), Map.of("name", "小明"));
        final Node end = mockNode("4:db:2", List.of("Person"), Map.of("name", "小红"));
        final Relationship relationship = mockRelationship("5:db:9", "KNOWS", "4:db:1", "4:db:2", Map.of());
        final Record driverRecord = mockRecord(Map.of(
            "a", mockValue(start),
            "r", mockValue(relationship),
            "b", mockValue(end)));

        final GraphRecordData recordData = Neo4jRecordConverter.toRecordData(driverRecord);
        final GraphRelationshipData relationshipData = (GraphRelationshipData) recordData.get("r");

        assertAll(
            () -> assertNotNull(relationshipData.getStartNode()),
            () -> assertEquals("小明", relationshipData.getStartNode().getProperty("name")),
            () -> assertNotNull(relationshipData.getEndNode()),
            () -> assertEquals("小红", relationshipData.getEndNode().getProperty("name")),
            // 回填复用同一实例，不重复构造
            () -> assertSame(recordData.get("a"), relationshipData.getStartNode())
        );
    }

    @Test
    @DisplayName("行内回填：关系两端节点未在同行返回时保持为空，不报错")
    void testToRecordData_WithoutEndpointNodes_ShouldKeepNull() {
        final Relationship relationship = mockRelationship("5:db:9", "KNOWS", "4:db:1", "4:db:2", Map.of());
        final Record driverRecord = mockRecord(Map.of("r", mockValue(relationship)));

        final GraphRelationshipData relationshipData =
            (GraphRelationshipData) Neo4jRecordConverter.toRecordData(driverRecord).get("r");

        assertAll(
            () -> assertNull(relationshipData.getStartNode()),
            () -> assertEquals("4:db:1", relationshipData.getStartNodeElementId())
        );
    }

    @Test
    @DisplayName("集合与嵌套映射内的节点同样被归一化，不残留驱动类型")
    void testToRecordData_NestedCollection_ShouldNormalizeElements() {
        final Node nodeOne = mockNode("4:db:1", List.of("Person"), Map.of("name", "小明"));
        final Node nodeTwo = mockNode("4:db:2", List.of("Person"), Map.of("name", "小红"));
        final Record driverRecord = mockRecord(Map.of("friends", mockValue(List.of(nodeOne, nodeTwo))));

        final Object friends = Neo4jRecordConverter.toRecordData(driverRecord).get("friends");

        assertInstanceOf(List.class, friends);
        final List<?> friendList = (List<?>) friends;
        assertAll(
            () -> assertEquals(2, friendList.size()),
            () -> assertInstanceOf(GraphNodeData.class, friendList.get(0)),
            () -> assertEquals("小明", ((GraphNodeData) friendList.get(0)).getProperty("name"))
        );
    }

    @Test
    @DisplayName("路径转换：拆为节点与关系列表，关系两端已关联")
    void testToRecordData_Path_ShouldSplitIntoNodesAndRelationships() {
        final Node start = mockNode("4:db:1", List.of("City"), Map.of("name", "上海"));
        final Node end = mockNode("4:db:2", List.of("City"), Map.of("name", "北京"));
        final Relationship relationship = mockRelationship("5:db:1", "ROUTE", "4:db:1", "4:db:2", Map.of());
        final Path path = mock(Path.class);
        when(path.nodes()).thenReturn(List.of(start, end));
        when(path.relationships()).thenReturn(List.of(relationship));
        final Record driverRecord = mockRecord(Map.of("p", mockValue(path)));

        @SuppressWarnings("unchecked")
        final Map<String, Object> pathMap =
            (Map<String, Object>) Neo4jRecordConverter.toRecordData(driverRecord).get("p");
        @SuppressWarnings("unchecked")
        final List<GraphRelationshipData> relationshipList =
            (List<GraphRelationshipData>) pathMap.get(FunGraphConstant.PATH_RELATIONSHIPS);

        assertAll(
            () -> assertEquals(2, ((List<?>) pathMap.get(FunGraphConstant.PATH_NODES)).size()),
            () -> assertEquals(1, relationshipList.size()),
            () -> assertNotNull(relationshipList.get(0).getStartNode()),
            () -> assertEquals("上海", relationshipList.get(0).getStartNode().getProperty("name"))
        );
    }

    @Test
    @DisplayName("空间点转换为坐标映射，不泄漏驱动类型")
    void testToObject_Point_ShouldConvertToMap() {
        final Value pointValue = Values.point(4326, 121.47, 31.23);

        @SuppressWarnings("unchecked")
        final Map<String, Object> pointMap = (Map<String, Object>) Neo4jRecordConverter.toObject(pointValue);

        assertAll(
            () -> assertEquals(4326, pointMap.get(FunGraphConstant.POINT_SRID)),
            () -> assertEquals(121.47, pointMap.get(FunGraphConstant.POINT_X)),
            () -> assertEquals(31.23, pointMap.get(FunGraphConstant.POINT_Y)),
            () -> assertTrue(!(Neo4jRecordConverter.toObject(pointValue) instanceof Point))
        );
    }

    @Test
    @DisplayName("时长转换为月天秒纳秒映射")
    void testToObject_IsoDuration_ShouldConvertToMap() {
        final Value durationValue = Values.isoDuration(1L, 2L, 3L, 4);

        @SuppressWarnings("unchecked")
        final Map<String, Object> durationMap = (Map<String, Object>) Neo4jRecordConverter.toObject(durationValue);

        assertAll(
            () -> assertEquals(1L, durationMap.get(FunGraphConstant.DURATION_MONTHS)),
            () -> assertEquals(2L, durationMap.get(FunGraphConstant.DURATION_DAYS)),
            () -> assertEquals(3L, durationMap.get(FunGraphConstant.DURATION_SECONDS)),
            () -> assertEquals(4, durationMap.get(FunGraphConstant.DURATION_NANOSECONDS)),
            () -> assertTrue(!(Neo4jRecordConverter.toObject(durationValue) instanceof IsoDuration))
        );
    }

    @Test
    @DisplayName("标量与时间类型本就是 JDK 类型，原样保留")
    void testToObject_ScalarAndTemporal_ShouldPassThrough() {
        assertAll(
            () -> assertEquals("文本", Neo4jRecordConverter.toObject(Values.value("文本"))),
            () -> assertEquals(42L, Neo4jRecordConverter.toObject(Values.value(42))),
            () -> assertEquals(true, Neo4jRecordConverter.toObject(Values.value(true))),
            () -> assertEquals(LocalDate.of(2026, Month.AUGUST, 5),
                Neo4jRecordConverter.toObject(Values.value(LocalDate.of(2026, Month.AUGUST, 5))))
        );
    }

    @Test
    @DisplayName("空值与空输入的处理")
    void testNullHandling() {
        assertAll(
            () -> assertNull(Neo4jRecordConverter.toObject(null)),
            () -> assertNull(Neo4jRecordConverter.toObject(Values.NULL)),
            () -> assertNull(Neo4jRecordConverter.toNodeData(null)),
            () -> assertNull(Neo4jRecordConverter.toRelationshipData(null)),
            () -> assertTrue(Neo4jRecordConverter.toRecordData(null).isEmpty()),
            () -> assertTrue(Neo4jRecordConverter.toRecordDataList(null).isEmpty()),
            () -> assertTrue(Neo4jRecordConverter.toRecordDataList(List.of()).isEmpty())
        );
    }

    @Test
    @DisplayName("批量转换：行数与结果数一致")
    void testToRecordDataList_ShouldConvertEachRecord() {
        final Record recordOne = mockRecord(Map.of("v", Values.value(1)));
        final Record recordTwo = mockRecord(Map.of("v", Values.value(2)));

        final List<GraphRecordData> recordDataList =
            Neo4jRecordConverter.toRecordDataList(List.of(recordOne, recordTwo));

        assertAll(
            () -> assertEquals(2, recordDataList.size()),
            () -> assertEquals(1L, recordDataList.get(0).get("v")),
            () -> assertEquals(2L, recordDataList.get(1).get("v"))
        );
    }

    /**
     * 构造节点桩
     *
     * @param elementId  图库标识
     * @param labelList  标签列表
     * @param properties 属性
     * @return 节点桩
     */
    private static Node mockNode(final String elementId,
                                 final List<String> labelList,
                                 final Map<String, Object> properties) {
        final Node node = mock(Node.class);
        when(node.elementId()).thenReturn(elementId);
        when(node.labels()).thenReturn(labelList);
        when(node.asMap()).thenReturn(properties);
        return node;
    }

    /**
     * 构造关系桩
     *
     * @param elementId  图库标识
     * @param type       关系类型
     * @param startId    起始节点标识
     * @param endId      结束节点标识
     * @param properties 属性
     * @return 关系桩
     */
    private static Relationship mockRelationship(final String elementId,
                                                 final String type,
                                                 final String startId,
                                                 final String endId,
                                                 final Map<String, Object> properties) {
        final Relationship relationship = mock(Relationship.class);
        when(relationship.elementId()).thenReturn(elementId);
        when(relationship.type()).thenReturn(type);
        when(relationship.startNodeElementId()).thenReturn(startId);
        when(relationship.endNodeElementId()).thenReturn(endId);
        when(relationship.asMap()).thenReturn(properties);
        return relationship;
    }

    /**
     * 构造包装指定对象的驱动值桩
     * <p>
     * {@code Values.value()} 只接受驱动自己构造的图元素，无法包装 mock 出的
     * 节点与关系，故此处直接桩化 {@link Value} 的 {@code asObject}。
     * </p>
     *
     * @param object 被包装的对象
     * @return 驱动值桩
     */
    private static Value mockValue(final Object object) {
        final Value value = mock(Value.class);
        when(value.isNull()).thenReturn(object == null);
        when(value.asObject()).thenReturn(object);
        return value;
    }

    /**
     * 构造结果行桩
     *
     * @param columnMap 列名到驱动值的映射
     * @return 结果行桩
     */
    private static Record mockRecord(final Map<String, Value> columnMap) {
        final Record driverRecord = mock(Record.class);
        when(driverRecord.keys()).thenReturn(List.copyOf(columnMap.keySet()));
        columnMap.forEach((key, value) -> when(driverRecord.get(key)).thenReturn(value));
        return driverRecord;
    }
}
