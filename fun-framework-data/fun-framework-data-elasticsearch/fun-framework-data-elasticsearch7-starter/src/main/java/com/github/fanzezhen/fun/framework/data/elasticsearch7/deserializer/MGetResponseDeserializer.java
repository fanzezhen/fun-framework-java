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
 *
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
     * 将es响应值解析成IResponseAdapter
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> ISearchResult<T> deserialize(Object response, Class<T> clz) {
        MgetResponse<JSONObject> realResponse = (MgetResponse<JSONObject>) response;
        MultiGetResponseAdapter responseAdapter = new MultiGetResponseAdapter(realResponse);
        return new SearchResultImpl<>(clz, responseAdapter, (long) realResponse.docs().size());
    }
}
