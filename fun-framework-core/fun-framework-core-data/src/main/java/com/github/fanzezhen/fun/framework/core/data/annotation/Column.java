package com.github.fanzezhen.fun.framework.core.data.annotation;

import com.github.fanzezhen.fun.framework.core.data.model.IColumnDeserializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {

    /**
     * 字段名
     */
    String name() default "";

    /**
     * 字段名
     */
    boolean isPrimaryKey() default false;

    /**
     * 反序列化自定义解析器，该类型必须包含无参构造器
     */
    Class<? extends IColumnDeserializer> deserializeResolver() default IColumnDeserializer.class;

}
