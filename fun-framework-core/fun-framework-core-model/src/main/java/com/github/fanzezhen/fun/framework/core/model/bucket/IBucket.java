package com.github.fanzezhen.fun.framework.core.model.bucket;

/**
 * 桶接口
 * <p>
 * 定义聚合桶的基本结构，用于存储分组统计的键和数值信息。
 * </p>
 */
public interface IBucket {

    /**
     * 获取桶的键值
     * <p>
     * 用于标识分组的键，如统计维度、分类名称等
     * </p>
     *
     * @return 桶的键值
     */
     String getKey();

    /**
     * 获取桶的数值
     * <p>
     * 返回该桶关联的数值统计结果，如计数、求和等
     * </p>
     *
     * @return 桶的数值
     */
     Long getNumber();
}
