package com.github.fanzezhen.fun.framework.core.model.entity;

import java.io.Serializable;

/**
 * 实体类接口
 *
 */
public interface IEntity<P extends Serializable> extends Serializable {

    /**
     * @return 主键
     */
    P getId();

    /**
     * @param id 主键
     */
    IEntity<P> setId(P id);
}
