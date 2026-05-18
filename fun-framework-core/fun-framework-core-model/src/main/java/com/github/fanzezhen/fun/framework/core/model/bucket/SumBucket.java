package com.github.fanzezhen.fun.framework.core.model.bucket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 求和桶
 * <p>
 * 用于存储聚合统计结果中的求和信息，包含键和文档求和值。
 * </p>
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SumBucket implements IBucket{

    /**
     * 键
     */
    private String key;

    /**
     * 文档求和值
     */
    private Long docSum;

    /**
     * 获取求和值
     *
     * @return 文档求和值
     */
    @Override
    public Long getNumber() {
        return docSum;
    }
}
