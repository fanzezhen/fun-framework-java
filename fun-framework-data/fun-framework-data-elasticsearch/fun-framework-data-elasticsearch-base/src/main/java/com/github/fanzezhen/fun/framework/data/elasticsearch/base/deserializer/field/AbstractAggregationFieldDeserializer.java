package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl.BaseAggregationResultDeserializer;
import lombok.AllArgsConstructor;

/**
 */
@AllArgsConstructor
public abstract class AbstractAggregationFieldDeserializer<T> implements IAggregationFieldDeserializer<T> {

    protected BaseAggregationResultDeserializer baseAggregationResultResolver;

}
