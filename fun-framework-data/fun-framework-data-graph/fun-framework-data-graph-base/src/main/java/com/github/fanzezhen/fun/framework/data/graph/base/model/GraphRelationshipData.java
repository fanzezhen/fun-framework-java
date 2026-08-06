package com.github.fanzezhen.fun.framework.data.graph.base.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 图关系中间表示
 * <p>
 * 驱动无关的关系数据载体，与 {@link GraphNodeData} 同为映射引擎的输入类型。
 * 起止节点以标识引用，完整节点数据由查询结果中的节点集合按标识关联。
 * </p>
 * <p>
 * 与 {@link GraphNodeData} 同理，不实现 {@link java.io.Serializable}。
 * </p>
 *
 * @since 4.1.1
 */
@Data
@Accessors(chain = true)
public class GraphRelationshipData {

    /**
     * 图库内部标识
     */
    private String elementId;

    /**
     * 关系类型
     */
    private String type;

    /**
     * 起始节点标识
     */
    private String startNodeElementId;

    /**
     * 结束节点标识
     */
    private String endNodeElementId;

    /**
     * 起始节点数据
     * <p>
     * 查询结果中携带节点数据时填充，仅返回关系本身时为 null。
     * </p>
     */
    private GraphNodeData startNode;

    /**
     * 结束节点数据
     */
    private GraphNodeData endNode;

    /**
     * 关系属性
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
     * 获取属性映射，永不返回 null
     *
     * @return 属性映射，未设置时返回空映射
     */
    public Map<String, Object> getPropertiesOrEmpty() {
        return properties == null ? Collections.emptyMap() : properties;
    }
}
