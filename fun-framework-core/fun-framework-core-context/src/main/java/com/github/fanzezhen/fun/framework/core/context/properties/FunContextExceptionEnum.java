package com.github.fanzezhen.fun.framework.core.context.properties;

import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * 异常枚举
 *
 * @author fanzezhen
 * @since 3
 */
@Getter
public enum FunContextExceptionEnum implements IExceptionCode<FunContextExceptionEnum> {

    // 定义了一个枚举常量，表示请求头中缺失参数的异常，错误码为100200，消息格式为"请求头中缺失参数 %s"
    CONTEXT_HEADER_MISSING(100200, "请求头中缺失参数 %s"),
    ;

    // 构造方法，用于初始化枚举常量的code和message属性
    FunContextExceptionEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    // 定义了code属性，表示错误码
    private final Integer code;
    // 定义了message属性，表示错误消息，并使用@Getter注解生成getter方法
    private final String text;

}
