package com.github.fanzezhen.fun.framework.core.thread.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * 线程池模块异常枚举，定义线程执行相关的异常码和消息.
 *
 * @since 3
 */
@Getter
public enum FunCoreThreadExceptionEnum implements IExceptionCode<FunCoreThreadExceptionEnum> {

    /**
     * 异步任务执行异常.
     */
    ASYNC_ERROR_THREAD_TERMINATE_ABNORMALLY(100401,
            "异步错误，线程终止异常：%s");

    /**
     * 构造函数.
     *
     * @param codeValue 异常码
     * @param textValue 异常消息模板
     */
    FunCoreThreadExceptionEnum(final int codeValue, final String textValue) {
        this.code = codeValue;
        this.text = textValue;
    }

    /**
     * 异常码.
     */
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;

    /**
     * 异常消息模板.
     */
    private final String text;
}
