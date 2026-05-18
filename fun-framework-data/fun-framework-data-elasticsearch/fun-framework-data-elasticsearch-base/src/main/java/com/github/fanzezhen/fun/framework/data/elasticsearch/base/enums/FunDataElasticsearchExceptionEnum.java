package com.github.fanzezhen.fun.framework.data.elasticsearch.base.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * Elasticsearch 模块异常枚举
 * <p>
 * 定义 Elasticsearch 模块中可能出现的异常类型和错误码。
 * </p>
 *
 * @since 3
 */
@Getter
public enum FunDataElasticsearchExceptionEnum implements IExceptionCode<FunDataElasticsearchExceptionEnum> {

    /**
     * 响应反序列化器错误
     * <p>
     * 当找不到合适的反序列化器或反序列化过程出错时抛出此异常。
     * </p>
     * <p>错误信息参数：</p>
     * <ol>
     *   <li>响应类型</li>
     *   <li>解析器信息</li>
     *   <li>详细错误信息</li>
     * </ol>
     */
    RESPONSE_DESERIALIZER_ERROR(120000, "Elasticsearch响应数据反序列化解析器异常，相应类型 %s 的解析器 %s %s");

    /**
     * 构造方法
     *
     * @param code 错误码
     * @param text 错误信息模板
     */
    FunDataElasticsearchExceptionEnum(final int code, final String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 错误码
     */
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;

    /**
     * 错误信息模板
     */
    private final String text;
}
