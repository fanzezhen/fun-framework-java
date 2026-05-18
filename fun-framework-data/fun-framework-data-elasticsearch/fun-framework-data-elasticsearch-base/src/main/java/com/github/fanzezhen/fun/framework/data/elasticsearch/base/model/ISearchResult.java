package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter.IResponseAdapter;

import java.util.List;


/**
 * Elasticsearch 搜索结果接口
 * <p>
 * 封装 Elasticsearch 查询结果，提供文档、聚合、分页等多种访问方式
 *
 * @param <T> 文档类型
 */
public interface ISearchResult<T> {

    /**
     * 转换为分页对象
     *
     * @param currentPage 当前页码
     * @param pageSize    每页大小
     * @return 分页数据对象
     */
    PageDTO<T> asPageResult(final int currentPage, final int pageSize);

    /**
     * 转换为文档列表
     *
     * @return 文档列表
     */
    List<T> asDocumentList();

    /**
     * 将搜索内容转换为指定类型的列表
     * <p>
     * 如果是聚合结果，需要在 vClass 上添加 {@link Aggregation} 注解
     *
     * @param vClass 搜索结果映射类
     * @param <V>    泛型类型
     * @return 对象列表
     */
    <V> List<V> asList(final Class<V> vClass);

    /**
     * 将聚合内容转换为对象
     * <p>
     * 需要在类型 T 上添加 {@link Aggregation} 注解
     *
     * @return 完整的聚合对象，对应 Elasticsearch 返回值中的 aggregations 字段
     */
    T asAggregations();

    /**
     * 转换为单个文档
     *
     * @return 单个文档对象
     */
    T asDocument();

    /**
     * 获取总命中数
     *
     * @return 总命中数
     */
    long getTotalHits();

    /**
     * 获取查询总耗时
     *
     * @return 耗时（毫秒）
     */
    double getTotalTime();

    /**
     * 获取游标 ID
     * <p>
     * 用于滚动查询
     *
     * @return 游标 ID
     */
    String getScrollId();

    /**
     * 将搜索内容转换为列表
     * <p>
     * 默认实现返回文档列表。如果是聚合结果，需要在类型 T 上添加 {@link Aggregation} 注解
     *
     * @return 对象列表
     */
    default List<T> asList(){
        return asDocumentList();
    }

    /**
     * 获取响应适配器
     *
     * @return 响应适配器
     */
    IResponseAdapter getResponseAdapter();

}
