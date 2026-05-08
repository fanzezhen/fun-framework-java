package com.github.fanzezhen.fun.framework.core.model.common;

import lombok.AllArgsConstructor;

/**
 *
 */
@AllArgsConstructor
public class Page implements IPage {

    /**
     * 当前页，默认 1
     */
    private Integer current;

    /**
     * 每页显示条数，默认 10
     */
    private Integer size;

    /**
     * 当前页
     *
     * @return 当前页
     */
    @Override
    public int getCurrent() {
        return current != null ? current : 1;
    }

    /**
     * 设置当前页
     */
    @Override
    public IPage setCurrent(int current) {
        this.current = current;
        return this;
    }

    /**
     * 获取每页显示条数
     *
     * @return 每页显示条数
     */
    @Override
    public int getSize() {
        return size != null ? size : 10;
    }

    /**
     * 设置每页显示条数
     */
    @Override
    public IPage setSize(int size) {
        this.size = size;
        return this;
    }
}
