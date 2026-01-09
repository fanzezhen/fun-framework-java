package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import java.lang.reflect.Field;

public interface IAggregationFieldDeserializer<T> {

    /**
     * 解析field在聚合中的值
     *
     * @param targetField 目标对象的属性
     * @param adapter     聚合适配器
     */
    Object deserialize(Field targetField, T adapter);

}
