package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;

/**
 * es结果适配器
 */
public interface IResponseAdapter {

    /**
     * 获取聚合
     */
    IAggregationsAdapter getAggregationsAdapter();

    /**
     * 获取hits
     */
    IHitsAdapter getHitsAdapter();

}
