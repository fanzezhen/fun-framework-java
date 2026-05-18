package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.serializer;

import co.elastic.clients.elasticsearch._types.RequestBase;
import com.github.fanzezhen.fun.framework.core.log.serializer.impl.DefaultPrintSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Request 打印序列化器
 *
 * <p>用于序列化 RequestBase 对象，主要用于日志打印和调试。
 */
@Slf4j
@Component
@Order(Short.MAX_VALUE - 1)
public class RequestPrintSerializer extends DefaultPrintSerializer {

    /**
     * 判断是否支持指定对象的序列化
     *
     * @param o 待序列化对象
     * @return 如果支持则返回 true
     */
    @Override
    public boolean isSupport(final Object o) {
        return o instanceof RequestBase;
    }

    /**
     * 序列化对象为字符串
     *
     * @param o 待序列化对象
     * @return 序列化后的字符串
     */
    @Override
    public String serialize(final Object o) {
        return o.toString();
    }
}
