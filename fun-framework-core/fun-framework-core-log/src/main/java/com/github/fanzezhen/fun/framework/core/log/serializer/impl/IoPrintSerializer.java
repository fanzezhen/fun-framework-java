package com.github.fanzezhen.fun.framework.core.log.serializer.impl;

import com.github.fanzezhen.fun.framework.core.log.serializer.IPrintSerializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.Reader;
import java.util.Objects;

/**
 * I/O 流对象的序列化器.
 * <p>
 * 通过返回占位符消息防止记录流内容。
 * 支持 InputStream 和 Reader 类型。
 */
@Order(Short.MAX_VALUE - 4)
@Component
public class IoPrintSerializer implements IPrintSerializer {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupport(final Object o) {
        if (Objects.isNull(o)) {
            return false;
        }
        final Class<?> aClass = o.getClass();
        return InputStream.class.isAssignableFrom(aClass) ||
                Reader.class.isAssignableFrom(aClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(final Object o) {
        return "'不支持的类型：InputStream'";
    }

}
