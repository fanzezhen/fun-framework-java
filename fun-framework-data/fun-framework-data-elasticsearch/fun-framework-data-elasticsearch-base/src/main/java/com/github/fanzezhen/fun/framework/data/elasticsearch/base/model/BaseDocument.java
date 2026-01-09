package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model;

import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 基础模型
 */
@Data
@Accessors(chain = true)
public class BaseDocument implements IEntity<String> {

    /**
     * 主键
     */
    private String id;

    public BaseDocument(String id) {
        this.id = id;
    }
}
