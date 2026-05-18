package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.serializer;

import co.elastic.clients.elasticsearch.core.MsearchRequest;
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
 * MSearch 请求打印序列化器
 *
 * <p>用于序列化 MsearchRequest 对象，主要用于日志打印和调试。
 */
@Slf4j
@Component
@Order(Short.MIN_VALUE)
public class MSearchRequestPrintSerializer extends DefaultPrintSerializer {

    /**
     * 判断是否支持指定对象的序列化
     *
     * @param o 待序列化对象
     * @return 如果支持则返回 true
     */
    @Override
    public boolean isSupport(final Object o) {
        return o instanceof MsearchRequest;
    }

    /**
     * 序列化对象为字符串
     *
     * @param o 待序列化对象
     * @return 序列化后的字符串
     */
    @Override
    public String serialize(final Object o) {
        MsearchRequest msearchRequest = (MsearchRequest) o;
        String requestString;
        JacksonJsonpMapper jacksonJsonpMapper = FunElasticsearch7AutoConfiguration.getJacksonJsonpMapper();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            try (JsonGenerator generator = jacksonJsonpMapper.jsonProvider().createGenerator(byteArrayOutputStream)) {
                msearchRequest.serialize(generator, jacksonJsonpMapper);
            }
            requestString = byteArrayOutputStream.toString();
        } catch (IOException e) {
            log.info("", e);
            requestString = super.serialize(o);
        }
        return requestString;
    }
}
