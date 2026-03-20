package com.github.fanzezhen.fun.framework.core.data.model;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import com.github.fanzezhen.fun.framework.core.data.template.ITemplate;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collection;

/**
 *
 */
@Data
@Accessors(chain = true)
public class NestedAggregationCondition extends AggregationCondition{

    /**
     * 字段名
     */
    private Order hitsOrder;

    /**
     * 限量
     */
    private Integer hitsLimit = (int) Short.MAX_VALUE;

    /**
     * 返回的字段集合
     */
    private Collection<String> hitSourceIncludes;
    /**
     * 排除的字段集合
     */
    private Collection<String> hitSourceExcludes;

    public <T> NestedAggregationCondition setHitsOrder(Direction direction, Func1<T, ?> column) {
        this.hitsOrder = new Order(ITemplate.getColumnName(column), direction);
        return this;
    }

    public boolean needFilterHitSource() {
        return hitSourceIncludes!=null||hitSourceExcludes != null;
    }
}
