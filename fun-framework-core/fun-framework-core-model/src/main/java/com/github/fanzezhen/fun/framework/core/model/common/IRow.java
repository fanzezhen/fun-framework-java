package com.github.fanzezhen.fun.framework.core.model.common;

import java.io.Serializable;

/**
 * 一行数据接口
 * <p>
 * 定义数据行的基本结构，用于获取主键和字段值。
 * </p>
 */
public interface IRow {
    /**
     * 获取主键值
     *
     * @return 主键值
     */
    Serializable getId();

    /**
     * 获取指定列的源数据值
     * <p>
     * 返回数据行中指定列名对应的原始值
     * </p>
     *
     * @param column 列名
     * @return 列的源数据值
     */
    Object getColumnSourceValue(String column);
}
