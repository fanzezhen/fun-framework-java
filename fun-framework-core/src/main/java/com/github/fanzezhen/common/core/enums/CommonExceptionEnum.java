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
public enum CommonExceptionEnum implements AbstractBaseExceptionEnum {

    ASYNC_ERROR(5000, "数据在被别人修改，请稍后重试"),
    ASYNC_ERROR_THREAD_TERMINATE_ABNORMALLY(404, "远程服务不存在");

    CommonExceptionEnum(int code, String message) {
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
