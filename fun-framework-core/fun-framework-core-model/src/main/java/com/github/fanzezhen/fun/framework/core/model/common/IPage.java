package com.github.fanzezhen.fun.framework.core.model.common;

/**
 * 分页参数
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
     */
    IPage setSize(int size);

    /**
     * 当前页
     *
     * @return 当前页
     */
    int getCurrent();

    /**
     * 设置当前页
     */
    IPage setCurrent(int current);
}
