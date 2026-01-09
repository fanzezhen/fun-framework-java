package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl;


import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IElasticsearchResultDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field.AggregationAnnotationSupportFieldDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field.AggregationFieldAnnotationSupportFieldDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field.BucketFieldAnnotationSupportFieldDeserializer;

/**
 */
public abstract class BaseAggregationResultDeserializer implements IElasticsearchResultDeserializer {

    protected final SupportFieldDeserializerResultDeserializer supportFieldResolveResultResolver;

    protected final AggregationAnnotationSupportFieldDeserializer aggregationsAnnotationSupportFieldResolver = new AggregationAnnotationSupportFieldDeserializer(this);

    protected final AggregationFieldAnnotationSupportFieldDeserializer aggregationAnnotationSupportFieldResolver = new AggregationFieldAnnotationSupportFieldDeserializer(this);

    protected final BucketFieldAnnotationSupportFieldDeserializer bucketAnnotationSupportFieldResolver = new BucketFieldAnnotationSupportFieldDeserializer(this);

    protected BaseAggregationResultDeserializer(SupportFieldDeserializerResultDeserializer supportFieldResolveResultResolver) {
        this.supportFieldResolveResultResolver = supportFieldResolveResultResolver;
    }

    public SupportFieldDeserializerResultDeserializer getSupportFieldResolveResultResolver() {
        return supportFieldResolveResultResolver;
    }

    public AggregationAnnotationSupportFieldDeserializer getAggregationAnnotationSupportFieldResolverInstance() {
        return aggregationsAnnotationSupportFieldResolver;
    }

    public AggregationFieldAnnotationSupportFieldDeserializer getAggregationFieldAnnotationSupportFieldResolverInstance() {
        return aggregationAnnotationSupportFieldResolver;
    }

    public BucketFieldAnnotationSupportFieldDeserializer getBucketFieldAnnotationSupportFieldResolverInstance() {
        return bucketAnnotationSupportFieldResolver;
    }

}
