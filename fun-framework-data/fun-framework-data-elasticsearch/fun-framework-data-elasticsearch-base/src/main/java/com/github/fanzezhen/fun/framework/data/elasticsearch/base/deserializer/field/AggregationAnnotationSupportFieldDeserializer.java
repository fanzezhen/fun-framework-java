package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.core.model.util.ObjUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl.BaseAggregationResultDeserializer;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 聚合注解支持字段反序列化器
 * <p>
 * 用于反序列化被 {@link Aggregation} 注解标记的字段，将聚合集合中的数据映射到 Java 对象
 */
public class AggregationAnnotationSupportFieldDeserializer extends AbstractAggregationFieldDeserializer<IAggregationsAdapter> {

    /**
     * 构造函数
     *
     * @param baseAggregationResultResolver 基础聚合结果反序列化器
     */
    public AggregationAnnotationSupportFieldDeserializer(final BaseAggregationResultDeserializer baseAggregationResultResolver) {
        super(baseAggregationResultResolver);
    }

    /**
     * 反序列化聚合字段值
     * <p>
     * 根据 {@link Aggregation} 注解配置获取对应的聚合，并将其映射到目标字段
     *
     * @param targetField 目标对象的属性字段
     * @param adapter     聚合集合适配器
     * @return 反序列化后的字段值
     */
    @Override
    public Object deserialize(final Field targetField, final IAggregationsAdapter adapter) {
        if (Objects.isNull(adapter)) {
            return ObjUtil.empty(targetField.getType());
        }
        final Aggregation aggregation = targetField.getAnnotation(Aggregation.class);
        String aggregationName;
        if (Objects.nonNull(aggregation) && CharSequenceUtil.isNotBlank(aggregation.value())) {
            aggregationName = aggregation.value();
        } else {
            aggregationName = targetField.getName();
        }

        final IAggregationAdapter aggregationAdapter = adapter.getAggregation(aggregationName);

        //如果标注了 @AggregationField 注解，优先使用
        if (Objects.nonNull(targetField.getAnnotation(AggregationField.class))) {
            return baseAggregationResultResolver.getAggregationFieldAnnotationSupportFieldResolverInstance().deserialize(targetField, aggregationAdapter);
        }

        return resolveField(targetField, aggregationAdapter);
    }

    /**
     * 解析聚合对象中的字段
     * <p>
     * 将聚合适配器中的数据映射到目标对象的各个字段
     *
     * @param targetField        目标字段
     * @param aggregationAdapter 聚合适配器
     * @return 反序列化后的对象
     */
    public Object resolveField(final Field targetField, final IAggregationAdapter aggregationAdapter) {
        final Object instance = ReflectUtil.newInstance(targetField.getType());
        final Field[] fields = ReflectUtil.getFields(targetField.getType());
        for (Field field : fields) {
            final Object value = this.baseAggregationResultResolver.getAggregationFieldAnnotationSupportFieldResolverInstance().deserialize(field, aggregationAdapter);
            if (Objects.nonNull(value)) {
                final Object filedValue = ObjUtil.resolveByField(field, value);
                ReflectUtil.setFieldValue(instance, field, filedValue);
            }
        }
        return instance;
    }

}
