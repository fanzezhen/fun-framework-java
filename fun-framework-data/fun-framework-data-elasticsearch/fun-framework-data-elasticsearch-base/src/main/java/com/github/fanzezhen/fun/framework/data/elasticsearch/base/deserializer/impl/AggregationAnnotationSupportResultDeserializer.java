package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl;

import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.core.model.util.ObjUtil;
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
 * 聚合注解支持结果反序列化器
 * <p>
 * 用于反序列化被 {@link Aggregation} 注解标记的类，将单个聚合结果映射到 Java 对象
 */
@Order(Short.MIN_VALUE + 101)
@Component
public class AggregationAnnotationSupportResultDeserializer extends BaseAggregationResultDeserializer {

    /**
     * 构造函数
     *
     * @param supportFieldResolveResultResolver 支持字段反序列化的结果反序列化器
     */
    public AggregationAnnotationSupportResultDeserializer(final SupportFieldDeserializerResultDeserializer supportFieldResolveResultResolver) {
        super(supportFieldResolveResultResolver);
    }

    /**
     * 判断是否支持反序列化为指定类型
     * <p>
     * 支持被 {@link Aggregation} 注解标记的类
     *
     * @param response 响应适配器
     * @param vClass   目标 Java 类型
     * @param <V>      泛型类型
     * @return 如果类被标记则返回 true，否则返回 false
     */
    @Override
    public <V> boolean isSupport(final IResponseAdapter response, final Class<V> vClass) {
        return Objects.nonNull(AnnotationUtils.findAnnotation(vClass, Aggregation.class));
    }

    /**
     * 将响应适配器中的聚合数据反序列化为对象列表
     * <p>
     * 根据注解配置获取指定的聚合，并将其数据映射到 Java 对象的各个字段
     *
     * @param response 响应适配器
     * @param vClass   目标 Java 类型
     * @param <V>      泛型类型
     * @return 包含单个对象的列表
     */
    @Override
    public <V> List<V> deserialize(final IResponseAdapter response, final Class<V> vClass) {
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

    /**
     * 获取聚合名称
     * <p>
     * 从 {@link Aggregation} 注解中获取聚合名称
     *
     * @param vClass 目标类型
     * @param <V>    泛型类型
     * @return 聚合名称，如果注解不存在则返回 null
     */
    private <V> String getAggregationName(final Class<V> vClass) {
        final Aggregation aggregation = AnnotationUtils.findAnnotation(vClass, Aggregation.class);
        if (Objects.nonNull(aggregation)) {
            return aggregation.value();
        }
        return null;
    }

}
