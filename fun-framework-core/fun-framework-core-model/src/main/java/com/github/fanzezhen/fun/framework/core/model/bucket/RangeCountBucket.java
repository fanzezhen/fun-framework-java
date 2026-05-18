package com.github.fanzezhen.fun.framework.core.model.bucket;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 范围桶
 * <p>
 * 继承 CountBucket，额外提供范围区间的起始值和结束值，适用于范围聚合统计场景。
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RangeCountBucket extends CountBucket {

    /**
     * 起始值
     */
    private Double from;

    /**
     * 结束值
     */
    private Double to;
}
