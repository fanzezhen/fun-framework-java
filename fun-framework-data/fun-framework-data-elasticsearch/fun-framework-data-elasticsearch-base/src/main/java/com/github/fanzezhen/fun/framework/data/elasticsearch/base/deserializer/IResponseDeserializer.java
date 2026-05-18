package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;

/**
 * 响应反序列化器接口
 * <p>
 * 用于将 Elasticsearch 原始响应对象反序列化为搜索结果对象
 */
public interface IResponseDeserializer {

    /**
     * 判断是否支持反序列化指定的响应对象
     *
     * @param response 原始响应对象
     * @return 如果支持反序列化则返回 true，否则返回 false
     */
    boolean isSupport(final Object response);

    /**
     * 将响应对象反序列化为搜索结果
     *
     * @param response 原始响应对象
     * @param vClass   目标 Java 类型
     * @param <T>      泛型类型
     * @return 搜索结果对象
     */
    <T> ISearchResult<T> deserialize(final Object response, final Class<T> vClass);

}
