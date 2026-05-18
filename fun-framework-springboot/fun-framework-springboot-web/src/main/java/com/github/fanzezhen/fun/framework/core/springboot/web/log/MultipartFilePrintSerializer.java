package com.github.fanzezhen.fun.framework.core.springboot.web.log;

import com.github.fanzezhen.fun.framework.core.log.model.FileInfo;
import com.github.fanzezhen.fun.framework.core.log.serializer.impl.DefaultPrintSerializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * MultipartFile日志打印序列化器.
 * <p>
 * 将MultipartFile对象序列化为FileInfo对象用于日志打印，
 * 避免打印文件内容导致日志过大。
 */
@Component
@Order(Short.MAX_VALUE - 2)
public class MultipartFilePrintSerializer extends DefaultPrintSerializer {

    /**
     * 判断是否支持该类型的序列化.
     *
     * @param o 待序列化对象
     * @return true表示支持
     */
    @Override
    public boolean isSupport(final Object o) {
        return o != null && MultipartFile.class.isAssignableFrom(o.getClass());
    }

    /**
     * 序列化MultipartFile对象为文件信息字符串.
     *
     * @param o MultipartFile对象
     * @return 文件信息字符串
     */
    @Override
    public String serialize(final Object o) {
        MultipartFile file = (MultipartFile) o;
        return super.serialize(new FileInfo(file.getName(), file.getOriginalFilename(), file.getSize()));
    }
}
