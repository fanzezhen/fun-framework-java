package com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.DocumentData;

/**
 * 文档序列化器接口
 * <p>
 * 用于将 Java 对象序列化为 Elasticsearch 文档数据
 */
public interface IDocumentSerializer {

    /**
     * 判断是否支持序列化指定类型的文档
     *
     * @param document 文档对象
     * @param vClass   文档的 Java 类型
     * @return 如果支持序列化则返回 true，否则返回 false
     */
    boolean isSupport(final Object document, final Class<?> vClass);

    /**
     * 序列化文档对象
     *
     * @param document 文档对象
     * @param vClass   文档的 Java 类型
     * @return 文档数据对象
     */
    DocumentData serialize(final Object document, final Class<?> vClass);

}
