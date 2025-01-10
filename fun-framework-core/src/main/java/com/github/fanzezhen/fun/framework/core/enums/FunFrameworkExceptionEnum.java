package com.github.fanzezhen.fun.framework.core.enums;

import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import lombok.Getter;

/**
 * 异常枚举
 *
 * @author fanzezhen
 * @createTime 2024/1/11 10:22
 * @since 3
 */
public enum FunFrameworkExceptionEnum implements AbstractBaseExceptionEnum {

    ASYNC_ERROR(100100, "数据在被别人修改，请稍后重试"),
    ASYNC_ERROR_THREAD_TERMINATE_ABNORMALLY(100101, "异步错误，线程终止异常：%s");

    FunFrameworkExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final int code;
    @Getter
    private final String message;

    @Override
    public Integer getCode() {
        return code;
    }
}
