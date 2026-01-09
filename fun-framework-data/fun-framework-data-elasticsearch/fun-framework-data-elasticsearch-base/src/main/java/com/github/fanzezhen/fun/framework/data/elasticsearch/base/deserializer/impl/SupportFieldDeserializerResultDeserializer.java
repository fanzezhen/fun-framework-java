package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.data.model.IColumnDeserializer;
import com.github.fanzezhen.fun.framework.core.data.util.ObjUtil;
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
 * 支持 字段序列化器 的 结果序列化器
 */
@Slf4j
@Order
@Component
public class SupportFieldDeserializerResultDeserializer implements IElasticsearchResultDeserializer {

    @Resource
    private List<IColumnDeserializer> fieldResolverList;
    @Resource
    private EsIdAnnotationSupportFieldDeserializer idAnnotationSupportFieldDeserializer;

    @Override
    public <V> boolean isSupport(IResponseAdapter response, Class<V> vClass) {
        return true;
    }

    @Override
    public <V> List<V> deserialize(IResponseAdapter response, Class<V> vClass) {
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

    public <V> V resolveHit(IHit hit, Class<V> vClass) {
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

    private static <V> void setFieldValue(V bean, Field field, IColumnDeserializer resolver, IHit hit ) {
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
            }
            log.warn("resolveHit {} 不能够被 {} 解析 {} hit：{}",
                    field.toGenericString(),
                    resolver.getClass().getName(),
                    filedValue,
                    hit != null ? hit.dataToString() : CharSequenceUtil.EMPTY,
                    e);
        }
    }

    private Field findPrimaryField(Field[] fields) {
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
