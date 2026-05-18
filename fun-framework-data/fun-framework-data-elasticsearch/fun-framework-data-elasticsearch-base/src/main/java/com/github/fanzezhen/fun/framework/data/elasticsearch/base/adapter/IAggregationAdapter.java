package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;


import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;

import java.util.List;

/**
 * 聚合适配器接口
 * <p>
 * 用于适配 Elasticsearch 响应中的单个聚合结果，支持获取桶、命中记录等信息
 */
public interface IAggregationAdapter extends IAggregationsAdapter {

    /**
     * 获取聚合的桶列表
     *
     * @return 桶适配器列表
     */
    List<BucketAdapter> getBuckets();

    /**
     * 获取聚合的命中记录适配器
     *
     * @return 命中记录适配器
     */
    IHitsAdapter getHits();

    /**
     * 获取聚合名称
     *
     * @return 聚合名称
     */
    String getName();

    /**
     * 获取聚合类型
     *
     * @return 聚合类型
     */
    String getType();

    /**
     * 根据键获取聚合字段值
     *
     * @param key    字段键
     * @param tClass 目标类型
     * @param <T>    泛型类型
     * @return 字段值
     */
    <T> T get(final String key, final Class<T> tClass);

    /**
     * 根据聚合字段枚举获取字段值
     *
     * @param aggregationField 聚合字段枚举
     * @param tClass           目标类型
     * @param <T>              泛型类型
     * @return 字段值
     */
    <T> T get(final AggregationFieldEnum aggregationField, final Class<T> tClass);

}
