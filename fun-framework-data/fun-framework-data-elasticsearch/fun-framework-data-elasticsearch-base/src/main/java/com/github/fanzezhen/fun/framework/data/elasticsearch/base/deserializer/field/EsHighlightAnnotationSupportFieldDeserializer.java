package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;


import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.HighlightField;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * Elasticsearch 高亮字段注解支持反序列化器
 * <p>
 * 用于反序列化被 {@link HighlightField} 注解标记的字段，将 Elasticsearch 响应中的高亮字段映射到 Java 对象
 */
@Order(Short.MIN_VALUE)
@Component
public class EsHighlightAnnotationSupportFieldDeserializer extends BaseFieldDeserializer {

    /**
     * 判断是否支持反序列化指定字段
     * <p>
     * 支持被 {@link HighlightField} 注解标记的字段
     *
     * @param field 字段对象
     * @return 如果字段被标记则返回 true，否则返回 false
     */
    @Override
    public boolean isSupport(final Field field) {
        return Objects.nonNull(field.getAnnotation(HighlightField.class));
    }

    /**
     * 从命中记录中反序列化高亮字段值
     * <p>
     * 高亮字段必须是 Map 类型，否则抛出异常
     *
     * @param targetField 目标对象的属性字段
     * @param hit         命中记录
     * @return 高亮字段映射
     * @throws SecurityException 如果字段类型不是 Map
     */
    @Override
    public Object deserialize(final Field targetField, final IHit hit) {
        if (!targetField.getType().isAssignableFrom(Map.class)) {
            throw new SecurityException(String.format("@ESHighlightField 标记的属性必须是 Map<String, List> 类型，而 %s 字段类型为 %s ",
                    targetField.toGenericString(), targetField.getType().getName()));
        }

        return hit.getHighlight();
    }
}
