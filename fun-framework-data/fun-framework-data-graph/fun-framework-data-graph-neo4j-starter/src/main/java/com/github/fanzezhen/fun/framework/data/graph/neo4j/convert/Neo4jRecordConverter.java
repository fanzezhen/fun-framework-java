package com.github.fanzezhen.fun.framework.data.graph.neo4j.convert;

import com.github.fanzezhen.fun.framework.data.graph.base.constant.FunGraphConstant;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphNodeData;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRecordData;
import com.github.fanzezhen.fun.framework.data.graph.base.model.GraphRelationshipData;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.IsoDuration;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Point;
import org.neo4j.driver.types.Relationship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Neo4j 结果转换器
 * <p>
 * 把驱动的 {@link Record} 与 {@link Value} 归一化为驱动无关的中间表示。
 * 这是 Neo4j starter 与抽象层之间的唯一转换点：转换之后再无任何驱动类型，
 * 因此映射引擎与模板层不必感知 Neo4j。
 * </p>
 * <p>
 * 驱动的 {@link Relationship} 只携带两端节点的标识而不含节点数据，
 * 故转换时会在同一行结果内建立标识到节点的索引，回填关系两端的节点数据，
 * 使 {@code MATCH (a)-[r]->(b) RETURN a, r, b} 的关系能直接取到起止节点。
 * </p>
 *
 * @since 4.1.1
 */
public final class Neo4jRecordConverter {

    private Neo4jRecordConverter() {
    }

    /**
     * 转换单行结果
     *
     * @param driverRecord 驱动结果行
     * @return 归一化结果行
     */
    public static GraphRecordData toRecordData(final Record driverRecord) {
        if (driverRecord == null) {
            return GraphRecordData.empty();
        }
        final Map<String, Object> columnMap = LinkedHashMap.newLinkedHashMap(driverRecord.keys().size());
        for (final String key : driverRecord.keys()) {
            columnMap.put(key, toObject(driverRecord.get(key)));
        }
        linkRelationshipEndpoints(columnMap);
        return new GraphRecordData(columnMap);
    }

    /**
     * 批量转换结果行
     *
     * @param recordList 驱动结果行列表
     * @return 归一化结果行列表
     */
    public static List<GraphRecordData> toRecordDataList(final List<Record> recordList) {
        if (recordList == null || recordList.isEmpty()) {
            return List.of();
        }
        final List<GraphRecordData> resultList = new ArrayList<>(recordList.size());
        for (final Record driverRecord : recordList) {
            resultList.add(toRecordData(driverRecord));
        }
        return resultList;
    }

    /**
     * 转换驱动值为归一化对象
     * <p>
     * 节点、关系、路径、空间点、时长这些驱动专有类型全部转为中间表示或 JDK 类型；
     * 其余（数值、字符串、布尔、时间、列表、映射）本就是 JDK 类型，原样保留。
     * </p>
     *
     * @param value 驱动值
     * @return 归一化对象，空值返回 null
     */
    public static Object toObject(final Value value) {
        if (value == null || value.isNull()) {
            return null;
        }
        return normalize(value.asObject());
    }

    /**
     * 归一化驱动对象
     *
     * @param object 驱动对象
     * @return 归一化对象
     */
    private static Object normalize(final Object object) {
        if (object instanceof Node node) {
            return toNodeData(node);
        }
        if (object instanceof Relationship relationship) {
            return toRelationshipData(relationship);
        }
        if (object instanceof Path path) {
            return toPathMap(path);
        }
        if (object instanceof Point point) {
            return toPointMap(point);
        }
        if (object instanceof IsoDuration duration) {
            return toDurationMap(duration);
        }
        if (object instanceof Collection<?> collection) {
            return normalizeCollection(collection);
        }
        if (object instanceof Map<?, ?> map) {
            return normalizeMap(map);
        }
        return object;
    }

    /**
     * 转换节点
     *
     * @param node 驱动节点
     * @return 节点中间表示
     */
    public static GraphNodeData toNodeData(final Node node) {
        if (node == null) {
            return null;
        }
        final Set<String> labelSet = new LinkedHashSet<>();
        node.labels().forEach(labelSet::add);
        return new GraphNodeData()
            .setElementId(node.elementId())
            .setLabels(labelSet)
            .setProperties(normalizeMap(node.asMap()));
    }

    /**
     * 转换关系
     * <p>
     * 起止节点数据留空，由 {@link #linkRelationshipEndpoints} 在行内回填。
     * </p>
     *
     * @param relationship 驱动关系
     * @return 关系中间表示
     */
    public static GraphRelationshipData toRelationshipData(final Relationship relationship) {
        if (relationship == null) {
            return null;
        }
        return new GraphRelationshipData()
            .setElementId(relationship.elementId())
            .setType(relationship.type())
            .setStartNodeElementId(relationship.startNodeElementId())
            .setEndNodeElementId(relationship.endNodeElementId())
            .setProperties(normalizeMap(relationship.asMap()));
    }

