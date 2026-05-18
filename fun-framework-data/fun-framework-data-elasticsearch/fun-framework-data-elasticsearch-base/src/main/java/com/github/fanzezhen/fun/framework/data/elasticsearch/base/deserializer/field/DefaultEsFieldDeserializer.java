package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import com.github.fanzezhen.fun.framework.core.model.common.IColumnDeserializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 默认 Elasticsearch 字段反序列化器
 * <p>
 * 作为字段反序列化的兜底实现，支持所有未被其他特定反序列化器处理的字段
 */
@Order
@Component
public class DefaultEsFieldDeserializer implements IColumnDeserializer {

    /**
     * 判断是否支持反序列化指定字段
     * <p>
     * 默认支持所有字段
     *
     * @param field 字段对象
     * @return 始终返回 true
     */
    @Override
    public boolean isSupport(final Field field) {
        return true;
    }

}
