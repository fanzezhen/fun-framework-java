package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;

public interface IAggregationsAdapter {

    /**
     * 通过名字获取聚合
     */
    IAggregationAdapter getAggregation(String name);

}
