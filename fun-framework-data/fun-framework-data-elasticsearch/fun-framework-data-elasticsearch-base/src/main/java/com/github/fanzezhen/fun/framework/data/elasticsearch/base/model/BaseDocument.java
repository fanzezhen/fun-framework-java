package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model;

import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Elasticsearch 文档基类
 * <p>
 * 提供 Elasticsearch 文档的基础模型，包含文档 ID 字段。
 * 所有 ES 文档实体类可以继承此类以获得基础的 ID 字段。
 * </p>
 */
@Data
@Accessors(chain = true)
public class BaseDocument implements IEntity<String> {

    /**
     * 文档主键 ID
     */
    private String id;

    /**
     * 构造方法
     *
     * @param id 文档 ID
     */
    public BaseDocument(final String id) {
        this.id = id;
    }
}
