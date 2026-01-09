package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl;

import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.data.util.ObjUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IAggregationsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregations;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 支持 @Aggregations 注解 的结果解析器
 */
@Order(Short.MIN_VALUE + 100)
@Component
public class AggregationsAnnotationSupportResultDeserializer extends BaseAggregationResultDeserializer {

    public AggregationsAnnotationSupportResultDeserializer(SupportFieldDeserializerResultDeserializer supportFieldResolveResultResolver) {
        super(supportFieldResolveResultResolver);
    }

    @Override
    public <V> boolean isSupport(IResponseAdapter response, Class<V> vClass) {
        return Objects.nonNull(AnnotationUtils.findAnnotation(vClass, Aggregations.class));
    }

    @Override
    public <V> List<V> deserialize(IResponseAdapter response, Class<V> vClass) {
        final V instance = ReflectUtil.newInstance(vClass);
        final Field[] fields = ReflectUtil.getFields(vClass);
        final IAggregationsAdapter aggregationsAdapter = response.getAggregationsAdapter();

        if (Objects.isNull(aggregationsAdapter)) {
            return Collections.emptyList();
        }

        for (Field field : fields) {
            Object value = getAggregationAnnotationSupportFieldResolverInstance().deserialize(field, aggregationsAdapter);
            if (Objects.nonNull(value)) {
                final Object filedValue = ObjUtil.resolveByField(field, value);
                ReflectUtil.setFieldValue(instance, field, filedValue);
            }
        }
        return Collections.singletonList(instance);
    }

}
