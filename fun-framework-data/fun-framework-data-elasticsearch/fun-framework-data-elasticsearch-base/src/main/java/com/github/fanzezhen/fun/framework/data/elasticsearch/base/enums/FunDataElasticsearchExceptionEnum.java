package com.github.fanzezhen.fun.framework.data.elasticsearch.base.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * 异常枚举
 *
 * @author fanzezhen
 * @since 3
 */
@Getter
public enum FunDataElasticsearchExceptionEnum implements IExceptionCode<FunDataElasticsearchExceptionEnum> {

    RESPONSE_DESERIALIZER_ERROR(120000, "Elasticsearch响应数据反序列化解析器异常，相应类型 %s 的解析器 %s %s");

    FunDataElasticsearchExceptionEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;
    private final String text;
}
