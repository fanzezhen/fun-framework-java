package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.bucket;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.core.model.bucket.CountBucket;
import com.github.fanzezhen.fun.framework.core.model.constant.StrConstant;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.BucketField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 *
 */
@Data
@Accessors(chain = true)
public class HitsCountBucket<T> extends CountBucket {
    @BucketField(aggregationName = StrConstant.RECORDS)
    @AggregationField(AggregationFieldEnum.HITS)
    private List<T> hitList;

    public HitsCountBucket(String key, Long docCount) {
        super(key, docCount);
    }
    public T getHit() {
        return CollUtil.isNotEmpty(hitList)?hitList.getFirst():null;
    }
}
