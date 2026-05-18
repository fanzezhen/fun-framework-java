package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import com.github.fanzezhen.fun.framework.core.model.common.IColumnDeserializer;
import com.github.fanzezhen.fun.framework.core.model.common.IRow;
import com.github.fanzezhen.fun.framework.core.model.util.ObjUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 字段反序列化器基类
 * <p>
 * 提供从 Elasticsearch 命中记录中提取和反序列化字段值的基础实现
 */
public abstract class BaseFieldDeserializer implements IColumnDeserializer {
    /**
     * 反序列化字段值
     * <p>
     * 如果行对象是 IHit 类型，则委托给专用的 deserialize 方法
     *
     * @param field 目标对象的属性字段
     * @param row   行对象
     * @return 反序列化后的字段值
     * @throws SecurityException 如果行对象不是 IHit 类型
     */
    @Override
    public Object deserialize(final Field field, final IRow row) {
        if (row instanceof IHit hit) {
            return deserialize(field, hit);
        }
        throw new SecurityException();
    }

    /**
     * 从命中记录中反序列化字段值
     *
     * @param field 目标对象的属性字段
     * @param hit   命中记录
     * @return 反序列化后的字段值
     */
    public Object deserialize(final Field field, final IHit hit) {
        final String columnName = getColumnName(field);
        return Optional.ofNullable(hit.getColumnSourceValue(columnName))
            .map(e -> ObjUtil.resolveByField(field, e))
            .orElse(ObjUtil.empty(field.getType()));
    }
}
