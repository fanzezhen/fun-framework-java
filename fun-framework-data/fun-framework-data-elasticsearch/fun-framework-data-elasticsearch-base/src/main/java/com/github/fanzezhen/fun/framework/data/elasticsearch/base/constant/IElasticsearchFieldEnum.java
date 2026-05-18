package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

/**
 * Elasticsearch 字段枚举接口
 * <p>
 * 定义 Elasticsearch 字段枚举的通用接口，提供元数据字段常量和键名获取方法。
 * </p>
 */
public interface IElasticsearchFieldEnum {

    /**
     * Elasticsearch 元数据字段列表
     */
    String[] META_FIELDS = new String[]{"_id", "_ignored", "_index", "_routing", "_size", "_timestamp", "_ttl", "_type"};

    /**
     * 获取字段键名
     *
     * @return 字段的键名
     */
    String getKey();
}
