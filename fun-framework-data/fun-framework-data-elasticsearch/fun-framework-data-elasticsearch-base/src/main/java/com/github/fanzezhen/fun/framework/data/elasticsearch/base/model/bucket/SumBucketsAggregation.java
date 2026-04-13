package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.bucket;

import com.github.fanzezhen.fun.framework.core.model.bucket.IBucketsAggregation;
import com.github.fanzezhen.fun.framework.core.model.bucket.SumBucket;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;
import lombok.Data;

import java.util.List;

@Data
@Aggregation
public class SumBucketsAggregation implements IBucketsAggregation {

    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<SumBucket> bucketList;

}
