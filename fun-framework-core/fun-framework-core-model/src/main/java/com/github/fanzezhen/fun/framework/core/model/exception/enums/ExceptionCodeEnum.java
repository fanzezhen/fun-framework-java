package com.github.fanzezhen.fun.framework.core.model.exception.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 异常码
 *
 * @author fanzezhen
 */
@Getter
public enum ExceptionCodeEnum implements IExceptionCode<ExceptionCodeEnum> {
    NOT_FOUND(404, "资源不存在"),
    SERVICE_ERROR(500, "服务异常"),
    FILE_NOT_FOUND(1404, "文件不存在"),
    ;

    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;
    private final String text;

    ExceptionCodeEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 枚举项编号
     */
    @Override
    public int intVal() {
        return code;
    }

    /**
     * 在中文语境下配合一个中文说明
     */
    @Override
    public String text() {
        return text;
    }
}
