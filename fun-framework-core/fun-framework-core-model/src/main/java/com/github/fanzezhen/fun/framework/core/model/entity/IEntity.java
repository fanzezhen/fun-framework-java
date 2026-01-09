package com.github.fanzezhen.fun.framework.core.model.entity;

import java.io.Serializable;

/**
 * 实体类接口
 *
 * @author fanzezhen
 */
public interface IEntity<P extends Serializable> extends Serializable {
    /**
     * 数据库逻辑删除字段的默认值
     */
    int DEFAULT_DEL_FLAG = 0;
    long DEFAULT_DEL_FLAG_LONG = 0;
    /**
     * 数据库逻辑删除字段的默认值
     */
    String DEFAULT_DEL_FLAG_STR = "0";

    /**
     * @return 主键
     */
    P getId();

    /**
     * @param id 主键
     */
    IEntity<P> setId(P id);
}
