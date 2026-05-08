package com.github.fanzezhen.fun.framework.core.model.request;

import com.github.fanzezhen.fun.framework.core.model.common.IPage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 *
 */
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest implements IPage {

    /**
     * 页码
     */
    @NotNull(message = "页码 不能为空")
    protected Integer current;

    /**
     * 每页显示条数
     */
    @NotNull(message = "每页显示条数 不能为空")
    protected Integer size;

    /**
     * 页码，默认 1
     *
     * @return 页码
     */
    @Override
    public int getCurrent() {
        return current != null ? current : 1;
    }

    /**
     * 设置页码
     */
    @Override
    public IPage setCurrent(int current) {
        this.current = current;
        return this;
    }

    /**
     * 获取每页显示条数，默认 10
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
