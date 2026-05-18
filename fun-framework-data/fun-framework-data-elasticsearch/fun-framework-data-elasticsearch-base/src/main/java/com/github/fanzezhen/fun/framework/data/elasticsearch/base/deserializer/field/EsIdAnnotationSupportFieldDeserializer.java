package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;


import com.github.fanzezhen.fun.framework.core.model.annotation.Column;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Elasticsearch ID 注解支持字段反序列化器
 * <p>
 * 用于反序列化被 {@link Column} 注解标记为主键的字段，将 Elasticsearch 文档 ID 映射到 Java 对象的主键字段
 */
@Order(Short.MIN_VALUE)
@Component
public class EsIdAnnotationSupportFieldDeserializer extends BaseFieldDeserializer {

    /**
     * 判断是否支持反序列化指定字段
     * <p>
     * 支持被 {@link Column} 注解标记且 isPrimaryKey 为 true 的字段
     *
     * @param field 字段对象
     * @return 如果字段被标记为主键则返回 true，否则返回 false
     */
    @Override
    public boolean isSupport(final Field field) {
        Column column = field.getAnnotation(Column.class);
        return Objects.nonNull(column) && column.isPrimaryKey();
    }

    /**
     * 从命中记录中反序列化字段值
     * <p>
     * 直接返回文档的 ID 作为主键字段值
     *
     * @param targetField 目标对象的属性字段
     * @param hit         命中记录
     * @return 文档 ID
     */
    @Override
    public Object deserialize(final Field targetField, final IHit hit) {
        return hit.getId();
    }

}
