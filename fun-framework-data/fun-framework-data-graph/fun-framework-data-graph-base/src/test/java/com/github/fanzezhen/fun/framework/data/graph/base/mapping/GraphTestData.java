package com.github.fanzezhen.fun.framework.data.graph.base.mapping;

import com.github.fanzezhen.fun.framework.core.model.annotation.Column;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.EndNode;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphId;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphLabels;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphNode;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphRelationship;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphType;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.StartNode;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphNodeData;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRelationshipData;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 图映射测试数据工厂
 * <p>
 * 集中构造测试实体与中间表示，避免各测试方法重复搭建数据。
 * </p>
 */
final class GraphTestData {

    private GraphTestData() {
    }

    /**
     * 人物节点测试实体
     */
    @Data
    @GraphNode(label = "Person")
    static class PersonNode {

        /**
         * 图库内部标识
         */
        @GraphId
        private String elementId;

        /**
         * 业务主键，用框架统一的列注解声明
         */
        @Column(name = "person_id", isPrimaryKey = true)
        private String personId;

        /**
         * 姓名
         */
        private String name;

        /**
         * 年龄，验证字符串到数值的转换
         */
        private Integer age;

        /**
         * 别名列表，验证泛型集合转换
         */
        @Column(name = "alias_list")
        private List<String> aliasList;

        /**
         * 标签集合
         */
        @GraphLabels
        private Set<String> labelSet;

        /**
         * 图库侧计算得出的只读属性
         */
        @Column(name = "degree", writable = false)
        private Integer degree;
    }

    /**
     * 认识关系测试实体
     */
    @Data
    @GraphRelationship(type = "KNOWS")
    static class KnowsRelationship {

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
        private PersonNode startNode;

        /**
         * 结束节点
         */
        @EndNode
        private PersonNode endNode;

        /**
         * 认识年份
         */
        @Column(name = "since_year")
        private Integer sinceYear;
    }

    /**
     * 投影结果测试实体，不标注图注解
     */
    @Data
    static class PersonSummary {

        /**
         * 姓名
         */
        private String name;

        /**
         * 计数
         */
        private Long total;
    }

    /**
     * 含嵌套节点集合的投影实体
     */
    @Data
    static class PersonWithFriends {

        /**
         * 主体节点
         */
        @Column(name = "p")
        private PersonNode person;

        /**
         * 好友节点集合，验证集合内嵌节点映射
         */
        @Column(name = "friends")
        private List<PersonNode> friendList;
    }

    /**
     * 构造人物节点中间表示
     *
     * @param elementId 图库标识
     * @param personId  业务主键
     * @param name      姓名
     * @return 节点中间表示
     */
    static GraphNodeData personNodeData(final String elementId, final String personId, final String name) {
        final Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("person_id", personId);
        properties.put("name", name);
        // 字符串形态的年龄，验证映射引擎的类型转换
        properties.put("age", "30");
        properties.put("alias_list", List.of("小明", "明明"));
        properties.put("degree", 7);
        return new GraphNodeData()
            .setElementId(elementId)
            .setLabels(Set.of("Person"))
            .setProperties(properties);
    }

    /**
     * 构造认识关系中间表示
     *
     * @param elementId 图库标识
     * @param startNode 起始节点
     * @param endNode   结束节点
     * @return 关系中间表示
     */
    static GraphRelationshipData knowsRelationshipData(final String elementId,
                                                       final GraphNodeData startNode,
                                                       final GraphNodeData endNode) {
        final Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("since_year", 2019);
        return new GraphRelationshipData()
            .setElementId(elementId)
            .setType("KNOWS")
            .setStartNodeElementId(startNode == null ? null : startNode.getElementId())
            .setEndNodeElementId(endNode == null ? null : endNode.getElementId())
            .setStartNode(startNode)
            .setEndNode(endNode)
            .setProperties(properties);
    }

    /**
     * 构造单列结果行
     *
     * @param column 列名
     * @param value  列值
     * @return 结果行
     */
    static GraphRecordData singleColumnRecord(final String column, final Object value) {
        final Map<String, Object> columns = new LinkedHashMap<>();
        columns.put(column, value);
        return new GraphRecordData(columns);
    }
}
