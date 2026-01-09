package com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer;


import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.data.annotation.Column;
import com.github.fanzezhen.fun.framework.core.exception.ValidUtil;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.DocumentData;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 支持 字段序列化器 的 文档序列化器
 */

@Order
public class DefaultDocumentSerializer implements IDocumentSerializer {

    /**
     * 是否可以解析
     *
     * @param document 文档对象
     * @param vClass   序列化的java泛型
     */
    @Override
    public boolean isSupport(Object document, Class<?> vClass) {
        return true;
    }

    /**
     * 序列化
     *
     * @param document 文档对象
     * @param vClass   序列化的java泛型
     */
    @Override
    public DocumentData serialize(Object document, Class<?> vClass) {
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
