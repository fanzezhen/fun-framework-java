package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.core.data.util.ObjUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 支持 @Aggregation 注解 的结果解析器
 */
@Order(Short.MIN_VALUE + 101)
@Component
public class AggregationAnnotationSupportResultDeserializer extends BaseAggregationResultDeserializer {

    public AggregationAnnotationSupportResultDeserializer(SupportFieldDeserializerResultDeserializer supportFieldResolveResultResolver) {
        super(supportFieldResolveResultResolver);
    }

    @Override
    public <V> boolean isSupport(IResponseAdapter response, Class<V> vClass) {
        return Objects.nonNull(AnnotationUtils.findAnnotation(vClass, Aggregation.class));
    }

    @Override
    public <V> List<V> deserialize(IResponseAdapter response, Class<V> vClass) {
        final V instance = ReflectUtil.newInstance(vClass);
        final Field[] fields = ReflectUtil.getFields(vClass);
        final IAggregationsAdapter aggregationsAdapter = response.getAggregationsAdapter();
        if (Objects.isNull(aggregationsAdapter)) {
            return Collections.emptyList();
        }

        String aggregationName = getAggregationName(vClass);

        final IAggregationAdapter aggregationAdapter = aggregationsAdapter.getAggregation(aggregationName);
        if (Objects.isNull(aggregationAdapter)) {
            return Collections.emptyList();
        }
        for (Field field : fields) {
            Object value = getAggregationFieldAnnotationSupportFieldResolverInstance().deserialize(field, aggregationAdapter);
            if (Objects.nonNull(value)) {
                final Object filedValue = ObjUtil.resolveByField(field, value);
                ReflectUtil.setFieldValue(instance, field, filedValue);
            }
        }
        return Collections.singletonList(instance);
    }

    private <V> String getAggregationName(Class<V> vClass) {
        String aggregationName = "";
        final Aggregation aggregation = AnnotationUtils.findAnnotation(vClass, Aggregation.class);
        if (Objects.nonNull(aggregation)) {
            aggregationName = aggregation.value();
        }
        if (CharSequenceUtil.isBlank(aggregationName)) {
            throw new ServiceException("@Aggregation 注解的 value 属性不能为空");
        }
        return aggregationName;
    }

}
