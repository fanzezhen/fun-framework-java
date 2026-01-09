package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;


import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;

import java.util.List;

public interface IAggregationAdapter extends IAggregationsAdapter {

    List<BucketAdapter> getBuckets();

    IHitsAdapter getHits();

    String getName();

    String getType();

    <T> T get(String key, Class<T> tClass);

    <T> T get(AggregationFieldEnum aggregationField, Class<T> tClass);

}
