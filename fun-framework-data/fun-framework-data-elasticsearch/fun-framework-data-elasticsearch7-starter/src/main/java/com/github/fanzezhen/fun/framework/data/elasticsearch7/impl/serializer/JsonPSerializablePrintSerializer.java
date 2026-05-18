package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.serializer;

import co.elastic.clients.json.JsonpSerializable;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.github.fanzezhen.fun.framework.core.log.serializer.impl.DefaultPrintSerializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.config.FunElasticsearch7AutoConfiguration;
import jakarta.json.stream.JsonGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * JsonpSerializable 打印序列化器
 *
 * <p>用于序列化实现了 JsonpSerializable 接口的对象，主要用于日志打印和调试。
 */
@Slf4j
@Component
@Order(Short.MAX_VALUE)
public class JsonPSerializablePrintSerializer extends DefaultPrintSerializer {

    /**
     * 判断是否支持指定对象的序列化
     *
     * @param o 待序列化对象
     * @return 如果支持则返回 true
     */
    @Override
    public boolean isSupport(final Object o) {
        return o instanceof JsonpSerializable;
    }

    /**
     * 序列化对象为字符串
     *
     * @param o 待序列化对象
     * @return 序列化后的字符串
     */
    @Override
    public String serialize(final Object o) {
        JsonpSerializable param = (JsonpSerializable) o;
        String requestString;
        JacksonJsonpMapper jacksonJsonpMapper = FunElasticsearch7AutoConfiguration.getJacksonJsonpMapper();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            try (JsonGenerator generator = jacksonJsonpMapper.jsonProvider().createGenerator(byteArrayOutputStream)) {
                param.serialize(generator, jacksonJsonpMapper);
            }
            requestString = byteArrayOutputStream.toString();
        } catch (IOException e) {
            log.info("", e);
            requestString = super.serialize(o);
        }
        return requestString;
    }
}
