package com.github.fanzezhen.fun.framework.data.graph.base.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 图节点中间表示
 * <p>
 * 驱动无关的节点数据载体。各图数据库 starter 负责把驱动返回的节点对象归一化为该类型，
 * 映射引擎只认这套中间表示，因此新增图数据库实现无需改动映射逻辑。
 * </p>
 * <p>
 * 不实现 {@link java.io.Serializable}：本类是驱动与映射引擎之间的进程内传输载体，
 * 属性值为图库返回的任意对象，无法保证可序列化。需要跨进程传递请先映射为业务实体。
 * </p>
 *
 * @since 4.1.1
 */
@Data
@Accessors(chain = true)
public class GraphNodeData {

    /**
     * 图库内部标识
     * <p>
     * 对应 Neo4j 的 {@code elementId}。图库生成，跨库唯一性由图库保证。
     * </p>
     */
    private String elementId;

    /**
     * 节点标签集合
     */
    private Set<String> labels = new LinkedHashSet<>();

    /**
     * 节点属性
     */
    private Map<String, Object> properties = new LinkedHashMap<>();

    /**
     * 获取指定属性值
     *
     * @param name 属性名
     * @return 属性值，不存在返回 null
     */
    public Object getProperty(final String name) {
        return properties == null ? null : properties.get(name);
    }

    /**
     * 获取标签集合，永不返回 null
     *
     * @return 标签集合，未设置时返回空集合
     */
    public Set<String> getLabelsOrEmpty() {
        return labels == null ? Collections.emptySet() : labels;
    }

    /**
     * 获取属性映射，永不返回 null
     *
     * @return 属性映射，未设置时返回空映射
     */
    public Map<String, Object> getPropertiesOrEmpty() {
        return properties == null ? Collections.emptyMap() : properties;
    }
}
