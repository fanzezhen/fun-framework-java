package com.github.fanzezhen.fun.framework.data.elasticsearch.base.model;

import com.github.fanzezhen.fun.framework.core.model.bucket.Bucket;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;
import lombok.Data;

import java.util.List;

@Data
@Aggregation
public class BucketAggregation {

    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<Bucket> bucketList;

}
