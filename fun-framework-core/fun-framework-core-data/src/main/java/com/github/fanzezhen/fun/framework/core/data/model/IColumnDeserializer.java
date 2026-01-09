package com.github.fanzezhen.fun.framework.core.data.model;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.data.annotation.Column;
import com.github.fanzezhen.fun.framework.core.data.util.ObjUtil;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 反序列化字段解析器
 */
public interface IColumnDeserializer {

    /**
     * 是否可以解析该字段
     */
    boolean isSupport(Field field);

    /**
     * 解析field
     *
     * @param field 目标对象的属性
     */
    default Object deserialize(Field field, IRow row){
        final String columnName = getColumnName(field);
        return Optional.ofNullable(row.getColumnSourceValue(columnName))
            .map(e -> ObjUtil.resolveByField(field, e))
            .orElse(ObjUtil.empty(field.getType()));
    }

    default String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        // 驼峰命名转换为下划线命名方式，例如：userName->user_name
        return column!=null&&CharSequenceUtil.isNotEmpty(column.name())?
            column.name():CharSequenceUtil.toUnderlineCase(field.getName());
    }

}
