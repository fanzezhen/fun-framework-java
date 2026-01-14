package com.github.fanzezhen.fun.framework.core.data.annotation;

import cn.hutool.core.text.CharSequenceUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * es索引标记
 *
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    /**
     * 表名
     */
    String table();

    /**
     * 表前缀名
     */
    String tablePrefix() default CharSequenceUtil.EMPTY;

    /**
     * 数据源名称
     */
    String datasource() default CharSequenceUtil.EMPTY;

}
