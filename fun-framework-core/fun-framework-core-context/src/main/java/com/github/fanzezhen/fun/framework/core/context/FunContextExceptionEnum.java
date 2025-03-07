package com.github.fanzezhen.fun.framework.core.context;

import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import lombok.Getter;

/**
 * 异常枚举
 *
 * @author fanzezhen
 * @since 3
 */
public enum FunContextExceptionEnum implements AbstractBaseExceptionEnum {

    // 定义了一个枚举常量，表示请求头中缺失参数的异常，错误码为100200，消息格式为"请求头中缺失参数 %s"
    CONTEXT_HEADER_MISSING(100200, "请求头中缺失参数 %s"),
    ;

    // 构造方法，用于初始化枚举常量的code和message属性
    FunContextExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // 定义了code属性，表示错误码
    private final int code;
    // 定义了message属性，表示错误消息，并使用@Getter注解生成getter方法
    @Getter
    private final String message;

    // 实现AbstractBaseExceptionEnum接口中的getCode方法，返回错误码
    public Integer getCode() {
        return code;
    }
}
