package com.github.fanzezhen.fun.framework.core.model.annotation;

import cn.hutool.core.text.CharSequenceUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实体注解
 * <p>
 * 用于标记实体类与数据库表的映射关系,支持指定表名、表前缀和数据源
 * </p>
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    /**
     * 数据库表名
     *
     * @return 表名
     */
    String table();

    /**
     * 表前缀名
     * <p>
     * 用于动态拼接表名，如：tablePrefix + table
     * </p>
     *
     * @return 表前缀，默认为空字符串
     */
    String tablePrefix() default CharSequenceUtil.EMPTY;

    /**
     * 数据源名称
     * <p>
     * 用于多数据源场景指定使用的数据源
     * </p>
     *
     * @return 数据源名称，默认为空字符串（使用默认数据源）
     */
    String datasource() default CharSequenceUtil.EMPTY;

}
