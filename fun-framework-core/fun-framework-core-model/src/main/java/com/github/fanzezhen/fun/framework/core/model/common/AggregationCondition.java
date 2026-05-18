package com.github.fanzezhen.fun.framework.core.model.common;

import cn.hutool.core.lang.func.Func1;
import com.github.fanzezhen.fun.framework.core.model.template.ITemplate;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.swing.*;

/**
 * 聚合条件
 * <p>
 * 定义聚合查询的条件参数，包括字段名、排序方式和数量限制。
 * </p>
 */
@Data
@Accessors(chain = true)
public class AggregationCondition {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 排序方式
     */
    private SortOrder sortOrder;

    /**
     * 聚合结果数量限制，默认 10000
     */
    private Integer limit = 10000;

    /**
     * 通过函数式引用设置字段名
     * <p>
     * 使用 Lambda 表达式引用实体字段，自动解析为数据库列名
     * </p>
     *
     * @param column 字段引用函数
     * @param <T>    实体类型
     * @return 当前对象，支持链式调用
     */
    public <T> AggregationCondition setFieldName(Func1<T, ?> column) {
        this.fieldName = ITemplate.getColumnName(column);
        return this;
    }

    /**
     * 获取数值列名
     * <p>
     * 返回聚合统计结果中数值列的默认名称
     * </p>
     *
     * @return 数值列名
     */
    public String getNumberColumnName() {
        return "doc_count";
    }
}
