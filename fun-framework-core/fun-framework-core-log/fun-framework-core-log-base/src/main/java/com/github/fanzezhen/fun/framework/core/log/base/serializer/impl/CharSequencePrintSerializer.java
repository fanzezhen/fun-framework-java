package com.github.fanzezhen.fun.framework.core.log.base.serializer.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Short.MAX_VALUE-5)
public class CharSequencePrintSerializer extends DefaultPrintSerializer {

    @Override
    public boolean isSupport(Object o) {
        return o instanceof CharSequence;
    }

    @Override
    public String serialize(Object o) {
        return o.toString();
    }
}
