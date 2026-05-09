package com.github.fanzezhen.fun.framework.core.model.common;

import cn.hutool.core.lang.func.Func1;
import com.github.fanzezhen.fun.framework.core.model.template.ITemplate;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.swing.*;

/**
 *
 */
@Data
@Accessors(chain = true)
public class AggregationCondition {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 排序
     */
    private SortOrder sortOrder;

    /**
     * 限量
     */
    private Integer limit = 10000;

    public <T> AggregationCondition setFieldName(Func1<T, ?> column) {
        this.fieldName = ITemplate.getColumnName(column);
        return this;
    }

    public String getNumberColumnName() {
        return "doc_count";
    }
}
