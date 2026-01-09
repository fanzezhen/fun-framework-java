package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.serializer;

import co.elastic.clients.elasticsearch._types.RequestBase;
import com.github.fanzezhen.fun.framework.core.log.base.serializer.impl.DefaultPrintSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(Short.MAX_VALUE -1 )
public class RequestPrintSerializer extends DefaultPrintSerializer {

    @Override
    public boolean isSupport(Object o) {
        return o instanceof RequestBase;
    }

    @Override
    public String serialize(Object o) {
        return o.toString();
    }
}
