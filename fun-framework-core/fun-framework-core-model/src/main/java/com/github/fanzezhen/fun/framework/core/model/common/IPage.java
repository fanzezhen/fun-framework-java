package com.github.fanzezhen.fun.framework.core.model.common;

/**
 * 分页参数接口
 * <p>
 * 定义分页查询的基本参数，包括当前页和每页显示条数。
 * </p>
 */
public interface IPage {

    /**
     * 获取每页显示条数
     *
     * @return 每页显示条数
     */
    int getSize();

    /**
     * 设置每页显示条数
     *
     * @param size 每页显示条数
     * @return 当前对象，支持链式调用
     */
    IPage setSize(int size);

    /**
     * 获取当前页码
     *
     * @return 当前页码
     */
    int getCurrent();

    /**
     * 设置当前页码
     *
     * @param current 当前页码
     * @return 当前对象，支持链式调用
     */
    IPage setCurrent(int current);
}
