package com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation;


import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.BucketFieldEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BucketField {

    /**
     * Bucket返回字段
     */
    BucketFieldEnum value() default BucketFieldEnum.NULL;

    /**
     * 嵌套聚合名
     */
    String aggregationName() default "";

    /**
     * bucket key
     */
    String bucketKey() default "";

}
