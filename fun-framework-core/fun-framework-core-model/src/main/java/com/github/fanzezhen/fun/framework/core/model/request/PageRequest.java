package com.github.fanzezhen.fun.framework.core.model.request;

import com.github.fanzezhen.fun.framework.core.model.common.IPage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 分页请求对象
 * <p>
 * 实现 IPage 接口，提供分页查询的请求参数，包含页码和每页显示条数的校验规则。
 * </p>
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
     *
     * @param current 页码
     * @return 当前对象，支持链式调用
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
     *
     * @param size 每页显示条数
     * @return 当前对象，支持链式调用
     */
    @Override
    public IPage setSize(int size) {
        this.size = size;
        return this;
    }
}
