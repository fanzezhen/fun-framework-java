package com.github.fanzezhen.fun.framework.data.elasticsearch7.deserializer;

import co.elastic.clients.elasticsearch.core.msearch.MultiSearchResponseItem;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.adapter.MultiSearchResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model.SearchResultImpl;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *
 */
@Order(Short.MAX_VALUE)
@Component
public class MSearchResponseDeserializer implements IResponseDeserializer {
    /**
     * 是否可以解析
     */
    @Override
    public boolean isSupport(Object response) {
        return response instanceof MultiSearchResponseItem;
    }

    /**
     * 将es响应值解析成IResponseAdapter
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> ISearchResult<T> deserialize(Object response, Class<T> clz) {
        MultiSearchResponseItem<JSONObject> realResponse = (MultiSearchResponseItem<JSONObject>) response;
        MultiSearchResponseAdapter responseAdapter = new MultiSearchResponseAdapter(realResponse);
        TotalHits totalHits = realResponse.result().hits().total();
        Long total = null;
        if (totalHits !=null){
            total = totalHits.value();
        }
        return new SearchResultImpl<>(clz, responseAdapter, total, realResponse.result().took());
    }
}
