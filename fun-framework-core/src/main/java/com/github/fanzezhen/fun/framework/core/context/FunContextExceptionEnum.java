package com.github.fanzezhen.fun.framework.core.context;

import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import lombok.Getter;

/**
 * 异常枚举
 *
 * @author fanzezhen
 * @createTime 2024/1/11 10:22
 * @since 3
 */
@Getter
public enum FunContextExceptionEnum implements AbstractBaseExceptionEnum {

    CONTEXT_HEADER_MISSING(100200, "请求头中缺失参数 %s"),
    ;

    FunContextExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final int code;
    private final String message;

}
