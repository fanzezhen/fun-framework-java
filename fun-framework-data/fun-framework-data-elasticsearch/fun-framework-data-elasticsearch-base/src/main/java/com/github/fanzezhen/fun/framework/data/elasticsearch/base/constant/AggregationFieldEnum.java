package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 聚合字段枚举
 * <p>
 * 定义 Elasticsearch 聚合查询可能返回的所有字段类型。
 * 用于 {@link com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField}
 * 注解中指定要提取的聚合字段。
 * </p>
 */
@Getter
@AllArgsConstructor
public enum AggregationFieldEnum {

    /**
     * 空值，表示未指定
     */
    NULL(""),

    /**
     * 聚合值
     */
    VALUE("value"),

    /**
     * 聚合值的字符串表示
     */
    VALUE_AS_STRING("value_as_string"),

    /**
     * 桶列表
     */
    BUCKETS("buckets"),

    /**
     * 其他文档数量总和
     */
    SUM_OTHER_DOC_COUNT("sum_other_doc_count"),

    /**
     * 命中文档
     */
    HITS("hits"),

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
