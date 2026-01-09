package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.data.annotation.Column;
import com.github.fanzezhen.fun.framework.core.data.model.IColumnDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * es 字段名映射解析器，解析es映射javaBean中被 {@link Column} 标记的字段
 */
@Order(Short.MAX_VALUE)
@Component
public class EsPropertyAnnotationSupportFieldDeserializer extends BaseFieldDeserializer {

    private final Map<Class<? extends IColumnDeserializer>, IColumnDeserializer> instanceFieldResolverCache = new ConcurrentHashMap<>();

    @Override
    public boolean isSupport(Field field) {
        Column column = field.getAnnotation(Column.class);
        return Objects.nonNull(column) && !column.isPrimaryKey();
    }

    @Override
    public Object deserialize(Field targetField, IHit hit) {
        final Column esProperty = targetField.getAnnotation(Column.class);
        //自定义解析器处理
        if (esProperty.deserializeResolver() != IColumnDeserializer.class) {
            final Class<? extends IColumnDeserializer> fieldResolverClass = esProperty.deserializeResolver();
            IColumnDeserializer fieldResolver = instanceFieldResolverCache.get(fieldResolverClass);
            if (Objects.isNull(fieldResolver)) {
                fieldResolver = ReflectUtil.newInstance(esProperty.deserializeResolver());
                instanceFieldResolverCache.put(fieldResolverClass, fieldResolver);
            }
            if (fieldResolver.isSupport(targetField)) {
                return fieldResolver.deserialize(targetField, hit);
            }
        }
        return super.deserialize(targetField, hit);
    }

}
