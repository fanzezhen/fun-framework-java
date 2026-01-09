package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.github.fanzezhen.fun.framework.core.data.util.ObjUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.BucketAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.BucketField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.BucketFieldEnum;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl.BaseAggregationResultDeserializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 支持 @Bucket 注解 的字段解析器
 */
public class BucketFieldAnnotationSupportFieldDeserializer extends AbstractAggregationFieldDeserializer<List<BucketAdapter>> {

    public BucketFieldAnnotationSupportFieldDeserializer(BaseAggregationResultDeserializer baseAggregationResultResolver) {
        super(baseAggregationResultResolver);
    }

    /**
     * 解析field在聚合中的值
     *
     * @param targetField 目标对象的属性
     * @param adapters    聚合适配器
     */
    @Override
    public Object deserialize(Field targetField, List<BucketAdapter> adapters) {
        // 获取类型，如果是List则获取泛型
        Class<?> tClass = targetField.getType();
        if (CollUtil.isEmpty(adapters)) {
            return ObjUtil.empty(tClass);
        }
        if (targetField.getType().isAssignableFrom(List.class)) {
            tClass = (Class<?>) TypeUtil.getTypeArgument(targetField.getGenericType());
        } else {
            //如果不是List，取第一个
            adapters = Collections.singletonList(adapters.get(0));
        }

        return resolve(adapters, tClass);
    }

    /**
     * 遍历 bucketAdapters 将每一个 bucket 属性赋值到 Class 对象中，转化为 Class list
     */
    private List<Object> resolve(List<BucketAdapter> adapters, Class tClass) {
        List<Object> result = new ArrayList<>(adapters.size());
        //解析到对象中
        final Field[] fields = ReflectUtil.getFields(tClass);
        for (BucketAdapter bucketAdapter : adapters) {
            final Object instance = ReflectUtil.newInstance(tClass);
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
     * 解析字段
     *
     * @param bucketAdapter bucket适配器
     * @param field         字段
     * @param clazz         解析类型（如果字段是List，则为List的泛型）
     */
    private Object resolveField(BucketAdapter bucketAdapter, Field field, Class clazz) {
        final BucketField bucketField = field.getAnnotation(BucketField.class);
        final String fieldName = field.getName();

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
     * 解析嵌套聚合
     */
    private Object resolveAggregationField(IAggregationAdapter aggregation, Field field) {
        if (Objects.isNull(aggregation)) {
            return ObjUtil.empty(field.getType());
        }
        return this.baseAggregationResultResolver.getAggregationFieldAnnotationSupportFieldResolverInstance()
                .deserialize(field, aggregation);
    }

    /**
     * 解析普通字段
     */
    private Object resolveSimpleField(BucketAdapter bucketAdapter, String bucketName, Class clazz) {
        // 驼峰命名转换为下划线命名方式，例如：userName->user_name
        bucketName = CharSequenceUtil.toUnderlineCase(bucketName);
        return bucketAdapter.get(bucketName, clazz);
    }

}
