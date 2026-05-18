package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;

/**
 * 聚合集合适配器接口
 * <p>
 * 用于适配 Elasticsearch 响应中的聚合集合，支持通过名称获取单个聚合
 */
public interface IAggregationsAdapter {

    /**
     * 通过名字获取聚合
     *
     * @param name 聚合名称
     * @return 聚合适配器
     */
    IAggregationAdapter getAggregation(final String name);

}
