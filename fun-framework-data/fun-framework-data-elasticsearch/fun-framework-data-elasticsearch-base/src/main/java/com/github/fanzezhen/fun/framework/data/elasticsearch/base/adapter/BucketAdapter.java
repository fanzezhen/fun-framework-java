package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.BucketFieldEnum;

public interface BucketAdapter extends IAggregationsAdapter {

    int getInt(BucketFieldEnum aggregationField);

    long getLong(BucketFieldEnum aggregationField);

    String getString(BucketFieldEnum aggregationField);

    double getDouble(BucketFieldEnum aggregationField);

    IAggregationAdapter getAggregation(String aggregationName);

    <T> T get(String key, Class<T> tClass);

    <T> T get(BucketFieldEnum bucketField, Class<T> tClass);


}
