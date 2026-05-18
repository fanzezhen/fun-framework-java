package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl;


import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IElasticsearchResultDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field.AggregationAnnotationSupportFieldDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field.AggregationFieldAnnotationSupportFieldDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field.BucketFieldAnnotationSupportFieldDeserializer;

/**
 * 基础聚合结果反序列化器抽象类
 * <p>
 * 提供聚合结果反序列化的基础功能，包含各种字段反序列化器的实例和访问方法
 */
public abstract class BaseAggregationResultDeserializer implements IElasticsearchResultDeserializer {

    /**
     * 支持字段反序列化的结果反序列化器
     */
    protected final SupportFieldDeserializerResultDeserializer supportFieldResolveResultResolver;

    /**
     * 聚合注解支持字段反序列化器
     */
    protected final AggregationAnnotationSupportFieldDeserializer aggregationsAnnotationSupportFieldResolver =
            new AggregationAnnotationSupportFieldDeserializer(this);

    /**
     * 聚合字段注解支持字段反序列化器
     */
    protected final AggregationFieldAnnotationSupportFieldDeserializer aggregationAnnotationSupportFieldResolver =
            new AggregationFieldAnnotationSupportFieldDeserializer(this);

    /**
     * 桶字段注解支持字段反序列化器
     */
    protected final BucketFieldAnnotationSupportFieldDeserializer bucketAnnotationSupportFieldResolver =
            new BucketFieldAnnotationSupportFieldDeserializer(this);

    /**
     * 构造函数
     *
     * @param supportFieldResolveResultResolver 支持字段反序列化的结果反序列化器
     */
    protected BaseAggregationResultDeserializer(final SupportFieldDeserializerResultDeserializer supportFieldResolveResultResolver) {
        this.supportFieldResolveResultResolver = supportFieldResolveResultResolver;
    }

    /**
     * 获取支持字段反序列化的结果反序列化器
     *
     * @return 支持字段反序列化的结果反序列化器
     */
    public SupportFieldDeserializerResultDeserializer getSupportFieldResolveResultResolver() {
        return supportFieldResolveResultResolver;
    }

    /**
     * 获取聚合注解支持字段反序列化器实例
     *
     * @return 聚合注解支持字段反序列化器
     */
    public AggregationAnnotationSupportFieldDeserializer getAggregationAnnotationSupportFieldResolverInstance() {
        return aggregationsAnnotationSupportFieldResolver;
    }

    /**
     * 获取聚合字段注解支持字段反序列化器实例
     *
     * @return 聚合字段注解支持字段反序列化器
     */
    public AggregationFieldAnnotationSupportFieldDeserializer getAggregationFieldAnnotationSupportFieldResolverInstance() {
        return aggregationAnnotationSupportFieldResolver;
    }

    /**
     * 获取桶字段注解支持字段反序列化器实例
     *
     * @return 桶字段注解支持字段反序列化器
     */
    public BucketFieldAnnotationSupportFieldDeserializer getBucketFieldAnnotationSupportFieldResolverInstance() {
        return bucketAnnotationSupportFieldResolver;
    }

}
