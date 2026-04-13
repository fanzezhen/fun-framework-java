package com.github.fanzezhen.fun.framework.core.model.bucket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CountBucket implements IBucket{

    /**
     * 键
     */
    private String key;

    /**
     * 数量
     */
    private Long docCount;

    @Override
    public Long getNumber() {
        return docCount;
    }
}
