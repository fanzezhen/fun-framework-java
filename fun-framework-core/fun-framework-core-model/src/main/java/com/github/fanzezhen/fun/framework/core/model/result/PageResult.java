package com.github.fanzezhen.fun.framework.core.model.result;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    /**
     * 转换分页数据中的行记录类型
     * <p>
     * 使用提供的转换函数将当前分页结果中的 rowList 从类型 T 转换为类型 R，
     * 保持分页元数据（currentPage、pageSize、total、totalTime）不变。
     * </p>
     *
     * @param converter 行记录转换函数
     * @param <R>       目标类型
     * @return 转换后的分页结果，如果当前 rowList 为空则返回空列表的分页结果
     */
    @SuppressWarnings("java:S6204") // 需要可变列表以支持后续修改操作
    public <R> PageResult<R> convert(Function<T, R> converter) {
        PageResult<R> result = new PageResult<>();
        result.setCurrentPage(this.currentPage);
        result.setPageSize(this.pageSize);
        result.setTotal(this.total);
        result.setTotalTime(this.totalTime);

        if (this.rowList != null && !this.rowList.isEmpty()) {
            result.setRowList(this.rowList.stream()
                    .map(converter)
                    .collect(Collectors.toList()));
        } else {
            result.setRowList(Collections.emptyList());
        }

        return result;
    }

    /**
     * 使用对象映射器转换分页数据（用于与 Orika MapperFacade 集成）
     * <p>
     * 此方法设计用于与 Orika MapperFacade 集成，通过提供源类型和目标类型信息进行批量对象转换。
     * sourceClass 参数用于类型推断和编译时类型安全，但在运行时不参与实际转换。
     * </p>
     *
     * @param sourceClass 源类型（用于类型推断）
     * @param targetClass 目标类型
     * @param mapper      对象映射器（如 Orika MapperFacade）
     * @param <R>         目标类型
     * @return 转换后的分页结果
     */
    public <R> PageResult<R> convert(Class<T> sourceClass, Class<R> targetClass, Object mapper) {
        PageResult<R> result = new PageResult<>();
        result.setCurrentPage(this.currentPage);
        result.setPageSize(this.pageSize);
        result.setTotal(this.total);
        result.setTotalTime(this.totalTime);

        if (this.rowList != null && !this.rowList.isEmpty()) {
            // 使用反射调用 mapper 的 mapAsList 方法
            try {
                @SuppressWarnings("unchecked")
                List<R> convertedList = (List<R>) mapper.getClass()
                        .getMethod("mapAsList", Iterable.class, Class.class)
                        .invoke(mapper, this.rowList, targetClass);
                result.setRowList(convertedList);
            } catch (Exception e) {
                throw new IllegalArgumentException("对象映射失败: " + e.getMessage(), e);
            }
        } else {
            result.setRowList(Collections.emptyList());
        }

        return result;
    }
}