    /**
     * 转换路径为节点与关系列表的映射
     *
     * @param path 驱动路径
     * @return 含 nodes 与 relationships 两个键的映射
     */
    private static Map<String, Object> toPathMap(final Path path) {
        final List<GraphNodeData> nodeList = new ArrayList<>();
        path.nodes().forEach(node -> nodeList.add(toNodeData(node)));
        final List<GraphRelationshipData> relationshipList = new ArrayList<>();
        path.relationships().forEach(relationship -> relationshipList.add(toRelationshipData(relationship)));
        linkEndpoints(relationshipList, indexByElementId(nodeList));
        final Map<String, Object> pathMap = LinkedHashMap.newLinkedHashMap(2);
        pathMap.put(FunGraphConstant.PATH_NODES, nodeList);
        pathMap.put(FunGraphConstant.PATH_RELATIONSHIPS, relationshipList);
        return pathMap;
    }

    /**
     * 转换空间点为映射
     *
     * @param point 驱动空间点
     * @return 含坐标系与坐标的映射
     */
    private static Map<String, Object> toPointMap(final Point point) {
        final Map<String, Object> pointMap = LinkedHashMap.newLinkedHashMap(4);
        pointMap.put(FunGraphConstant.POINT_SRID, point.srid());
        pointMap.put(FunGraphConstant.POINT_X, point.x());
        pointMap.put(FunGraphConstant.POINT_Y, point.y());
        pointMap.put(FunGraphConstant.POINT_Z, point.z());
        return pointMap;
    }

    /**
     * 转换时长为映射
     *
     * @param duration 驱动时长
     * @return 含月、天、秒、纳秒的映射
     */
    private static Map<String, Object> toDurationMap(final IsoDuration duration) {
        final Map<String, Object> durationMap = LinkedHashMap.newLinkedHashMap(4);
        durationMap.put(FunGraphConstant.DURATION_MONTHS, duration.months());
        durationMap.put(FunGraphConstant.DURATION_DAYS, duration.days());
        durationMap.put(FunGraphConstant.DURATION_SECONDS, duration.seconds());
        durationMap.put(FunGraphConstant.DURATION_NANOSECONDS, duration.nanoseconds());
        return durationMap;
    }

    /**
     * 逐元素归一化集合
     *
     * @param collection 原集合
     * @return 归一化列表
     */
    private static List<Object> normalizeCollection(final Collection<?> collection) {
        final List<Object> resultList = new ArrayList<>(collection.size());
        for (final Object element : collection) {
            resultList.add(normalize(element));
        }
        return resultList;
    }

    /**
     * 逐值归一化映射
     *
     * @param map 原映射
     * @return 归一化映射
     */
    private static Map<String, Object> normalizeMap(final Map<?, ?> map) {
        final Map<String, Object> resultMap = LinkedHashMap.newLinkedHashMap(map.size());
        map.forEach((key, value) -> resultMap.put(String.valueOf(key), normalize(value)));
        return resultMap;
    }

    /**
     * 在行内回填关系两端的节点数据
     *
     * @param columnMap 列数据映射
     */
    private static void linkRelationshipEndpoints(final Map<String, Object> columnMap) {
        final List<GraphNodeData> nodeList = new ArrayList<>();
        final List<GraphRelationshipData> relationshipList = new ArrayList<>();
        collectElements(columnMap.values(), nodeList, relationshipList);
        if (nodeList.isEmpty() || relationshipList.isEmpty()) {
            return;
        }
        linkEndpoints(relationshipList, indexByElementId(nodeList));
    }

    /**
     * 递归收集列数据中的节点与关系
     *
     * @param values           待扫描的值集合
     * @param nodeList         收集到的节点
     * @param relationshipList 收集到的关系
     */
    private static void collectElements(final Collection<?> values,
                                        final List<GraphNodeData> nodeList,
                                        final List<GraphRelationshipData> relationshipList) {
        for (final Object value : values) {
            if (value instanceof GraphNodeData nodeData) {
                nodeList.add(nodeData);
            } else if (value instanceof GraphRelationshipData relationshipData) {
                relationshipList.add(relationshipData);
            } else if (value instanceof Collection<?> collection) {
                collectElements(collection, nodeList, relationshipList);
            } else if (value instanceof Map<?, ?> map) {
                collectElements(map.values(), nodeList, relationshipList);
            }
        }
    }

    /**
     * 按图库标识索引节点
     *
     * @param nodeList 节点列表
     * @return 标识到节点的映射
     */
    private static Map<String, GraphNodeData> indexByElementId(final List<GraphNodeData> nodeList) {
        final Map<String, GraphNodeData> nodeMap = LinkedHashMap.newLinkedHashMap(nodeList.size());
        for (final GraphNodeData nodeData : nodeList) {
            if (nodeData != null && nodeData.getElementId() != null) {
                nodeMap.putIfAbsent(nodeData.getElementId(), nodeData);
            }
        }
        return nodeMap;
    }

    /**
     * 按标识索引回填关系两端节点
     *
     * @param relationshipList 关系列表
     * @param nodeMap          标识到节点的映射
     */
    private static void linkEndpoints(final List<GraphRelationshipData> relationshipList,
                                      final Map<String, GraphNodeData> nodeMap) {
        for (final GraphRelationshipData relationshipData : relationshipList) {
            if (relationshipData == null) {
                continue;
            }
            if (relationshipData.getStartNode() == null) {
                relationshipData.setStartNode(nodeMap.get(relationshipData.getStartNodeElementId()));
            }
            if (relationshipData.getEndNode() == null) {
                relationshipData.setEndNode(nodeMap.get(relationshipData.getEndNodeElementId()));
            }
        }
    }
}
