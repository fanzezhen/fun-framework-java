package com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公共字段
 */
@Getter
@AllArgsConstructor
public enum CommonElasticsearchFieldEnum implements IElasticsearchFieldEnum{

    NULL(""),
    VALUE("value"),
    VALUE_AS_STRING("value_as_string"),
    DOC_COUNT("doc_count"),
    ;

    private final String key;

}
