package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

/**
 *
 */
public interface IElasticsearchFieldEnum {
    String[] META_FIELDS = new String[]{"_id", "_ignored", "_index", "_routing", "_size", "_timestamp", "_ttl", "_type"};
    String getKey();
}
