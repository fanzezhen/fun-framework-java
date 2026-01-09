package com.github.fanzezhen.fun.framework.data.elasticsearch.base.template;

import com.github.fanzezhen.fun.framework.core.data.template.ITemplate;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;

import java.util.Collection;
import java.util.List;

/**
 * Elasticsearch 操作模板接口
 *
 */
public interface IElasticsearchTemplate extends ITemplate<String> {
     String ELASTICSEARCH_MARK = "（elasticsearch）" ;

    /**
     * 查询单个文档
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param request 查询条件
     * @param clz     文档类型
     * @param <T>     返回文档类型
     * @return 查询结果列表
     */
    <T> T searchOne(Object request, Class<T> clz);

    /**
     * 查询多个文档
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param request 查询条件
     * @param clz     文档类型
     * @param <T>     返回文档类型
     * @return 查询结果列表
     */
    <T> List<T> searchList(Object request, Class<T> clz);

    /**
     * 高级查询,查询条件与searchList类似,但可以通过{@link ISearchResult}获取其他结果
     * <p>
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param requestBuilder 查询条件
     * @param clz     文档类型
     * @param <T>     返回文档类型
     * @return SearchResult对象
     */
    <T> ISearchResult<T> search(Object requestBuilder, Class<T> clz);

    /**
     * 游标查询，通过{@link ISearchResult}获取其他结果
     * scrollId 游标id用于获取下一批数据的标记，第一次查询游标id为空
     * <p>
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param requestBuilder     查询条件
     * @param clz         文档类型
     * @param timeSeconds 游标id查询的有效时间（单位：分钟）
     * @param <T>         返回文档类型
     * @return SearchResult对象
     */
    <T> ISearchResult<T> scrollSearchByRequestBuilder(Object requestBuilder, Class<T> clz, Long timeSeconds);

    /**
     * 游标查询，通过{@link ISearchResult}获取其他结果
     * scrollId 游标id用于获取下一批数据的标记，第一次查询游标id为空
     * <p>
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param clz         文档类型
     * @param scrollId    游标id(第一次查询时游标id可为空)
     * @param timeSeconds 游标id查询的有效时间（单位：分钟）
     * @param <T>         返回文档类型
     * @return SearchResult对象
     */
    <T> ISearchResult<T> scrollSearchByScrollId(String scrollId, Class<T> clz, Long timeSeconds);

    /**
     * 游标查询，通过{@link ISearchResult}获取其他结果
     * scrollId 游标id用于获取下一批数据的标记，第一次查询游标id为空
     * <p>
     * es6       org.elasticsearch.search.builder.SearchSourceBuilder 作为入参
     * <p>
     * es7或es8  co.elastic.clients.elasticsearch.core.SearchRequest.Builder 作为入参
     *
     * @param clz         文档类型
     * @param scrollId    游标id
     * @param timeSeconds 游标id查询的有效时间（单位：分钟）
     * @param <T>         返回文档类型
     * @return SearchResult对象
     */
    default <T> List<T> scrollSearchList(String scrollId, Class<T> clz, Long timeSeconds) {
        return scrollSearchByScrollId(scrollId, clz, timeSeconds).asList();
    }
    
    /**
     * esMsearch,对按照请求返回多个结果
     *
     * @param requestBuilders 多个不同的请求build co.elastic.clients.elasticsearch.core.SearchRequest.Builder
     * @param clz             文档类型
     * @return 按照requestBuilders的顺序逐一包装的返回结果，如果某个request没有值，也会有一个空对象
     */
    <T> List<ISearchResult<T>> mSearch(Collection<?> requestBuilders, Class<T> clz);

}
