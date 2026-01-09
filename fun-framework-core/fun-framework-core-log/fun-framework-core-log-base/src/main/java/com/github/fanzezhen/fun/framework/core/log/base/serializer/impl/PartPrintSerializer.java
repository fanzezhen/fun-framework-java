package com.github.fanzezhen.fun.framework.core.log.base.serializer.impl;

import com.github.fanzezhen.fun.framework.core.log.base.model.FileInfo;
import jakarta.servlet.http.Part;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Short.MAX_VALUE - 3)
@Component
public class PartPrintSerializer extends DefaultPrintSerializer {

    @Override
    public boolean isSupport(Object o) {
        return o != null && Part.class.isAssignableFrom(o.getClass());
    }

    @Override
    public String serialize(Object o) {
        Part p = (Part) o;
        return super.serialize(new FileInfo(p.getName(), p.getSubmittedFileName(), p.getSize()));
    }

}
