package com.github.fanzezhen.fun.framework.core.context.properties;

import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * 上下文异常枚举类.
 * <p>
 * 定义上下文模块相关的异常码和异常消息。
 *
 * @since 3
 */
@Getter
public enum FunContextExceptionEnum implements IExceptionCode<FunContextExceptionEnum> {

    /**
     * 请求头中缺失参数异常.
     * <p>
     * 错误码：100200，消息格式："请求头中缺失参数 %s"
     */
    CONTEXT_HEADER_MISSING(100200, "请求头中缺失参数 %s"),
    ;

    /**
     * 错误码.
     */
    private final Integer code;
    /**
     * 错误消息.
     */
    private final String text;

    /**
     * 构造方法.
     *
     * @param exceptionCode 错误码
     * @param exceptionText 错误消息
     */
    FunContextExceptionEnum(final int exceptionCode,
                            final String exceptionText) {
        this.code = exceptionCode;
        this.text = exceptionText;
    }

}
