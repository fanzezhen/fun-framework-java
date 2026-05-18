package com.github.fanzezhen.fun.framework.core.model.bucket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 计数桶
 * <p>
 * 用于存储聚合统计结果中的计数信息，包含键和文档数量。
 * </p>
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
     * 文档计数
     */
    private Long docCount;

    /**
     * 获取桶的数值
     * <p>
     * 返回该桶关联的数值统计结果，如计数、求和等
     * </p>
     *
     * @return 桶的数值
     */
    @Override
    public Long getNumber() {
        return docCount;
    }
}
