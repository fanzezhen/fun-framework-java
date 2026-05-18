package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl;

import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.model.util.ObjUtil;
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
 * 聚合集合注解支持结果反序列化器
 * <p>
 * 用于反序列化被 {@link Aggregations} 注解标记的类，将聚合集合结果映射到 Java 对象
 */
@Order(Short.MIN_VALUE + 100)
@Component
public class AggregationsAnnotationSupportResultDeserializer extends BaseAggregationResultDeserializer {

    /**
     * 构造函数
     *
     * @param supportFieldResolveResultResolver 支持字段反序列化的结果反序列化器
     */
    public AggregationsAnnotationSupportResultDeserializer(final SupportFieldDeserializerResultDeserializer supportFieldResolveResultResolver) {
        super(supportFieldResolveResultResolver);
    }

    /**
     * 判断是否支持反序列化为指定类型
     * <p>
     * 支持被 {@link Aggregations} 注解标记的类
     *
     * @param response 响应适配器
     * @param vClass   目标 Java 类型
     * @param <V>      泛型类型
     * @return 如果类被标记则返回 true，否则返回 false
     */
    @Override
    public <V> boolean isSupport(final IResponseAdapter response, final Class<V> vClass) {
        return Objects.nonNull(AnnotationUtils.findAnnotation(vClass, Aggregations.class));
    }

    /**
     * 将响应适配器中的聚合集合数据反序列化为对象列表
     * <p>
     * 遍历对象的所有字段，使用聚合注解支持字段反序列化器将聚合数据映射到各个字段
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
