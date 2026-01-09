package com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 高亮字段标记，必须是Map&lt;String, List&lt;String&gt;&gt; 类型，key为es字段名，值为加高亮标签的值
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ESHighlightField {
}
