package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.TypeUtil;
import com.github.fanzezhen.fun.framework.core.data.util.ObjUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.BucketAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl.BaseAggregationResultDeserializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 支持 @Aggregation 注解 的字段解析器
 */
public class AggregationFieldAnnotationSupportFieldDeserializer extends AbstractAggregationFieldDeserializer<IAggregationAdapter> {

    public AggregationFieldAnnotationSupportFieldDeserializer(BaseAggregationResultDeserializer baseAggregationResultResolver) {
        super(baseAggregationResultResolver);
    }

    /**
     * 解析field在聚合中的值
     *
     * @param targetField 目标对象的属性
     * @param adapter     聚合适配器
     */
    @Override
    public Object deserialize(Field targetField, IAggregationAdapter adapter) {
        if (Objects.isNull(adapter)) {
            return ObjUtil.empty(targetField.getType());
        }

        final AggregationField aggregationField = targetField.getAnnotation(AggregationField.class);

        //是否是嵌套聚合
        if (Objects.nonNull(aggregationField) && CharSequenceUtil.isNotBlank(aggregationField.aggregationName())) {
            adapter = adapter.getAggregation(aggregationField.aggregationName());
            return baseAggregationResultResolver.getAggregationAnnotationSupportFieldResolverInstance().resolveField(targetField, adapter);
        }

        String aggregationKey = getAggregationKey(targetField, aggregationField);
        Object value;
        if (aggregationKey.equals(AggregationFieldEnum.BUCKETS.getKey())) {
            value = resolveBuckets(targetField, adapter);
        } else if (aggregationKey.equals(AggregationFieldEnum.HITS.getKey())) {
            value = resolveHits(targetField, adapter);
        } else {
            value = adapter.get(aggregationKey, targetField.getType());
        }
        return value;
    }

    private String getAggregationKey(Field targetField, AggregationField aggregationField) {
        String aggregationKey;
        if (Objects.isNull(aggregationField)) {
            // 驼峰命名转换为下划线命名方式，例如：userName->user_name
            aggregationKey = CharSequenceUtil.toUnderlineCase(targetField.getName());
        } else if (aggregationField.value() != AggregationFieldEnum.NULL) {
            aggregationKey = aggregationField.value().getKey();
        } else if (CharSequenceUtil.isNotBlank(aggregationField.fieldName())) {
            aggregationKey = aggregationField.fieldName();
        } else {
            aggregationKey = targetField.getName();
        }
        return aggregationKey;
    }

    private Object resolveHits(Field targetField, IAggregationAdapter adapter) {
        final IHitsAdapter hits = adapter.getHits();
        if (Objects.isNull(hits) || CollUtil.isEmpty(hits.getHitList())) {
            return ObjUtil.empty(targetField.getType());
        }
        // 如果是List则获取泛型
        if (targetField.getType().isAssignableFrom(List.class)) {
            Class<?> tClass = (Class<?>) TypeUtil.getTypeArgument(targetField.getGenericType());
            List<Object> tList = new ArrayList<>();
            for (IHit hit : hits.getHitList()) {
                final Object bean = baseAggregationResultResolver.getSupportFieldResolveResultResolver().resolveHit(hit, tClass);
                tList.add(bean);
            }
            return tList;
        } else {
            final Class<?> type = targetField.getType();
            final IHit hit = hits.getHitList().get(0);
            return baseAggregationResultResolver.getSupportFieldResolveResultResolver().resolveHit(hit, type);
        }
    }

    private Object resolveBuckets(Field targetField, IAggregationAdapter adapter) {
        final List<BucketAdapter> bucketAdapters = adapter.getBuckets();
        return this.baseAggregationResultResolver.getBucketFieldAnnotationSupportFieldResolverInstance().deserialize(targetField, bucketAdapters);
    }

}
