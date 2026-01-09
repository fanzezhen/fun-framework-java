package com.github.fanzezhen.fun.framework.core.log.base.serializer.impl;

import com.github.fanzezhen.fun.framework.core.log.base.model.FileInfo;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Order(Short.MAX_VALUE - 2)
public class MultipartFilePrintSerializer extends DefaultPrintSerializer {

    @Override
    public boolean isSupport(Object o) {
        return o != null && MultipartFile.class.isAssignableFrom(o.getClass());
    }

    @Override
    public String serialize(Object o) {
        MultipartFile file = (MultipartFile) o;
        return super.serialize(new FileInfo(file.getName(), file.getOriginalFilename(), file.getSize()));
    }
}
