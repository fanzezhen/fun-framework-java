package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公共字段枚举
 * <p>
 * 定义 Elasticsearch 查询和聚合中常用的公共字段。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum CommonElasticsearchFieldEnum implements IElasticsearchFieldEnum {

    /**
     * 空值，表示未指定
     */
    NULL(""),

    /**
     * 值
     */
    VALUE("value"),

    /**
     * 值的字符串表示
     */
    VALUE_AS_STRING("value_as_string"),

    /**
     * 文档数量
     */
    DOC_COUNT("doc_count"),
    ;

    /**
     * 字段键名
     */
    private final String key;

}
