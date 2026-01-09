package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import com.github.fanzezhen.fun.framework.core.data.model.IColumnDeserializer;
import com.github.fanzezhen.fun.framework.core.data.model.IRow;
import com.github.fanzezhen.fun.framework.core.data.util.ObjUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 字段解析器基础类
 */
public abstract class BaseFieldDeserializer implements IColumnDeserializer {
    /**
     * 解析field
     *
     * @param field 目标对象的属性
     */
    @Override
    public Object deserialize(Field field, IRow row) {
        if (row instanceof IHit hit) {
            return deserialize(field, hit);
        }
        throw new SecurityException();
    }

    /**
     * 解析field
     */
    public Object deserialize(Field field, IHit hit) {
        final String columnName = getColumnName(field);
        return Optional.ofNullable(hit.getColumnSourceValue(columnName))
            .map(e -> ObjUtil.resolveByField(field, e))
            .orElse(ObjUtil.empty(field.getType()));
    }
}
