package com.github.fanzezhen.fun.framework.core.model.bucket;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 */
@Data
@Accessors(chain = true)
public class Bucket {

    /**
     * 数量
     */
    private Long docCount;

    /**
     * 名称
     */
    private String key;
}
