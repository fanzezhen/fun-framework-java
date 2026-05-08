package com.github.fanzezhen.fun.framework.core.model.dto;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.fanzezhen.fun.framework.core.model.common.IHolder;
import com.github.fanzezhen.fun.framework.core.model.common.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

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
public class PageDTO<T> implements IPage, IHolder {
    static final PageDTO<?> EMPTY = new PageDTO<>();

    /**
     * 当前页
     */
    private int current;

    /**
     * 页大小
     */
    private int size;

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
    private List<T> records;

    /**
     * 获取下标号
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public int getCurrentPageIndex() {
        return Math.max((this.current - 1) * this.size, 0);
    }

    /**
     * 获取总页数
     */
    public Long getPageCount() {
        long mod = this.getTotalOrZero() % this.size;
        if (mod == 0) {
            return this.getTotalOrZero() / this.size;
        }
        return this.getTotalOrZero() / this.size + 1;
    }

    public PageDTO(int current, int size) {
        this.current = current;
        this.size = size;
    }

    public PageDTO(int current, int size, Long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
    }

    public Long getTotalOrZero() {
        return total == null ? 0L : total;
    }

    public List<T> getRecordsOrEmpty() {
        return records != null ? records : Collections.emptyList();
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public List<T> getRowList() {
        return records;
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    @JSONField(serialize = false)
    public static <T> PageDTO<T> empty() {
        return (PageDTO<T>) EMPTY;
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
     *
     * @return 转换后的分页结果，如果当前 rowList 为空则返回空列表的分页结果
     */
    @SuppressWarnings("java:S6204") // 需要可变列表以支持后续修改操作
    @JsonIgnore
    @JSONField(serialize = false)
    public <R> PageDTO<R> convert(Function<T, R> converter) {
        PageDTO<R> result = new PageDTO<>();
        result.setCurrent(this.current);
        result.setSize(this.size);
        result.setTotal(this.total);
        result.setTotalTime(this.totalTime);

        if (this.records != null && !this.records.isEmpty()) {
            result.setRecords(this.records.stream()
                .map(converter)
                .collect(Collectors.toList()));
        } else {
            result.setRecords(Collections.emptyList());
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
     * @param sourceClass  源类型（用于类型推断）
     * @param targetClass  目标类型
     * @param mapperFacade 对象映射器（如 Orika MapperFacade）
     * @param <R>          目标类型
     *
     * @return 转换后的分页结果
     */
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    public <R> PageDTO<R> convert(Class<T> sourceClass, Class<R> targetClass, MapperFacade mapperFacade) {
        Type<PageDTO> from = TypeFactory.valueOf(PageDTO.class, sourceClass);
        Type<PageDTO> to = TypeFactory.valueOf(PageDTO.class, targetClass);
        return mapperFacade.map(this, from, to);
    }

    @Override
    public boolean isEmpty() {
        return CollUtil.isEmpty(getRecords());
    }
}
