package com.github.fanzezhen.fun.framework.core.data.model;

import cn.hutool.core.lang.func.Func1;
import com.github.fanzezhen.fun.framework.core.data.template.ITemplate;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 */
@Data
@Accessors(chain = true)
public class SumAggregationCondition extends AggregationCondition{

    /**
     * 字段名
     */
    private String sumFieldName;

    public <T> SumAggregationCondition setSumFieldName(Func1<T, ?> column) {
        this.sumFieldName = ITemplate.getColumnName(column);
        return this;
    }

    @Override
    public String getNumberColumnName() {
        return "doc_sum";
    }
}
