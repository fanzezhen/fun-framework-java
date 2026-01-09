package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Aggregation 可能返回的所有字段
 */
@Getter
@AllArgsConstructor
public enum AggregationFieldEnum {

    NULL(""),
    VALUE("value"),
    VALUE_AS_STRING("value_as_string"),
    BUCKETS("buckets"),
    SUM_OTHER_DOC_COUNT("sum_other_doc_count"),
    HITS("hits"),
    DOC_COUNT("doc_count"),
    ;


    private final String key;

}
