package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl.BaseAggregationResultDeserializer;
import lombok.AllArgsConstructor;

/**
 * 聚合字段反序列化器抽象基类
 * <p>
 * 提供聚合字段反序列化的通用功能和对基础聚合结果反序列化器的引用
 *
 * @param <T> 适配器类型
 */
@AllArgsConstructor
public abstract class AbstractAggregationFieldDeserializer<T> implements IAggregationFieldDeserializer<T> {

    /**
     * 基础聚合结果反序列化器
     */
    protected BaseAggregationResultDeserializer baseAggregationResultResolver;

}
