package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Bucket可能返回的所有字段
 */
@Getter
@AllArgsConstructor
public enum BucketFieldEnum implements IElasticsearchFieldEnum{

    NULL(""),
    DOC_COUNT("doc_count"),
    KEY("key"),
    KEY_AS_STRING("key_as_string"),
    VALUE("value"),
    VALUE_AS_STRING("value_as_string"),
    FROM("from"),
    FROM_AS_STRING("from_as_string"),
    TO("to"),
    TO_AS_STRING("to_as_string"),
    ;

    private final String key;

}
