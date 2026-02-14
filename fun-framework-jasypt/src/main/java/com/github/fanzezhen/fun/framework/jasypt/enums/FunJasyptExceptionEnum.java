package com.github.fanzezhen.fun.framework.jasypt.enums;

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
public enum FunJasyptExceptionEnum implements IExceptionCode<FunJasyptExceptionEnum> {

    PRIVATE_KEY_MISSING(100601, "私钥缺失"),
    PRIVATE_KEY_GENERATE_FAILED(100602, "私钥生成失败"),
    PUBLIC_KEY_MISSING(100603, "公钥缺失"),
    PUBLIC_KEY_GENERATE_FAILED(100604, "公钥生成失败"),
    ;

    FunJasyptExceptionEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;
    private final String text;
}
