package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.core.data.util.ObjUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl.BaseAggregationResultDeserializer;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 支持 @Aggregation 注解 的字段解析器
 */
public class AggregationAnnotationSupportFieldDeserializer extends AbstractAggregationFieldDeserializer<IAggregationsAdapter> {

    public AggregationAnnotationSupportFieldDeserializer(BaseAggregationResultDeserializer baseAggregationResultResolver) {
        super(baseAggregationResultResolver);
    }

    /**
     * 解析field在聚合中的值
     *
     * @param targetField 目标对象的属性
     * @param adapter     聚合适配器
     */
    @Override
    public Object deserialize(Field targetField, IAggregationsAdapter adapter) {
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

    public Object resolveField(Field targetField, IAggregationAdapter aggregationAdapter) {
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
