/**
 * Elasticsearch 反序列化器实现包
 * <p>
 * 本包提供响应结果反序列化器的具体实现。
 * </p>
 * <p>主要功能：</p>
 * <ul>
 *   <li>实现基于注解的聚合结果反序列化</li>
 *   <li>支持 @Aggregation 和 @Aggregations 注解</li>
 *   <li>提供字段反序列化器的统一调度</li>
 *   <li>支持多层嵌套聚合结果的反序列化</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.impl;
