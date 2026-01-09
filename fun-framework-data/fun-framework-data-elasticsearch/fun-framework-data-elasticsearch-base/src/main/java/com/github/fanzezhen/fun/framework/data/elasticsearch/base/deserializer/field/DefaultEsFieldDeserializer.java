package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;

import com.github.fanzezhen.fun.framework.core.data.model.IColumnDeserializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 默认
 */
@Order
@Component
public class DefaultEsFieldDeserializer implements IColumnDeserializer {

    @Override
    public boolean isSupport(Field field) {
        return true;
    }

}
