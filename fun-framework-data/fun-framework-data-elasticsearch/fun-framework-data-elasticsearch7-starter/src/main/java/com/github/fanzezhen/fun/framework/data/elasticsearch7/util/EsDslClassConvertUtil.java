package com.github.fanzezhen.fun.framework.data.elasticsearch7.util;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import jakarta.json.stream.JsonGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Elasticsearch DSL 转换工具类
 *
 * <p>提供 Elasticsearch 请求对象到 DSL 语句的转换功能。
 */
public class EsDslClassConvertUtil {

    /**
     * 私有构造函数，防止实例化
     */
    private EsDslClassConvertUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 解析 SearchRequest 里面的查询语句并转换为 DSL
     *
     * @param request SearchRequest 对象
     * @param jacksonJsonMapper ES 的 JSON 映射器
     * @return ES DSL 语句
     */
    public static String convertToDsl(final SearchRequest request, final JacksonJsonpMapper jacksonJsonMapper) {
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
