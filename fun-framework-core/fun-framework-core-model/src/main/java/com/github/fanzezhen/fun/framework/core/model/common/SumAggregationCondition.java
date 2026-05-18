package com.github.fanzezhen.fun.framework.core.model.common;

import cn.hutool.core.lang.func.Func1;
import com.github.fanzezhen.fun.framework.core.model.template.ITemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 求和聚合条件
 * <p>
 * 继承 AggregationCondition，额外提供求和字段配置，用于求和聚合查询。
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SumAggregationCondition extends AggregationCondition{

    /**
     * 求和字段名
     */
    private String sumFieldName;

    /**
     * 通过函数式引用设置求和字段名
     * <p>
     * 使用 Lambda 表达式引用实体字段，自动解析为数据库列名
     * </p>
     *
     * @param column 字段引用函数
     * @param <T>    实体类型
     * @return 当前对象，支持链式调用
     */
    public <T> SumAggregationCondition setSumFieldName(Func1<T, ?> column) {
        this.sumFieldName = ITemplate.getColumnName(column);
        return this;
    }

    /**
     * 获取数值列名
     * <p>
     * 返回求和聚合统计结果中数值列的名称
     * </p>
     *
     * @return 数值列名
     */
    @Override
    public String getNumberColumnName() {
        return "doc_sum";
    }
}
