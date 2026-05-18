/**
 * Elasticsearch 字段反序列化器包
 * <p>
 * 本包提供字段级别的反序列化器实现。
 * </p>
 * <p>主要功能：</p>
 * <ul>
 *   <li>提供字段反序列化的抽象基类和接口</li>
 *   <li>支持 @EsId、@EsProperty、@HighlightField 等注解的字段反序列化</li>
 *   <li>支持聚合字段和桶字段的反序列化</li>
 *   <li>可扩展的字段反序列化机制</li>
 * </ul>
 */
package com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.field;
