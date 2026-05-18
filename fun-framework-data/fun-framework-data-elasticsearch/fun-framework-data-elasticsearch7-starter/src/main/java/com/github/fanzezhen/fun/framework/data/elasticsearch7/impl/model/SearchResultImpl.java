package com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.model;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.BaseSearchResult;
import lombok.ToString;

/**
 * 搜索结果实现类
 *
 * <p>封装 Elasticsearch 查询结果，提供统一的访问接口。
 *
 * @param <T> 文档泛型类型
 */
@ToString
public class SearchResultImpl<T> extends BaseSearchResult<T, SearchResponse<?>> {

    /**
     * 总命中数
     */
    private final Long totalHits;

    /**
     * 总耗时
     */
    private final Long totalTime;

    /**
     * 游标 ID
     */
    private final String scrollId;

    /**
     * 响应适配器
     */
    private final IResponseAdapter responseAdapter;

    /**
     * 构造函数
     *
     * @param tClass 文档类型
     * @param responseAdapter 响应适配器
     * @param totalHits 总命中数
     */
    public SearchResultImpl(final Class<T> tClass, final IResponseAdapter responseAdapter, final Long totalHits) {
        this(tClass, responseAdapter, totalHits, null);
    }

    /**
     * 构造函数
     *
     * @param tClass 文档类型
     * @param responseAdapter 响应适配器
     * @param totalHits 总命中数
     * @param totalTime 总耗时
     */
    public SearchResultImpl(final Class<T> tClass, final IResponseAdapter responseAdapter,
                            final Long totalHits, final Long totalTime) {
        this(tClass, responseAdapter, totalHits, totalTime, null);
    }

    /**
     * 构造函数
     *
     * @param tClass 文档类型
     * @param responseAdapter 响应适配器
     * @param totalHits 总命中数
     * @param totalTime 总耗时
     * @param scrollId 游标 ID
     */
    public SearchResultImpl(final Class<T> tClass, final IResponseAdapter responseAdapter,
                            final Long totalHits, final Long totalTime, final String scrollId) {
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
