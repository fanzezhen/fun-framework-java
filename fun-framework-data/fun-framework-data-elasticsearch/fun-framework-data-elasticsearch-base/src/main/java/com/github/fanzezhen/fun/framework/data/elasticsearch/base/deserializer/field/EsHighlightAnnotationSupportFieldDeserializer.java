package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;


import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.ESHighlightField;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * es 高亮字段解析器，解析es映射javaBean中被  {@link ESHighlightField} 标记的字段
 */
@Order(Short.MIN_VALUE)
@Component
public class EsHighlightAnnotationSupportFieldDeserializer extends BaseFieldDeserializer {

    @Override
    public boolean isSupport(Field field) {
        return Objects.nonNull(field.getAnnotation(ESHighlightField.class));
    }

    /**
     * 解析field在esRow中的值
     *
     * @param targetField javaBean属性
     */
    @Override
    public Object deserialize(Field targetField, IHit hit) {
        if (!targetField.getType().isAssignableFrom(Map.class)) {
            throw new SecurityException(String.format("@ESHighlightField 标记的属性必须是 Map<String, List> 类型，而 %s 字段类型为 %s ", targetField.toGenericString(), targetField.getType().getName()));
        }

        return hit.getHighlight();
    }
}
