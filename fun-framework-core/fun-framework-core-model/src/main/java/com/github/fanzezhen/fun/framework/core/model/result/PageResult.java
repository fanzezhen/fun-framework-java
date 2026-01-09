package com.github.fanzezhen.fun.framework.core.model.result;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * 分页查询结果
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    static final PageResult<?> EMPTY = new PageResult<>();

    /**
     * 当前页
     */
    private Long currentPage;

    /**
     * 页大小
     */
    private Long pageSize;

    /**
     * 总记录数量
     */
    private Long total;

    /**
     * 总耗时
     */
    private Double totalTime;

    /**
     * 行记录
     */
    private List<T> rowList = Collections.emptyList();

    /**
     * 获取下标号
     */
    @JSONField(serialize = false)
    public Long getCurrentPageIndex() {
        return (this.getCurrentPage() - 1) * this.getPageSize() < 0 ? 0 : (this.getCurrentPage() - 1) * this.getPageSize();
    }

    /**
     * 获取总页数
     */
    public Long getPageCount() {
        long mod = this.total % this.pageSize;
        if (mod == 0) {
            return this.total / this.pageSize;
        }
        return this.total / this.pageSize + 1;
    }

    public PageResult(Long currentPage, Long pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public PageResult(Long currentPage, Long pageSize, Long total, List<T> rowList) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.total = total;
        this.rowList = rowList;
    }

    public Long getCurrentPage() {
        return currentPage == null ? 1L : currentPage;
    }

    public Long getPageSize() {
        return pageSize == null ? 10L : pageSize;
    }

    public Long getTotal() {
        return total == null ? 0L : total;
    }

    public List<T> getRowListOrEmpty() {
        return rowList != null ? rowList : Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public static <T> PageResult<T> empty() {
        return (PageResult<T>) EMPTY;
    }
}
