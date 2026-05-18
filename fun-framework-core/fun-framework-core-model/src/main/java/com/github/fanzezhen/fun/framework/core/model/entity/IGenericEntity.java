package com.github.fanzezhen.fun.framework.core.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通用实体类接口
 * <p>
 * 继承 IEntity，额外定义逻辑删除标识、状态、创建时间、创建人等通用字段的访问方法。
 * </p>
 *
 * @param <P> 主键类型
 */
public interface IGenericEntity<P extends Serializable> extends IEntity<P> {


    /**
     * 逻辑删除标识默认值（int 类型）
     */
    int DEFAULT_DEL_FLAG_INT = 0;

    /**
     * 逻辑删除标识默认值（long 类型）
     */
    long DEFAULT_DEL_FLAG_LONG = 0;

    /**
     * 逻辑删除标识默认值（String 类型）
     */
    String DEFAULT_DEL_FLAG_STR = "0";

    /**
     * 逻辑删除标识字段名
     */
    String FIELD_DEL_FLAG = "delFlag";

    /**
     * 状态字段名
     */
    String FIELD_STATUS = "status";

    /**
     * 创建人ID字段名
     */
    String FIELD_CREATE_USER_ID = "createUserId";

    /**
     * 更新人ID字段名
     */
    String FIELD_UPDATE_USER_ID = "updateUserId";

    /**
     * 创建时间字段名
     */
    String FIELD_CREATE_TIME = "createTime";

    /**
     * 更新时间字段名
     */
    String FIELD_UPDATE_TIME = "updateTime";

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    LocalDateTime getCreateTime();

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     * @return 当前对象，支持链式调用
     */
    IGenericEntity<P> setCreateTime(LocalDateTime createTime);

    /**
     * 获取创建人ID
     *
     * @return 创建人ID
     */
    P getCreateUserId();

    /**
     * 设置创建人ID
     *
     * @param createUserId 创建人ID
     * @return 当前对象，支持链式调用
     */
    IGenericEntity<P> setCreateUserId(P createUserId);
}
