package com.github.fanzezhen.fun.framework.core.log.base.serializer.impl;

import com.github.fanzezhen.fun.framework.core.log.base.serializer.IPrintSerializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.Reader;
import java.util.Objects;

@Order(Short.MAX_VALUE - 4)
@Component
public class IoPrintSerializer implements IPrintSerializer {

    @Override
    public boolean isSupport(Object o) {
        if (Objects.isNull(o)) {
            return false;
        }
        final Class<?> aClass = o.getClass();
        return InputStream.class.isAssignableFrom(aClass) || Reader.class.isAssignableFrom(aClass);
    }

    @Override
    public String serialize(Object o) {
        return "'不支持的类型：InputStream'";
    }

}
