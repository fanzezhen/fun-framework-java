package com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.DocumentData;

/**
 * 文档序列化器
 *
 */
public interface IDocumentSerializer {

    /**
     * 是否可以解析
     *
     * @param document 文档对象
     * @param vClass    序列化的java泛型
     */
    boolean isSupport(Object document, Class<?> vClass);

    /**
     * 序列化
     *
     * @param document 文档对象
     * @param vClass    序列化的java泛型
     */
    DocumentData serialize(Object document, Class<?> vClass);

}
