package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;

import java.util.List;

/**
 * 结果反序列化解析器
 */
public interface IElasticsearchResultDeserializer {

    /**
     * 是否可以解析
     *
     * @param vClass 返回值接收的java泛型
     */
    <V> boolean isSupport(IResponseAdapter response, Class<V> vClass);

    /**
     * 将es返回值解析成SearchResult
     *
     */
    <V> List<V> deserialize(IResponseAdapter response, Class<V> vClass);

}
