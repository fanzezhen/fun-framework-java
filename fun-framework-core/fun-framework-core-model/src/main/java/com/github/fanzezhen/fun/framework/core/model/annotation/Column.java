package com.github.fanzezhen.fun.framework.core.model.annotation;

import com.github.fanzezhen.fun.framework.core.model.common.IColumnDeserializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列注解
 * <p>
 * 用于标记实体字段与数据库列的映射关系,支持自定义列名、主键标识和反序列化处理
 * </p>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {

    /**
     * 数据库列名
     *
     * @return 列名，默认为空字符串（使用字段名）
     */
    String name() default "";

    /**
     * 是否为主键
     *
     * @return true 表示该字段为主键列，默认为 false
     */
    boolean isPrimaryKey() default false;

    /**
     * 是否参与写入
     * <p>
     * 置为 false 时该列只读：查询结果会映射到该字段，但写入语句不带该列。
     * 适用于由存储侧计算得出的派生列（如图数据库的节点度数、数据库的计算列）。
     * </p>
     *
     * @return true 表示参与写入，默认为 true
     * @since 4.1.1
     */
    boolean writable() default true;

    /**
     * 反序列化自定义解析器
     * <p>
     * 指定该列的自定义反序列化处理器类型，该类型必须包含无参构造器
     * </p>
     *
     * @return 反序列化解析器类，默认为 IColumnDeserializer.class
     */
    Class<? extends IColumnDeserializer> deserializeResolver() default IColumnDeserializer.class;

}
