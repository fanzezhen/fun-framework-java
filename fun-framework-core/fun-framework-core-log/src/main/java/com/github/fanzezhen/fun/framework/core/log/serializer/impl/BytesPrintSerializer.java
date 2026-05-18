package com.github.fanzezhen.fun.framework.core.log.serializer.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 字节数组的序列化器.
 * <p>
 * 通过返回占位符消息防止记录大型二进制数据。
 */
@Component
@Order(Short.MAX_VALUE - 1)
public class BytesPrintSerializer extends DefaultPrintSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupport(final Object o) {
        return o instanceof byte[];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(final Object o) {
        return "'不支持的类型：byte[]'";
    }
}
