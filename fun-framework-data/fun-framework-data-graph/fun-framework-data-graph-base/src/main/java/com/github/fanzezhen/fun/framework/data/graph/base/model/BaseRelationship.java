package com.github.fanzezhen.fun.framework.data.graph.base.model;

import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.EndNode;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphId;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphRelationship;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.GraphType;
import com.github.fanzezhen.fun.framework.data.graph.base.annotation.StartNode;
import lombok.Data;

/**
 * 图关系实体基类
 * <p>
 * 业务关系实体可继承该类复用标识、类型与起止节点字段。与 {@link BaseNode} 同理，
 * 映射引擎按注解工作，不强制继承。
 * </p>
 *
 * @since 4.1.1
 */
@Data
@GraphRelationship
public class BaseRelationship implements IEntity<String> {

    /**
     * 图库内部标识
     */
    @GraphId
    private String id;

    /**
     * 关系类型
     * <p>
     * 由图库返回值填充，不作为普通属性写入。
     * </p>
     */
    @GraphType
    private String type;

    /**
     * 起始节点
     */
    @StartNode
    private BaseNode startNode;

    /**
     * 结束节点
     */
    @EndNode
    private BaseNode endNode;

    @Override
    public IEntity<String> setId(final String id) {
        this.id = id;
        return this;
    }
}
