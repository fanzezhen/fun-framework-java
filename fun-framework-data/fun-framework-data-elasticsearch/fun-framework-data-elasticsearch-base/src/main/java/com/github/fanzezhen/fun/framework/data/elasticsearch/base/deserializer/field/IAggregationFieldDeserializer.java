package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import java.lang.reflect.Field;

/**
 * 聚合字段反序列化器接口
 * <p>
 * 用于从聚合适配器中提取字段值并反序列化为 Java 对象
 *
 * @param <T> 适配器类型
 */
public interface IAggregationFieldDeserializer<T> {

    /**
     * 反序列化聚合中的字段值
     *
     * @param targetField 目标对象的属性字段
     * @param adapter     聚合适配器
     * @return 反序列化后的字段值
     */
    Object deserialize(final Field targetField, final T adapter);

}
