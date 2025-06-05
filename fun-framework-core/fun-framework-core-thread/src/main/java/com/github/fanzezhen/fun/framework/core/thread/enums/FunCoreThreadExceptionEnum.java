package com.github.fanzezhen.fun.framework.core.thread.enums;

import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 异常枚举
 *
 * @author fanzezhen
 * @since 3
 */
public enum FunCoreThreadExceptionEnum implements AbstractBaseExceptionEnum {

    ASYNC_ERROR_THREAD_TERMINATE_ABNORMALLY(100101, "异步错误，线程终止异常：%s");

    FunCoreThreadExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final int code;
    @Getter
    private final String message;

    @Override
    public Integer getCode() {
        return code;
    }
}
