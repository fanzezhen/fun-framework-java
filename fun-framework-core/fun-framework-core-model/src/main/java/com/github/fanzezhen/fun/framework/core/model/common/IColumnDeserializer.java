package com.github.fanzezhen.fun.framework.core.model.common;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.model.annotation.Column;
import com.github.fanzezhen.fun.framework.core.model.util.ObjUtil;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 反序列化字段解析器
 * <p>
 * 定义字段反序列化的解析策略，用于将数据行中的值反序列化为对象字段。
 * </p>
 */
public interface IColumnDeserializer {

    /**
     * 判断是否支持解析该字段
     *
     * @param field 字段对象
     * @return true 表示支持解析，false 表示不支持
     */
    boolean isSupport(Field field);

    /**
     * 反序列化字段值
     * <p>
     * 从数据行中获取对应列的值，并转换为字段类型
     * </p>
     *
     * @param field 目标对象的字段
     * @param row   数据行对象
     * @return 反序列化后的字段值，如果列值为 null 则返回该类型的默认空值
     */
    default Object deserialize(Field field, IRow row){
        final String columnName = getColumnName(field);
        return Optional.ofNullable(row.getColumnSourceValue(columnName))
            .map(e -> ObjUtil.resolveByField(field, e))
            .orElse(ObjUtil.empty(field.getType()));
    }

    /**
     * 获取字段对应的列名
     * <p>
     * 优先使用 @Column 注解指定的列名，否则将字段名从驼峰命名转换为下划线命名
     * </p>
     *
     * @param field 字段对象
     * @return 数据库列名
     */
    default String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        // 驼峰命名转换为下划线命名方式，例如：userName->user_name
        return column!=null&&CharSequenceUtil.isNotEmpty(column.name())?
            column.name():CharSequenceUtil.toUnderlineCase(field.getName());
    }

}
