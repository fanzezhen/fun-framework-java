package com.github.fanzezhen.fun.framework.data.graph.base.mapping;

import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphNodeData;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRelationshipData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 默认图映射引擎测试
 */
@DisplayName("默认图映射引擎")
class DefaultGraphMapperTest {

    /**
     * 被测映射引擎
     */
    private final IGraphMapper graphMapper = new DefaultGraphMapper();

    @Test
    @DisplayName("节点实体：单列节点直接映射到实体，含标识、标签、类型转换")
    void testMapOne_NodeEntityColumn_ShouldMapElementDirectly() {
        final GraphNodeData nodeData = GraphTestData.personNodeData("4:db:1", "P001", "小明");
        final GraphRecordData recordData = GraphTestData.singleColumnRecord("n", nodeData);

        final GraphTestData.PersonNode person = graphMapper.mapOne(recordData, GraphTestData.PersonNode.class);

        assertNotNull(person);
        assertAll(
            () -> assertEquals("4:db:1", person.getElementId()),
            () -> assertEquals("P001", person.getPersonId()),
            () -> assertEquals("小明", person.getName()),
            // 属性中 age 是字符串 "30"，应转为 Integer
            () -> assertEquals(30, person.getAge()),
            () -> assertEquals(List.of("小明", "明明"), person.getAliasList()),
            () -> assertEquals(java.util.Set.of("Person"), person.getLabelSet()),
            () -> assertEquals(7, person.getDegree())
        );
    }

    @Test
    @DisplayName("关系实体：映射关系类型、起止节点与属性")
    void testMapOne_RelationshipEntityColumn_ShouldMapWithBothEndNodes() {
        final GraphNodeData start = GraphTestData.personNodeData("4:db:1", "P001", "小明");
        final GraphNodeData end = GraphTestData.personNodeData("4:db:2", "P002", "小红");
        final GraphRelationshipData relationshipData = GraphTestData.knowsRelationshipData("5:db:9", start, end);
        final GraphRecordData recordData = GraphTestData.singleColumnRecord("r", relationshipData);

        final GraphTestData.KnowsRelationship relationship =
            graphMapper.mapOne(recordData, GraphTestData.KnowsRelationship.class);

        assertNotNull(relationship);
        assertAll(
            () -> assertEquals("5:db:9", relationship.getElementId()),
            () -> assertEquals("KNOWS", relationship.getType()),
            () -> assertEquals(2019, relationship.getSinceYear()),
            () -> assertNotNull(relationship.getStartNode()),
            () -> assertEquals("小明", relationship.getStartNode().getName()),
            () -> assertNotNull(relationship.getEndNode()),
            () -> assertEquals("小红", relationship.getEndNode().getName())
        );
    }

    @Test
    @DisplayName("投影结果：按列名映射到非图实体，含类型转换")
    void testMapOne_ProjectionColumns_ShouldMapByColumnName() {
        final Map<String, Object> columns = new LinkedHashMap<>();
        columns.put("name", "小明");
        columns.put("total", 12);
        final GraphRecordData recordData = new GraphRecordData(columns);

        final GraphTestData.PersonSummary summary = graphMapper.mapOne(recordData, GraphTestData.PersonSummary.class);

        assertNotNull(summary);
        assertAll(
            () -> assertEquals("小明", summary.getName()),
            () -> assertEquals(12L, summary.getTotal())
        );
    }

    @Test
    @DisplayName("嵌套结构：投影实体中的节点字段与节点集合字段均递归映射")
    void testMapOne_NestedNodeAndCollection_ShouldMapRecursively() {
        final GraphNodeData person = GraphTestData.personNodeData("4:db:1", "P001", "小明");
        final GraphNodeData friendOne = GraphTestData.personNodeData("4:db:2", "P002", "小红");
        final GraphNodeData friendTwo = GraphTestData.personNodeData("4:db:3", "P003", "小刚");
        final Map<String, Object> columns = new LinkedHashMap<>();
        columns.put("p", person);
        columns.put("friends", List.of(friendOne, friendTwo));
        final GraphRecordData recordData = new GraphRecordData(columns);

        final GraphTestData.PersonWithFriends result =
            graphMapper.mapOne(recordData, GraphTestData.PersonWithFriends.class);

        assertNotNull(result);
        assertAll(
            () -> assertNotNull(result.getPerson()),
            () -> assertEquals("小明", result.getPerson().getName()),
            () -> assertNotNull(result.getFriendList()),
            () -> assertEquals(2, result.getFriendList().size()),
            () -> assertEquals("小红", result.getFriendList().get(0).getName()),
            () -> assertEquals("小刚", result.getFriendList().get(1).getName())
        );
    }

