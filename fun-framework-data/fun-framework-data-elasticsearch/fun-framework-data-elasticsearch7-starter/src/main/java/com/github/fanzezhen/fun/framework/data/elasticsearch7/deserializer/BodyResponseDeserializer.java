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
 * 响应体反序列化器
 *
 * <p>将 Elasticsearch 的 ResponseBody 响应反序列化为 ISearchResult 对象。
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
     * 将 ES 响应值解析成 ISearchResult
     *
     * @param response ES 响应对象
     * @param clz 文档类型
     * @param <T> 文档泛型类型
     * @return 查询结果对象
     */
    @Override
    public <T> ISearchResult<T> deserialize(final Object response, final Class<T> clz) {
        ResponseBody<?> responseBody = (ResponseBody<?>) response;
        BodyResponseAdapter responseAdapter = new BodyResponseAdapter(responseBody);
        long totalHits = 0L;
        if (responseBody.hits() != null && responseBody.hits().total() != null) {
            totalHits = responseBody.hits().total().value();
        }
        return new SearchResultImpl<>(
            clz,
            responseAdapter,
            totalHits,
            responseBody.took(),
            responseBody.scrollId()
            );
    }

}
