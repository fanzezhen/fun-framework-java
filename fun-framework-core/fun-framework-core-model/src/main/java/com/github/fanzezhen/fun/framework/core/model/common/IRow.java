package com.github.fanzezhen.fun.framework.core.model.common;

import java.io.Serializable;

/**
 * 一行数据
 */
public interface IRow {
    /**
     * 主键值
     */
    Serializable getId();

    /**
     * Source内容
     */
    Object getColumnSourceValue(String column);
}
