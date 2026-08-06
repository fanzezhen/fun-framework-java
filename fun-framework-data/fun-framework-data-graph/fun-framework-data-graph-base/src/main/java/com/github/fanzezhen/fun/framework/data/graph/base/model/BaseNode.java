package com.github.fanzezhen.fun.framework.data.graph.base.model;

import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphId;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphLabels;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphNode;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 图节点实体基类
 * <p>
 * 业务节点实体可继承该类复用标识与标签字段，也可不继承而仅用注解标注，
 * 映射引擎按注解工作，不要求继承关系。
 * </p>
 * <p>
 * {@code id} 为图库内部标识（Neo4j 的 elementId），由图库生成，写入时不带该字段；
 * 业务主键请在子类中用 {@code @GraphId(business = true)} 单独声明。
 * </p>
 *
 * @since 4.1.1
 */
@Data
@GraphNode
public class BaseNode implements IEntity<String> {

    /**
     * 图库内部标识
     */
    @GraphId
    private String id;

    /**
     * 节点标签集合
     */
    @GraphLabels
    private Set<String> labelSet = new LinkedHashSet<>();

    @Override
    public IEntity<String> setId(final String id) {
        this.id = id;
        return this;
    }
}
