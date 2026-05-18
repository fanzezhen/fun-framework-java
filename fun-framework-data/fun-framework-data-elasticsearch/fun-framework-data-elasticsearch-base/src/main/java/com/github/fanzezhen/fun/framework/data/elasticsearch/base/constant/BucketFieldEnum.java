package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 桶字段枚举
 * <p>
 * 定义 Elasticsearch 桶聚合可能返回的所有字段类型。
 * 用于 {@link com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.BucketField}
 * 注解中指定要提取的桶字段。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum BucketFieldEnum implements IElasticsearchFieldEnum {

    /**
     * 空值，表示未指定
     */
    NULL(""),

    /**
     * 文档数量
     */
    DOC_COUNT("doc_count"),

    /**
     * 桶键值
     */
    KEY("key"),

    /**
     * 桶键值的字符串表示
     */
    KEY_AS_STRING("key_as_string"),

    /**
     * 桶值
     */
    VALUE("value"),

    /**
     * 桶值的字符串表示
     */
    VALUE_AS_STRING("value_as_string"),

    /**
     * 范围起始值
     */
    FROM("from"),

    /**
     * 范围起始值的字符串表示
     */
    FROM_AS_STRING("from_as_string"),

    /**
     * 范围结束值
     */
    TO("to"),

    /**
     * 范围结束值的字符串表示
     */
    TO_AS_STRING("to_as_string"),
    ;

    /**
     * 字段键名
     */
    private final String key;

}
