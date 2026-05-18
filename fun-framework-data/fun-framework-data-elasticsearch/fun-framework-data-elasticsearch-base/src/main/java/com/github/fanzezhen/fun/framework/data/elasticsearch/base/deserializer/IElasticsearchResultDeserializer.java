package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;

import java.util.List;

/**
 * Elasticsearch 结果反序列化器接口
 * <p>
 * 用于将 Elasticsearch 响应适配器中的数据反序列化为 Java 对象列表
 */
public interface IElasticsearchResultDeserializer {

    /**
     * 判断是否支持反序列化为指定类型
     *
     * @param response 响应适配器
     * @param vClass   目标 Java 类型
     * @param <V>      泛型类型
     * @return 如果支持反序列化则返回 true，否则返回 false
     */
    <V> boolean isSupport(final IResponseAdapter response, final Class<V> vClass);

    /**
     * 将响应适配器中的数据反序列化为对象列表
     *
     * @param response 响应适配器
     * @param vClass   目标 Java 类型
     * @param <V>      泛型类型
     * @return 反序列化后的对象列表
     */
    <V> List<V> deserialize(final IResponseAdapter response, final Class<V> vClass);

}
