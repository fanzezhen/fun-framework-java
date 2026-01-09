package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.serializer;

import co.elastic.clients.elasticsearch.core.MsearchRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.github.fanzezhen.fun.framework.core.log.base.serializer.impl.DefaultPrintSerializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.config.FunElasticsearch7AutoConfiguration;
import jakarta.json.stream.JsonGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
@Order(Short.MIN_VALUE)
public class MSearchRequestPrintSerializer extends DefaultPrintSerializer {

    @Override
    public boolean isSupport(Object o) {
        return o instanceof MsearchRequest;
    }

    @Override
    public String serialize(Object o) {
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
