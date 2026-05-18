package com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 聚合字段注解
 * <p>
 * 用于标记 Elasticsearch 聚合查询结果中需要映射到 Java 字段的属性。
 * 可以指定聚合返回字段类型、字段名称和嵌套聚合名称。
 * </p>
 * <p>使用示例：</p>
 * <pre>
 * public class AggResult {
 *     {@literal @}AggregationField(value = AggregationFieldEnum.SUM, fieldName = "total_amount")
 *     private Double totalAmount;
 *
 *     {@literal @}AggregationField(aggregationName = "nested_agg", fieldName = "count")
 *     private Long count;
 * }
 * </pre>
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregationField {

    /**
     * 聚合返回字段类型
     *
     * @return 聚合字段枚举，默认为 NULL
     */
    AggregationFieldEnum value() default AggregationFieldEnum.NULL;

    /**
     * 聚合返回字段名
     *
     * @return ES 中的字段名称
     */
    String fieldName() default "";

    /**
     * 嵌套聚合名称
     *
     * @return 嵌套聚合的名称
     */
    String aggregationName() default "";

}
