package com.github.fanzezhen.fun.framework.data.elasticsearch.base.dao;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.model.entity.IEntity;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.IElasticsearchTemplate;
import jakarta.annotation.Resource;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 */
public abstract class AbstractEsDao<T extends IEntity<String>>  {

    @Resource
    protected IElasticsearchTemplate elasticsearchTemplate;

    public T get(Func1<T, ?> func, Serializable value) {
        return elasticsearchTemplate.get(func, value, getDocumentClass());
    }

    public T getById(String id) {
        return elasticsearchTemplate.getById(id, getDocumentClass());
    }

    public List<T> listByIds(Collection<String> ids) {
        return elasticsearchTemplate.listByIds(ids, getDocumentClass());
    }

    public boolean deleteById(Collection<String> ids) {
        return elasticsearchTemplate.deleteById(ids, getDocumentClass());
    }
    /**
     * 查询单个文档
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param request 查询条件
     * @return 查询结果
     */
    public T searchOne(Object request) {
        return elasticsearchTemplate.searchOne(request, getDocumentClass());
    }

    /**
     * 查询多个文档
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param request 查询条件
     * @return 查询结果列表
     */
    public List<T> searchList(Object request) {
        return elasticsearchTemplate.searchList(request, getDocumentClass());
    }

    /**
     * 高级查询,查询条件与searchList类似,但可以通过{@link ISearchResult}获取其他结果
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param requestBuilder 查询条件
     * @return SearchResult对象
     */
    public ISearchResult<T> search(Object requestBuilder) {
        return elasticsearchTemplate.search(requestBuilder, getDocumentClass());
    }

    /**
     * 游标查询，通过{@link ISearchResult}获取其他结果
     * scrollId 游标id用于获取下一批数据的标记，第一次查询游标id为空
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param requestBuilder     查询条件
     * @param timeSeconds 游标id查询的有效时间（单位：分钟）
     * @return SearchResult对象
     */
    public ISearchResult<T> scrollSearchByRequestBuilder(Object requestBuilder, Long timeSeconds) {
        return elasticsearchTemplate.scrollSearchByRequestBuilder(requestBuilder, getDocumentClass(), timeSeconds);
    }


    /**
     * 游标查询，通过{@link ISearchResult}获取其他结果
     * scrollId 游标id用于获取下一批数据的标记，第一次查询游标id为空
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param scrollId    游标id(第一次查询时游标id可为空)
     * @param timeSeconds 游标id查询的有效时间（单位：分钟）
     * @return SearchResult对象
     */
    public ISearchResult<T> scrollSearchByScrollId(String scrollId, Long timeSeconds) {
            return elasticsearchTemplate.scrollSearchByScrollId(scrollId, getDocumentClass(), timeSeconds);
    }


    /**
     * 游标查询，通过{@link ISearchResult}获取其他结果
     * scrollId 游标id用于获取下一批数据的标记，第一次查询游标id为空
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param requestBuilder     查询条件
     * @param scrollId    游标id(第一次查询时游标id可为空)
     * @param timeSeconds 游标id查询的有效时间（单位：分钟）
     * @return SearchResult对象
     */
    public ISearchResult<T> scrollSearch(Object requestBuilder, Long timeSeconds, String scrollId) {
        if (CharSequenceUtil.isNotEmpty(scrollId)){
            return elasticsearchTemplate.scrollSearchByScrollId(scrollId, getDocumentClass(), timeSeconds);
        }
        return elasticsearchTemplate.scrollSearchByRequestBuilder(requestBuilder, getDocumentClass(), timeSeconds);
    }

    /**
     * esMsearch,对按照请求返回多个结果
     *
     * @param requestBuilders 多个不同的请求build co.elastic.clients.elasticsearch.core.SearchRequest.Builder
     * @return 按照requestBuilders的顺序逐一包装的返回结果，如果某个request没有值，也会有一个空对象
     */
    public List<ISearchResult<T>> mSearch(Collection<?> requestBuilders) {
        return elasticsearchTemplate.mSearch(requestBuilders, getDocumentClass());
    }

    public abstract Class<T> getDocumentClass();
}
