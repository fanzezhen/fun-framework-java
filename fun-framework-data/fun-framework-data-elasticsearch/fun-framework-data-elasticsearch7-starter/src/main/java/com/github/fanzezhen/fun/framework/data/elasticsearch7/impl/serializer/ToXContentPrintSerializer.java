package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.serializer;

import com.github.fanzezhen.fun.framework.core.log.base.serializer.impl.DefaultPrintSerializer;
import org.elasticsearch.common.Strings;
import org.elasticsearch.xcontent.ToXContent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Short.MIN_VALUE)
public class ToXContentPrintSerializer extends DefaultPrintSerializer {

    @Override
    public boolean isSupport(Object o) {
        return o instanceof ToXContent;
    }

    @Override
    public String serialize(Object o) {
        return Strings.toString((ToXContent) o);
    }
}
