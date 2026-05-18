package com.github.fanzezhen.fun.framework.core.model.entity;

import java.io.Serializable;

/**
 * 实体类接口
 * <p>
 * 定义实体类的基本结构，包含主键的获取和设置方法。
 * </p>
 *
 * @param <P> 主键类型
 */
public interface IEntity<P extends Serializable> extends Serializable {

    /**
     * 获取主键
     *
     * @return 主键值
     */
    P getId();

    /**
     * 设置主键
     *
     * @param id 主键值
     * @return 当前对象，支持链式调用
     */
    IEntity<P> setId(P id);
}
