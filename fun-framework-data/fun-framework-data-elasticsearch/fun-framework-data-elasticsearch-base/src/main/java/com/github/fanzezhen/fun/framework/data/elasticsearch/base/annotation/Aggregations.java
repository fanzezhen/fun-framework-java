package com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 聚合类标注注解
 * <p>
 * 用于标记一个类是聚合结果的容器类，表示该类的字段将从 Elasticsearch 聚合查询结果中映射。
 * 通常与 {@link AggregationField} 注解配合使用。
 * </p>
 * <p>使用示例：</p>
 * <pre>
 * {@literal @}Aggregations
 * public class OrderAggResult {
 *     {@literal @}AggregationField(value = AggregationFieldEnum.SUM, fieldName = "total_price")
 *     private Double totalPrice;
 *
 *     {@literal @}AggregationField(value = AggregationFieldEnum.COUNT)
 *     private Long orderCount;
 * }
 * </pre>
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aggregations {

}
