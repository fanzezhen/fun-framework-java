package com.github.fanzezhen.fun.framework.core.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类接口
 *
 * @author fanzezhen
 */
public interface IBaseEntity<P extends Serializable> extends Serializable {
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
    IBaseEntity<P> setId(P id);

    /**
     * @return 创建时间
     */
    LocalDateTime getCreateTime();

    /**
     * @param createTime 创建时间
     */
    IBaseEntity<P> setCreateTime(LocalDateTime createTime);

    /**
     * @return 创建人ID
     */
    P getCreateUserId();

    /**
     * @param createUserId 创建人ID
     */
    IBaseEntity<P> setCreateUserId(P createUserId);
}
