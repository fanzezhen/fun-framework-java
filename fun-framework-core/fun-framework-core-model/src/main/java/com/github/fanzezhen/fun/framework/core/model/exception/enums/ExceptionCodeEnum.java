package com.github.fanzezhen.fun.framework.core.model.exception.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 异常码枚举
 * <p>
 * 定义系统常用的异常码和对应的错误描述。
 * </p>
 */
@Getter
public enum ExceptionCodeEnum implements IExceptionCode<ExceptionCodeEnum> {
    /**
     * 资源不存在（404）
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 服务异常（500）
     */
    SERVICE_ERROR(500, "服务异常"),

    /**
     * 文件不存在（1404）
     */
    FILE_NOT_FOUND(1404, "文件不存在"),
    ;

    /**
     * 异常码
     */
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;

    /**
     * 异常文本说明
     */
    private final String text;

    /**
     * 构造异常码枚举
     *
     * @param code 异常码
     * @param text 异常文本说明
     */
    ExceptionCodeEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 获取异常码整数值
     *
     * @return 异常码
     */
    @Override
    public int intVal() {
        return code;
    }

    /**
     * 获取异常文本说明
     *
     * @return 异常文本
     */
    @Override
    public String text() {
        return text;
    }
}
