package com.github.fanzezhen.fun.framework.data.elasticsearch7.util;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import jakarta.json.stream.JsonGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EsDslClassConvertUtil {

    /**
     * 解析request里面的查询语句
     * @param request SearchRequest
     * @param jacksonJsonMapper es的序列化器
     * @return es dsl语句
     */
    public static String convertToDsl(SearchRequest request, JacksonJsonpMapper jacksonJsonMapper) {
        String q = request.q();
        if (q != null && !q.isEmpty()){
            return q;
        }
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            JsonGenerator generator = jacksonJsonMapper.jsonProvider().createGenerator(byteArrayOutputStream);
            jacksonJsonMapper.serialize(request, generator);
            generator.close();
            return byteArrayOutputStream.toString();
        } catch (IOException e) {
            throw new ServiceException("转换es查询语句为dsl时发生异常："+e.getMessage(), e);
        }
    }
}
