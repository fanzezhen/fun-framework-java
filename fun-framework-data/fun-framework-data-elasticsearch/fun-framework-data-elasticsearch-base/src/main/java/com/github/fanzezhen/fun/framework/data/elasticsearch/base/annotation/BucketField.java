package com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation;


import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.BucketFieldEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 桶字段注解
 * <p>
 * 用于标记 Elasticsearch 桶聚合（Bucket Aggregation）结果中需要映射到 Java 字段的属性。
 * 可以指定桶返回字段类型、嵌套聚合名称和桶键值。
 * </p>
 * <p>使用示例：</p>
 * <pre>
 * public class CategoryBucket {
 *     {@literal @}BucketField(value = BucketFieldEnum.KEY)
 *     private String category;
 *
 *     {@literal @}BucketField(value = BucketFieldEnum.DOC_COUNT)
 *     private Long count;
 *
 *     {@literal @}BucketField(aggregationName = "total_sales", value = BucketFieldEnum.SUM)
 *     private Double totalSales;
 * }
 * </pre>
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BucketField {

    /**
     * 桶返回字段类型
     *
     * @return 桶字段枚举，默认为 NULL
     */
    BucketFieldEnum value() default BucketFieldEnum.NULL;

    /**
     * 嵌套聚合名称
     *
     * @return 嵌套聚合的名称
     */
    String aggregationName() default "";

    /**
     * 桶键值
     *
     * @return 桶的键值
     */
    String bucketKey() default "";

}
