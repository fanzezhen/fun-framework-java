package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.BaseSearchResult;
import lombok.ToString;

/**
 * 封装ES查询结果
 *
 */
@ToString
public class SearchResultImpl<T> extends BaseSearchResult<T, SearchResponse<?>> {

    private final Long totalHits;

    private final Long totalTime;
    private final String scrollId;

    private final IResponseAdapter responseAdapter;

    public SearchResultImpl(Class<T> tClass, IResponseAdapter responseAdapter, Long totalHits) {
        this(tClass, responseAdapter, totalHits, null);
    }

    public SearchResultImpl(Class<T> tClass, IResponseAdapter responseAdapter, Long totalHits, Long totalTime) {
        this(tClass, responseAdapter, totalHits, totalTime, null);
    }

    public SearchResultImpl(Class<T> tClass, IResponseAdapter responseAdapter, Long totalHits, Long totalTime, String scrollId) {
        super(tClass);
        this.totalHits = totalHits;
        this.totalTime = totalTime;
        this.scrollId = scrollId;
        this.responseAdapter = responseAdapter;
    }

    @Override
    public long getTotalHits() {
        return totalHits;
    }

    @Override
    public double getTotalTime() {
        return totalTime;
    }

    @Override
    public String getScrollId() {
        return this.scrollId;
    }

    @Override
    public IResponseAdapter getResponseAdapter() {
        return this.responseAdapter;
    }

}
