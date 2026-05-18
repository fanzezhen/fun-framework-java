package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.bucket;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.core.model.bucket.CountBucket;
import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.BucketField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 带命中记录的计数桶
 * <p>
 * 扩展计数桶，添加命中记录列表，用于在聚合结果中包含具体的文档数据
 *
 * @param <T> 命中记录类型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class HitsCountBucket<T> extends CountBucket {
    /**
     * 命中记录列表
     */
    @BucketField(aggregationName = NormalTypeConstant.STR_RECORDS)
    @AggregationField(AggregationFieldEnum.HITS)
    private List<T> hitList;

    /**
     * 构造函数
     *
     * @param key      桶键
     * @param docCount 文档计数
     */
    public HitsCountBucket(final String key, final Long docCount) {
        super(key, docCount);
    }

    /**
     * 获取第一条命中记录
     *
     * @return 命中记录，如果列表为空则返回 null
     */
    public T getHit() {
        return CollUtil.isNotEmpty(hitList)?hitList.getFirst():null;
    }
}
