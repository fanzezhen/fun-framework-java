package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.TypeUtil;
import com.github.fanzezhen.fun.framework.core.model.util.ObjUtil;
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
 * 聚合字段注解支持反序列化器
 * <p>
 * 用于反序列化被 {@link AggregationField} 注解标记的字段，支持桶和命中记录等聚合数据的映射
 */
public class AggregationFieldAnnotationSupportFieldDeserializer extends AbstractAggregationFieldDeserializer<IAggregationAdapter> {

    /**
     * 构造函数
     *
     * @param baseAggregationResultResolver 基础聚合结果反序列化器
     */
    public AggregationFieldAnnotationSupportFieldDeserializer(final BaseAggregationResultDeserializer baseAggregationResultResolver) {
        super(baseAggregationResultResolver);
    }

    /**
     * 反序列化聚合字段值
     * <p>
     * 根据字段注解配置，处理嵌套聚合、桶、命中记录等不同类型的聚合数据
     *
     * @param targetField 目标对象的属性字段
     * @param adapter     聚合适配器
     * @return 反序列化后的字段值
     */
    @Override
    public Object deserialize(final Field targetField, IAggregationAdapter adapter) {
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

    /**
     * 获取聚合字段键
     *
     * @param targetField      目标字段
     * @param aggregationField 聚合字段注解
     * @return 聚合键
     */
    private String getAggregationKey(final Field targetField, final AggregationField aggregationField) {
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

    /**
     * 解析命中记录字段
     *
     * @param targetField 目标字段
     * @param adapter     聚合适配器
     * @return 反序列化后的命中记录
     */
    private Object resolveHits(final Field targetField, final IAggregationAdapter adapter) {
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
            final IHit hit = hits.getHitList().getFirst();
            return baseAggregationResultResolver.getSupportFieldResolveResultResolver().resolveHit(hit, type);
        }
    }

    /**
     * 解析桶字段
     *
     * @param targetField 目标字段
     * @param adapter     聚合适配器
     * @return 反序列化后的桶列表
     */
    private Object resolveBuckets(final Field targetField, final IAggregationAdapter adapter) {
        final List<BucketAdapter> bucketAdapters = adapter.getBuckets();
        return this.baseAggregationResultResolver.getBucketFieldAnnotationSupportFieldResolverInstance().deserialize(targetField, bucketAdapters);
    }

}
