package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.core.model.result.PageResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;

import java.util.List;


/**
 * 封装ES查询结果
 *
 */
public interface ISearchResult<T> {
    
    /**
     * 转换为分页对象
     */
    PageResult<T> asPageResult(Long currentPage, Long pageSize);

    /**
     * 转换为list
     */
    List<T> asDocumentList();

    /**
     * 将搜索内容转换为list
     * <p>
     *
     * @param vClass 搜索结果映射类
     *               <p>
     *               如果是聚合，需要在 vClass 上加 {@link Aggregation}
     */
    <V> List<V> asList(Class<V> vClass);

    /**
     * 将聚合内容转化为对象
     * <p>
     * 需要在 tClass 上加 {@link Aggregation}
     *
     * @return 完整的聚合对象，对应es返回值中的 aggregations 字段
     */
    T asAggregations();

    /**
     * 转换为单个文档
     */
    T asDocument();

    /**
     * 总数量
     */
    long getTotalHits();

    /**
     * 总耗时
     */
    double getTotalTime();

    /**
     * 获取游标id
     */
    String getScrollId();

    /**
     * 将搜索内容转化为list
     * <p>
     * 如果是聚合，需要在 tClass 上加 {@link Aggregation}
     */
    default List<T> asList(){
        return asDocumentList();
    }
    IResponseAdapter getResponseAdapter();

}