    @Test
    @DisplayName("列表映射：逐行映射，行数与结果数一致")
    void testMapList_MultipleRecords_ShouldMapEachRow() {
        final List<GraphRecordData> recordList = List.of(
            GraphTestData.singleColumnRecord("n", GraphTestData.personNodeData("4:db:1", "P001", "小明")),
            GraphTestData.singleColumnRecord("n", GraphTestData.personNodeData("4:db:2", "P002", "小红"))
        );

        final List<GraphTestData.PersonNode> personList =
            graphMapper.mapList(recordList, GraphTestData.PersonNode.class);

        assertAll(
            () -> assertEquals(2, personList.size()),
            () -> assertEquals("小明", personList.get(0).getName()),
            () -> assertEquals("小红", personList.get(1).getName())
        );
    }

    @Test
    @DisplayName("列表映射：空输入返回空列表而非 null")
    void testMapList_EmptyInput_ShouldReturnEmptyList() {
        assertAll(
            () -> assertTrue(graphMapper.mapList(null, GraphTestData.PersonNode.class).isEmpty()),
            () -> assertTrue(graphMapper.mapList(List.of(), GraphTestData.PersonNode.class).isEmpty())
        );
    }

    @Test
    @DisplayName("单值映射：首列标量按目标类型转换")
    void testMapObject_ScalarFirstColumn_ShouldConvertToTargetType() {
        final GraphRecordData recordData = GraphTestData.singleColumnRecord("count(n)", 42);

        assertAll(
            () -> assertEquals(42L, graphMapper.mapObject(recordData, Long.class)),
            () -> assertEquals("42", graphMapper.mapObject(recordData, String.class))
        );
    }

    @Test
    @DisplayName("单值映射：首列为节点时映射为节点实体")
    void testMapObject_NodeFirstColumn_ShouldMapToNodeEntity() {
        final GraphRecordData recordData =
            GraphTestData.singleColumnRecord("n", GraphTestData.personNodeData("4:db:1", "P001", "小明"));

        final GraphTestData.PersonNode person = graphMapper.mapObject(recordData, GraphTestData.PersonNode.class);

        assertNotNull(person);
        assertEquals("小明", person.getName());
    }

    @Test
    @DisplayName("空值处理：空结果行与 null 输入均返回 null")
    void testMapOneAndMapObject_NullOrEmptyRecord_ShouldReturnNull() {
        assertAll(
            () -> assertNull(graphMapper.mapOne(null, GraphTestData.PersonNode.class)),
            () -> assertNull(graphMapper.mapObject(null, Long.class)),
            () -> assertNull(graphMapper.mapObject(GraphRecordData.empty(), Long.class)),
            () -> assertNull(graphMapper.mapObject(GraphTestData.singleColumnRecord("v", null), Long.class))
        );
    }

    @Test
    @DisplayName("无法转换的属性值被跳过，不影响其余字段映射")
    void testMapOne_UnconvertibleProperty_ShouldSkipAndKeepOtherFields() {
        final Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("name", "小明");
        // age 无法转为 Integer，应跳过该字段而非整体失败
        properties.put("age", "不是数字");
        final GraphNodeData nodeData = new GraphNodeData()
            .setElementId("4:db:1")
            .setProperties(properties);
        final GraphRecordData recordData = GraphTestData.singleColumnRecord("n", nodeData);

        final GraphTestData.PersonNode person = graphMapper.mapOne(recordData, GraphTestData.PersonNode.class);

        assertNotNull(person);
        assertAll(
            () -> assertEquals("小明", person.getName()),
            () -> assertNull(person.getAge())
        );
    }
}
