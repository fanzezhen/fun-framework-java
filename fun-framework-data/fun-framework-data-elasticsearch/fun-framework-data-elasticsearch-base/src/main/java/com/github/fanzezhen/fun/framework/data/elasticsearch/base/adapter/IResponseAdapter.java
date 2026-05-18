package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;

/**
 * Elasticsearch 响应适配器接口
 * <p>
 * 用于适配 Elasticsearch 的响应结果，提供对聚合和命中记录的统一访问
 */
public interface IResponseAdapter {

    /**
     * 获取聚合集合适配器
     *
     * @return 聚合集合适配器
     */
    IAggregationsAdapter getAggregationsAdapter();

    /**
     * 获取命中记录集合适配器
     *
     * @return 命中记录集合适配器
     */
    IHitsAdapter getHitsAdapter();

}
