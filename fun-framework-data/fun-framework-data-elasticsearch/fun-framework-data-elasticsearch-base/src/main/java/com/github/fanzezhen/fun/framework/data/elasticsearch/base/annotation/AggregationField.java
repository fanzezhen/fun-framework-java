package com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 聚合标记
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregationField {

    /**
     * 聚合返回字段
     */
    AggregationFieldEnum value() default AggregationFieldEnum.NULL;

    /**
     * 聚合返回字段名
     */
    String fieldName() default "";

    /**
     * 嵌套聚合名
     */
    String aggregationName() default "";

}
