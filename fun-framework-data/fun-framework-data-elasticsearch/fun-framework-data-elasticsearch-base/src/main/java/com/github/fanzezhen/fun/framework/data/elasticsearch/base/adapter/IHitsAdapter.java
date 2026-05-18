package com.github.fanzezhen.fun.framework.data.elasticsearch.base.adapter;

import java.util.List;

/**
 * 命中记录集合适配器接口
 * <p>
 * 用于适配 Elasticsearch 响应中的命中记录集合，包含总数、最大得分和命中记录列表
 */
public interface IHitsAdapter {

    /**
     * 获取总命中数
     *
     * @return 命中总数
     */
    long getTotal();

    /**
     * 获取最大得分
     *
     * @return 最大得分
     */
    double getMaxScore();

    /**
     * 获取命中记录列表
     *
     * @return 命中记录列表
     */
    List<? extends IHit> getHitList();

}
