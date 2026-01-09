package com.github.fanzezhen.fun.framework.data.elasticsearch7.deserializer;

import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.adapter.BodyResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model.SearchResultImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *
 */
@Slf4j
@Order(Short.MIN_VALUE)
@Component
public class BodyResponseDeserializer implements IResponseDeserializer {
    /**
     * 是否可以解析
     */
    @Override
    public boolean isSupport(Object response) {
        return response instanceof ResponseBody;
    }

    /**
     * 将es响应值解析成IResponseAdapter
     *
     */
    @Override
    public <T> ISearchResult<T> deserialize(Object response, Class<T> clz) {
        ResponseBody<?> responseBody = (ResponseBody<?>) response;
        BodyResponseAdapter responseAdapter = new BodyResponseAdapter(responseBody);
        return new SearchResultImpl<>(
            clz,
            responseAdapter,
            responseBody.hits().total() !=null? responseBody.hits().total().value():0,
            responseBody.took(),
            responseBody.scrollId()
            );
    }

}
