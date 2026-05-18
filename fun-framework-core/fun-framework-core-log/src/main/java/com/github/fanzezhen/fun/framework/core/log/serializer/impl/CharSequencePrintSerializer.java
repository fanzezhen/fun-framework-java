package com.github.fanzezhen.fun.framework.core.log.serializer.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * CharSequence 对象的序列化器.
 * <p>
 * 使用 toString 方法直接将 CharSequence 转换为字符串。
 */
@Component
@Order(CharSequencePrintSerializer.ORDER_VALUE)
public class CharSequencePrintSerializer extends DefaultPrintSerializer {

    /**
     * 此序列化器的 Spring 顺序值.
     */
    static final int ORDER_VALUE = Short.MAX_VALUE - 5;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupport(final Object o) {
        return o instanceof CharSequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(final Object o) {
        return o.toString();
    }
}
