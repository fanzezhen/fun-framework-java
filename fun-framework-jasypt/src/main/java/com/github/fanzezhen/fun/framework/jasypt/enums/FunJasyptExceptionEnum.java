package com.github.fanzezhen.fun.framework.jasypt.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * Jasypt 模块异常码枚举。
 * <p>
 * 定义加密解密过程中可能出现的异常情况，包括密钥缺失和密钥生成失败等。
 *
 * @since 3
 */
@Getter
public enum FunJasyptExceptionEnum implements IExceptionCode<FunJasyptExceptionEnum> {

    /**
     * 私钥缺失异常。
     */
    PRIVATE_KEY_MISSING(100601, "私钥缺失"),

    /**
     * 私钥生成失败异常。
     */
    PRIVATE_KEY_GENERATE_FAILED(100602, "私钥生成失败"),

    /**
     * 公钥缺失异常。
     */
    PUBLIC_KEY_MISSING(100603, "公钥缺失"),

    /**
     * 公钥生成失败异常。
     */
    PUBLIC_KEY_GENERATE_FAILED(100604, "公钥生成失败"),
    ;

    /**
     * 构造异常枚举。
     *
     * @param code 异常码
     * @param text 异常描述
     */
    FunJasyptExceptionEnum(final int code, final String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 异常码。
     */
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;

    /**
     * 异常描述文本。
     */
    private final String text;
}
