package com.github.fanzezhen.fun.framework.data.graph.base.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 图查询结果行中间表示
 * <p>
 * 一行查询结果，键为 Cypher {@code RETURN} 子句的列名，值为归一化后的数据：
 * </p>
 * <ul>
 *   <li>节点 → {@link GraphNodeData}</li>
 *   <li>关系 → {@link GraphRelationshipData}</li>
 *   <li>列表 → {@link java.util.List}（元素同样归一化）</li>
 *   <li>映射 → {@link java.util.Map}</li>
 *   <li>其余 → JDK 标量类型</li>
 * </ul>
 * <p>
 * 该类是抽象层与驱动的唯一数据边界：starter 只需产出本类型，
 * 映射引擎与模板层不感知任何驱动类型。
 * </p>
 * <p>
 * 与 {@link GraphNodeData} 同理，不实现 {@link java.io.Serializable}：
 * 列值为图库返回的任意对象，无法保证可序列化。
 * </p>
 *
 * @param columns 列数据，键为列名
 * @since 4.1.1
 */
public record GraphRecordData(Map<String, Object> columns) {

    /**
     * 构造方法，columns 为 null 时归一化为空映射
     *
     * @param columns 列数据
     */
    public GraphRecordData(final Map<String, Object> columns) {
        this.columns = columns == null ? Collections.emptyMap() : columns;
    }

    /**
     * 创建空结果行
     *
     * @return 空结果行
     */
    public static GraphRecordData empty() {
        return new GraphRecordData(Collections.emptyMap());
    }

    /**
     * 按列名与列顺序构建结果行
     *
     * @param keys   列名列表
     * @param values 列值列表，长度不足时缺失列不写入
     * @return 结果行
     */
    public static GraphRecordData of(final List<String> keys, final List<Object> values) {
        if (keys == null || keys.isEmpty() || values == null) {
            return empty();
        }
        final Map<String, Object> columnMap = LinkedHashMap.newLinkedHashMap(keys.size());
        final int size = Math.min(keys.size(), values.size());
        for (int index = 0; index < size; index++) {
            columnMap.put(keys.get(index), values.get(index));
        }
        return new GraphRecordData(columnMap);
    }

    /**
     * 获取指定列的值
     *
     * @param column 列名
     * @return 列值，不存在返回 null
     */
    public Object get(final String column) {
        return columns.get(column);
    }

    /**
     * 判断是否存在指定列
     *
     * @param column 列名
     * @return true 表示存在
     */
    public boolean contains(final String column) {
        return columns.containsKey(column);
    }

    /**
     * 获取第一列的值
     * <p>
     * 用于 {@code RETURN count(n)} 这类单列查询，避免调用方硬编码列名。
     * </p>
     *
     * @return 第一列的值，无列时返回 null
     */
    public Object firstValue() {
        return columns.isEmpty() ? null : columns.values().iterator().next();
    }

    /**
     * 判断结果行是否为空
     *
     * @return true 表示无任何列
     */
    public boolean isEmpty() {
        return columns.isEmpty();
    }
}
