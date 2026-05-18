package com.github.fanzezhen.fun.framework.data.elasticsearch7.deserializer;

import co.elastic.clients.elasticsearch.core.MgetResponse;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.adapter.MultiGetResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model.SearchResultImpl;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * MGet 响应反序列化器
 *
 * <p>将 Elasticsearch 的 MgetResponse 响应反序列化为 ISearchResult 对象。
 */
@Order(Short.MAX_VALUE)
@Component
public class MGetResponseDeserializer implements IResponseDeserializer {
    /**
     * 是否可以解析
     */
    @Override
    public boolean isSupport(Object response) {
        return response instanceof MgetResponse;
    }

    /**
     * 将 ES 响应值解析成 ISearchResult
     *
     * @param response ES 响应对象
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 查询结果对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> ISearchResult<T> deserialize(final Object response, final Class<T> clz) {
        MgetResponse<JSONObject> realResponse = (MgetResponse<JSONObject>) response;
        MultiGetResponseAdapter responseAdapter = new MultiGetResponseAdapter(realResponse);
        return new SearchResultImpl<>(clz, responseAdapter, (long) realResponse.docs().size());
    }
}
