package com.github.fanzezhen.fun.framework.core.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类接口
 *
 */
public interface IGenericEntity<P extends Serializable> extends IEntity<P> {


    /**
     * 数据库逻辑删除字段的默认值
     */
    int DEFAULT_DEL_FLAG_INT = 0;
    long DEFAULT_DEL_FLAG_LONG = 0;
    /**
     * 数据库逻辑删除字段的默认值
     */
    String DEFAULT_DEL_FLAG_STR = "0";
    String FIELD_DEL_FLAG = "delFlag";
    String FIELD_STATUS = "status";
    String FIELD_CREATE_USER_ID = "createUserId";
    String FIELD_UPDATE_USER_ID = "updateUserId";
    String FIELD_CREATE_TIME = "createTime";
    String FIELD_UPDATE_TIME = "updateTime";

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
