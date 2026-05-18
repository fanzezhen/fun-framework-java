package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.serializer;

import com.github.fanzezhen.fun.framework.core.log.serializer.impl.DefaultPrintSerializer;
import org.elasticsearch.common.Strings;
import org.elasticsearch.xcontent.ToXContent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * ToXContent 打印序列化器
 *
 * <p>用于序列化实现了 ToXContent 接口的对象，主要用于日志打印和调试。
 */
@Component
@Order(Short.MIN_VALUE)
public class ToXContentPrintSerializer extends DefaultPrintSerializer {

    /**
     * 判断是否支持指定对象的序列化
     *
     * @param o 待序列化对象
     * @return 如果支持则返回 true
     */
    @Override
    public boolean isSupport(final Object o) {
        return o instanceof ToXContent;
    }

    /**
     * 序列化对象为字符串
     *
     * @param o 待序列化对象
     * @return 序列化后的字符串
     */
    @Override
    public String serialize(final Object o) {
        return Strings.toString((ToXContent) o);
    }
}
