package com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer;


import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.annotation.Column;
import com.github.fanzezhen.fun.framework.core.model.util.ValidUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.DocumentData;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 默认文档序列化器
 * <p>
 * 支持基于字段反射的文档序列化，将 Java 对象转换为 Elasticsearch 文档数据
 */

@Order
public class DefaultDocumentSerializer implements IDocumentSerializer {

    /**
     * 判断是否支持序列化指定类型的文档
     * <p>
     * 默认支持所有类型
     *
     * @param document 文档对象
     * @param vClass   文档的 Java 类型
     * @return 始终返回 true
     */
    @Override
    public boolean isSupport(final Object document, final Class<?> vClass) {
        return true;
    }

    /**
     * 序列化文档对象
     * <p>
     * 通过反射获取对象字段，将主键字段作为文档 ID，其他字段作为文档源数据
     *
     * @param document 文档对象
     * @param vClass   文档的 Java 类型
     * @return 文档数据对象
     */
    @Override
    public DocumentData serialize(final Object document, final Class<?> vClass) {
        String esId = null;
        final Field[] fields = ReflectUtil.getFields(vClass);
        JSONObject source = new JSONObject();
        for (Field field : fields) {
            String propertyName = CharSequenceUtil.EMPTY;
            Column column = field.getAnnotation(Column.class);
            if (Objects.nonNull(column)) {
                if (column.isPrimaryKey()) {
                    Object fieldValue = ReflectUtil.getFieldValue(document, field);
                    if (ValidUtil.isNotBlank(fieldValue)) {
                        esId = fieldValue.toString();
                    }
                    continue;
                }
                if (CharSequenceUtil.isNotBlank(column.name())) {
                    propertyName = column.name();
                }
            }
            if (CharSequenceUtil.isBlank(propertyName)) {
                propertyName = CharSequenceUtil.toUnderlineCase(field.getName());
            }
            source.put(propertyName, ReflectUtil.getFieldValue(document, field));
        }
        return new DocumentData(esId, source);
    }

}
