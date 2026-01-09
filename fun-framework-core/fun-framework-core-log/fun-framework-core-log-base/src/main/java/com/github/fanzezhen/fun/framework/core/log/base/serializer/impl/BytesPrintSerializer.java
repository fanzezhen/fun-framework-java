package com.github.fanzezhen.fun.framework.core.log.base.serializer.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Short.MAX_VALUE - 1)
public class BytesPrintSerializer extends DefaultPrintSerializer {

    @Override
    public boolean isSupport(Object o) {
        return o instanceof byte[];
    }

    @Override
    public String serialize(Object o) {
        return "'不支持的类型：byte[]'";
    }
}
