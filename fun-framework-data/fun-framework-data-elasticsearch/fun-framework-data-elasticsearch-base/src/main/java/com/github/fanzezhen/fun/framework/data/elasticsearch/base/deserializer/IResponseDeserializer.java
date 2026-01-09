package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;

/**
 * 响应反序列化解析器
 */
public interface IResponseDeserializer {

    /**
     * 是否可以解析
     */
    boolean isSupport(Object response);

    /**
     * 将es响应值解析成IResponseAdapter
     *
     */
    <T> ISearchResult<T> deserialize(Object response, Class<T> vClass);

}
