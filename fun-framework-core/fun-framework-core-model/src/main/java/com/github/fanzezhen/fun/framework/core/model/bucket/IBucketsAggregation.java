package com.github.fanzezhen.fun.framework.core.model.bucket;

import java.util.List;

/**
 * 桶聚合接口
 * <p>
 * 定义聚合结果集合的数据结构，用于获取包含多个桶的列表。
 * </p>
 */
public interface IBucketsAggregation {

    /**
     * 获取桶列表
     * <p>
     * 返回聚合查询结果中所有桶的集合
     * </p>
     *
     * @return 桶列表
     */
    List<? extends IBucket> getBucketList();
}
