package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.util.ObjUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.BucketAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.BucketField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.BucketFieldEnum;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl.BaseAggregationResultDeserializer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 桶字段注解支持反序列化器
 * <p>
 * 用于反序列化被 {@link BucketField} 注解标记的字段，将聚合桶数据映射到 Java 对象
 */
public class BucketFieldAnnotationSupportFieldDeserializer extends AbstractAggregationFieldDeserializer<List<BucketAdapter>> {

    /**
     * 构造函数
     *
     * @param baseAggregationResultResolver 基础聚合结果反序列化器
     */
    public BucketFieldAnnotationSupportFieldDeserializer(final BaseAggregationResultDeserializer baseAggregationResultResolver) {
        super(baseAggregationResultResolver);
    }

    /**
     * 反序列化桶字段值
     * <p>
     * 将桶适配器列表中的数据映射到目标字段，支持单个桶或桶列表
     *
     * @param targetField 目标对象的属性字段
     * @param adapters    桶适配器列表
     * @return 反序列化后的字段值
     */
    @Override
    public Object deserialize(final Field targetField, List<BucketAdapter> adapters) {
        // 获取类型，如果是List则获取泛型
        Class<?> targetFieldClass = targetField.getType();
        if (CollUtil.isEmpty(adapters)) {
            return ObjUtil.empty(targetFieldClass);
        }
        if (targetField.getType().isAssignableFrom(List.class)) {
            Type typeArgument = TypeUtil.getTypeArgument(targetField.getGenericType());
            if (typeArgument instanceof ParameterizedType parameterizedType){
                targetFieldClass = (Class<?>) parameterizedType.getRawType();
            }else {
                targetFieldClass = (Class<?>) typeArgument;
            }
        } else if (targetField.getType().isAssignableFrom(JSONArray.class)) {
            targetFieldClass = JSONObject.class;
        } else {
            //如果不是List，取第一个
            adapters = Collections.singletonList(adapters.getFirst());
        }
        return resolve(adapters, targetFieldClass);
    }

    /**
     * 解析桶适配器列表
     * <p>
     * 遍历桶适配器列表，将每个桶的属性赋值到目标类对象中，转换为对象列表
     *
     * @param adapters 桶适配器列表
     * @param hitClass 目标类型
     * @return 对象列表
     */
    private List<Object> resolve(final List<BucketAdapter> adapters, final Class<?> hitClass) {
        if (hitClass.isAssignableFrom(JSONObject.class)){
            return new JSONArray().fluentAddAll(adapters.stream().map(BucketAdapter::getBucketJson).toList());
        }
        List<Object> result = new ArrayList<>(adapters.size());
        final Field[] fields = ReflectUtil.getFields(hitClass);
        for (BucketAdapter bucketAdapter : adapters) {
            final Object instance = ReflectUtil.newInstance(hitClass);
            for (Field field : fields) {
                Object value = resolveField(bucketAdapter, field, field.getType());
                if (Objects.nonNull(value)) {
                    final Object filedValue = ObjUtil.resolveByField(field, value);
                    ReflectUtil.setFieldValue(instance, field, filedValue);
                }
            }
            result.add(instance);
        }
        return result;
    }

    /**
     * 解析桶中的单个字段
     *
     * @param bucketAdapter 桶适配器
     * @param field         字段对象
     * @param hitClass      目标类型
     * @return 字段值
     */
    private Object resolveField(final BucketAdapter bucketAdapter, final Field field, final Class<?> hitClass) {
        final BucketField bucketField = field.getAnnotation(BucketField.class);
        final String fieldName = field.getName();
        Class<?> clazz = field.getType();

        if (Objects.isNull(bucketField)) {
            final IAggregationAdapter aggregation = bucketAdapter.getAggregation(fieldName);
            if (Objects.isNull(aggregation)) {
                return resolveSimpleField(bucketAdapter, fieldName, clazz);
            } else {
                return resolveAggregationField(aggregation, field);
            }
        } else {
            String aggregationName = bucketField.aggregationName();
            if (CharSequenceUtil.isBlank(aggregationName)) {
                String bucketName = fieldName;
                if (bucketField.value() != BucketFieldEnum.NULL) {
                    bucketName = bucketField.value().getKey();
                } else if (CharSequenceUtil.isNotBlank(bucketField.bucketKey())) {
                    bucketName = bucketField.bucketKey();
                }
                return resolveSimpleField(bucketAdapter, bucketName, clazz);
            } else {
                return resolveAggregationField(bucketAdapter.getAggregation(aggregationName), field);
            }
        }
    }

    /**
     * 解析嵌套聚合字段
     *
     * @param aggregation 聚合适配器
     * @param field       字段对象
     * @return 反序列化后的聚合字段值
     */
    private Object resolveAggregationField(final IAggregationAdapter aggregation, final Field field) {
        if (Objects.isNull(aggregation)) {
            return ObjUtil.empty(field.getType());
        }
        return this.baseAggregationResultResolver.getAggregationFieldAnnotationSupportFieldResolverInstance()
                .deserialize(field, aggregation);
    }

    /**
     * 解析桶中的简单字段
     *
     * @param bucketAdapter 桶适配器
     * @param bucketName    桶字段名
     * @param clazz         目标类型
     * @return 字段值
     */
    private Object resolveSimpleField(final BucketAdapter bucketAdapter, String bucketName, final Class<?> clazz) {
        // 驼峰命名转换为下划线命名方式，例如：userName->user_name
        bucketName = CharSequenceUtil.toUnderlineCase(bucketName);
        return bucketAdapter.get(bucketName, clazz);
    }

}
