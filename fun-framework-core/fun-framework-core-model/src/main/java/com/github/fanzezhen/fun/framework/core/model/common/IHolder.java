package com.github.fanzezhen.fun.framework.core.model.common;

/**
 * 载具接口
 * <p>
 * 定义数据载体的基本行为，用于判断载体是否为空。
 * </p>
 */
public interface IHolder {
    /**
     * 判断载体是否为空
     * <p>
     * 用于检测数据载体（如集合、分页结果等）是否不包含有效数据
     * </p>
     *
     * @return true 表示载体为空，false 表示载体包含数据
     */
    boolean isEmpty();
}
