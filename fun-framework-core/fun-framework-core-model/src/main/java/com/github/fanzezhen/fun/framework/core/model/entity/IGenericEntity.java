package com.github.fanzezhen.fun.framework.core.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类接口
 *
 * @author fanzezhen
 */
public interface IGenericEntity<P extends Serializable> extends IEntity<P> {

    /**
     * @return 创建时间
     */
    LocalDateTime getCreateTime();

    /**
     * @param createTime 创建时间
     */
    IGenericEntity<P> setCreateTime(LocalDateTime createTime);

    /**
     * @return 创建人ID
     */
    P getCreateUserId();

    /**
     * @param createUserId 创建人ID
     */
    IGenericEntity<P> setCreateUserId(P createUserId);
}
