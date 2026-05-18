package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.model.annotation.Column;
import com.github.fanzezhen.fun.framework.core.model.common.IColumnDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Elasticsearch 属性注解支持字段反序列化器
 * <p>
 * 用于反序列化被 {@link Column} 注解标记的非主键字段，支持自定义反序列化器和字段名映射
 */
@Order(Short.MAX_VALUE)
@Component
public class EsPropertyAnnotationSupportFieldDeserializer extends BaseFieldDeserializer {

    /**
     * 字段反序列化器实例缓存
     */
    private final Map<Class<? extends IColumnDeserializer>, IColumnDeserializer> instanceFieldResolverCache = new ConcurrentHashMap<>();

    /**
     * 判断是否支持反序列化指定字段
     * <p>
     * 支持被 {@link Column} 注解标记且不是主键的字段
     *
     * @param field 字段对象
     * @return 如果字段被标记且不是主键则返回 true，否则返回 false
     */
    @Override
    public boolean isSupport(final Field field) {
        Column column = field.getAnnotation(Column.class);
        return Objects.nonNull(column) && !column.isPrimaryKey();
    }

    /**
     * 从命中记录中反序列化字段值
     * <p>
     * 如果字段配置了自定义反序列化器，则使用自定义反序列化器处理；否则使用默认处理逻辑
     *
     * @param targetField 目标对象的属性字段
     * @param hit         命中记录
     * @return 反序列化后的字段值
     */
    @Override
    public Object deserialize(final Field targetField, final IHit hit) {
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
