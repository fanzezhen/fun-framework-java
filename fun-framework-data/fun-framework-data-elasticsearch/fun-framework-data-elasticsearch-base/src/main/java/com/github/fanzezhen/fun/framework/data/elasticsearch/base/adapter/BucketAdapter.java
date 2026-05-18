package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;

import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.BucketFieldEnum;

/**
 * 桶适配器接口
 * <p>
 * 用于适配 Elasticsearch 聚合结果中的桶数据，提供对桶字段和嵌套聚合的访问
 */
public interface BucketAdapter extends IAggregationsAdapter {

    /**
     * 获取整型字段值
     *
     * @param aggregationField 聚合字段枚举
     * @return 整型值
     */
    int getInt(final BucketFieldEnum aggregationField);

    /**
     * 获取长整型字段值
     *
     * @param aggregationField 聚合字段枚举
     * @return 长整型值
     */
    long getLong(final BucketFieldEnum aggregationField);

    /**
     * 获取字符串字段值
     *
     * @param aggregationField 聚合字段枚举
     * @return 字符串值
     */
    String getString(final BucketFieldEnum aggregationField);

    /**
     * 获取双精度浮点型字段值
     *
     * @param aggregationField 聚合字段枚举
     * @return 双精度浮点型值
     */
    double getDouble(final BucketFieldEnum aggregationField);

    /**
     * 根据名称获取嵌套聚合
     *
     * @param aggregationName 聚合名称
     * @return 聚合适配器
     */
    IAggregationAdapter getAggregation(final String aggregationName);

    /**
     * 获取桶的 JSON 对象
     *
     * @return 桶的 JSON 对象
     */
    JSONObject getBucketJson();

    /**
     * 根据键获取字段值
     *
     * @param key    字段键
     * @param tClass 目标类型
     * @param <T>    泛型类型
     * @return 字段值
     */
    <T> T get(final String key, final Class<T> tClass);

    /**
     * 根据桶字段枚举获取字段值
     *
     * @param bucketField 桶字段枚举
     * @param tClass      目标类型
     * @param <T>         泛型类型
     * @return 字段值
     */
    <T> T get(final BucketFieldEnum bucketField, final Class<T> tClass);


}
