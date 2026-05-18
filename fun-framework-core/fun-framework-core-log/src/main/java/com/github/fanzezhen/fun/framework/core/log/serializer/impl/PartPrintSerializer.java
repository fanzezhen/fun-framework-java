package com.github.fanzezhen.fun.framework.core.log.serializer.impl;

import com.github.fanzezhen.fun.framework.core.log.model.FileInfo;
import jakarta.servlet.http.Part;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * servlet Part 对象（文件上传）的序列化器.
 * <p>
 * 将 Part 对象转换为 FileInfo，以记录文件上传元数据而不是内容。
 */
@Order(Short.MAX_VALUE - 3)
@Component
public class PartPrintSerializer extends DefaultPrintSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupport(final Object o) {
        return o != null && Part.class.isAssignableFrom(o.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(final Object o) {
        Part p = (Part) o;
        return super.serialize(new FileInfo(p.getName(), p.getSubmittedFileName(), p.getSize()));
    }

}
