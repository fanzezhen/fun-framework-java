package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;


import com.github.fanzezhen.fun.framework.core.data.annotation.Column;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * es id字段反序列化器，解析es映射javaBean中被 {@link Column} 标记的字段
 */
@Order(Short.MIN_VALUE)
@Component
public class EsIdAnnotationSupportFieldDeserializer extends BaseFieldDeserializer {

    @Override
    public boolean isSupport(Field field) {
        Column column = field.getAnnotation(Column.class);
        return Objects.nonNull(column) && column.isPrimaryKey();
    }

    @Override
    public Object deserialize(Field targetField, IHit hit) {
        return hit.getId();
    }

}
