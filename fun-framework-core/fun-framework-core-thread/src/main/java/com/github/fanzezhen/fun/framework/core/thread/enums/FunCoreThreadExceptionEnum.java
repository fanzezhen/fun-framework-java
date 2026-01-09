package com.github.fanzezhen.fun.framework.core.thread.enums;

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
public enum FunCoreThreadExceptionEnum implements IExceptionCode<FunCoreThreadExceptionEnum> {

    ASYNC_ERROR_THREAD_TERMINATE_ABNORMALLY(100401, "异步错误，线程终止异常：%s");

    FunCoreThreadExceptionEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;
    private final String text;
}
