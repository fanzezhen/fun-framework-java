package com.github.fanzezhen.fun.framework.core.model.bucket;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *范围桶
 */
@Data
@Accessors(chain = true)
public class RangeBucket extends Bucket{

    /**
     * 起始值
     */
    private Double from;

    /**
     * 结束值
     */
    private Double to;
}
