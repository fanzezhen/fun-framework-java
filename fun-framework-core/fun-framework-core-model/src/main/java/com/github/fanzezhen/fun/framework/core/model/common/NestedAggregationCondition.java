package com.github.fanzezhen.fun.framework.core.model.common;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import com.github.fanzezhen.fun.framework.core.model.template.ITemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Collection;

/**
 * 嵌套聚合条件
 * <p>
 * 继承 AggregationCondition，额外提供嵌套聚合的排序、限量和字段过滤配置。
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class NestedAggregationCondition extends AggregationCondition{

    /**
     * 命中结果排序规则
     */
    private Order hitsOrder;

    /**
     * 命中结果数量限制，默认为 Short.MAX_VALUE
     */
    private Integer hitsLimit = (int) Short.MAX_VALUE;

    /**
     * 命中结果需要包含的字段集合
     */
    private Collection<String> hitSourceIncludes;

    /**
     * 命中结果需要排除的字段集合
     */
    private Collection<String> hitSourceExcludes;

    /**
     * 通过函数式引用设置命中结果排序规则
     *
     * @param direction 排序方向
     * @param column    字段引用函数
     * @param <T>       实体类型
     * @return 当前对象，支持链式调用
     */
    public <T> NestedAggregationCondition setHitsOrder(Direction direction, Func1<T, ?> column) {
        this.hitsOrder = new Order(ITemplate.getColumnName(column), direction);
        return this;
    }

    /**
     * 判断是否需要过滤命中结果的字段
     *
     * @return true 表示需要过滤，false 表示不需要过滤
     */
    public boolean needFilterHitSource() {
        return hitSourceIncludes!=null||hitSourceExcludes != null;
    }
}
