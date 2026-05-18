package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.model.common.IColumnDeserializer;
import com.github.fanzezhen.fun.framework.core.model.util.ObjUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHitsAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IHit;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IElasticsearchResultDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field.EsIdAnnotationSupportFieldDeserializer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 支持字段反序列化器的结果反序列化器
 * <p>
 * 通过组合多个字段反序列化器，实现完整的 Elasticsearch 结果反序列化功能
 */
@Slf4j
@Order
@Component
public class SupportFieldDeserializerResultDeserializer implements IElasticsearchResultDeserializer {

    /**
     * 字段反序列化器列表
     */
    @Resource
    private List<IColumnDeserializer> fieldResolverList;

    /**
     * ID 注解支持字段反序列化器
     */
    @Resource
    private EsIdAnnotationSupportFieldDeserializer idAnnotationSupportFieldDeserializer;

    /**
     * 判断是否支持反序列化为指定类型
     * <p>
     * 默认支持所有类型
     *
     * @param response 响应适配器
     * @param vClass   目标 Java 类型
     * @param <V>      泛型类型
     * @return 始终返回 true
     */
    @Override
    public <V> boolean isSupport(final IResponseAdapter response, final Class<V> vClass) {
        return true;
    }

    /**
     * 将响应适配器中的数据反序列化为对象列表
     * <p>
     * 遍历所有命中记录，使用字段反序列化器将每条记录映射为 Java 对象
     *
     * @param response 响应适配器
     * @param vClass   目标 Java 类型
     * @param <V>      泛型类型
     * @return 反序列化后的对象列表
     */
    @Override
    public <V> List<V> deserialize(final IResponseAdapter response, final Class<V> vClass) {
        List<V> results = new ArrayList<>();
        final IHitsAdapter hits = response.getHitsAdapter();
        if (Objects.isNull(hits) || CollUtil.isEmpty(hits.getHitList())) {
            return results;
        }
        // 遍历所有行
        for (IHit hit : hits.getHitList()) {
            final V bean = resolveHit(hit, vClass);
            results.add(bean);
        }
        return results;
    }

    /**
     * 解析单条命中记录
     * <p>
     * 使用字段反序列化器将命中记录中的数据映射到 Java 对象的各个字段
     *
     * @param hit    命中记录
     * @param vClass 目标类型
     * @param <V>    泛型类型
     * @return 反序列化后的对象
     */
    public <V> V resolveHit(final IHit hit, final Class<V> vClass) {
        final V bean = ReflectUtil.newInstance(vClass);
        final Field[] fields = ReflectUtil.getFields(vClass);
        Field primaryField= findPrimaryField(fields);
        if (primaryField!=null) {
            setFieldValue(bean, primaryField, idAnnotationSupportFieldDeserializer, hit);
        }
        for (Field field : fields) {
            if (field.equals(primaryField)){
                continue;
            }
            IColumnDeserializer resolver = null;
            for (IColumnDeserializer fieldResolver : fieldResolverList) {
                if (fieldResolver.isSupport(field)) {
                    resolver = fieldResolver;
                    break;
                }
            }
            if (Objects.isNull(resolver)) {
                throw new SecurityException("没有找到可以解析" +field.toGenericString()+ "的解析器");
            }
            setFieldValue(bean, field, resolver, hit );
        }
        return bean;
    }

    /**
     * 设置字段值
     * <p>
     * 使用反序列化器从命中记录中提取字段值并设置到目标对象
     *
     * @param bean     目标对象
     * @param field    字段
     * @param resolver 字段反序列化器
     * @param hit      命中记录
     * @param <V>      泛型类型
     */
    private static <V> void setFieldValue(final V bean, final Field field, final IColumnDeserializer resolver, final IHit hit) {
        try {
            Object value = resolver.deserialize(field, hit);
            final Object filedValue = ObjUtil.resolveByField(field, value);
            ReflectUtil.setFieldValue(bean, field, filedValue);
        } catch (Exception e) {
            Object filedValue = null;
            try {
                filedValue = hit.getSourceValue(CharSequenceUtil.toUnderlineCase(field.getName()));
                if (filedValue != null && filedValue.getClass().isAssignableFrom(field.getType())) {
                    ReflectUtil.setFieldValue(bean, field, filedValue);
                }
            } catch (Exception ignored) {
                // 忽略字段值提取失败的异常，已在日志中记录
            }
            log.warn("resolveHit {} 不能够被 {} 解析 {} hit：{}",
                    field.toGenericString(),
                    resolver.getClass().getName(),
                    filedValue,
                    hit != null ? hit.dataToString() : CharSequenceUtil.EMPTY,
                    e);
        }
    }

    /**
     * 查找主键字段
     * <p>
     * 按优先级查找主键字段：1. 被注解标记的主键字段 2. 名为 "id" 的字段 3. 名为 "pk" 的字段
     *
     * @param fields 字段数组
     * @return 主键字段，如果未找到则返回 null
     */
    private Field findPrimaryField(final Field[] fields) {
        for (Field field : fields) {
            if (idAnnotationSupportFieldDeserializer.isSupport(field)){
                return field;
            }
        }
        for (Field field : fields) {
            if ("id".equals(field.getName())){
                return field;
            }
        }
        for (Field field : fields) {
            if ("pk".equals(field.getName())){
                return field;
            }
        }
        return null;
    }

}
