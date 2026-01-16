package com.github.fanzezhen.fun.framework.core.model.exception;

import cn.hutool.core.util.ArrayUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.ExceptionCodeEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;

/**
 * 业务异常的封装
 */
public class ServiceException extends RuntimeException {
    private final IExceptionCode<?> exceptionCode;

    /**
     * 创建异常
     */
    public ServiceException(String message) {
        super(message);
        this.exceptionCode = ExceptionCodeEnum.SERVICE_ERROR;
    }

    /**
     * 创建异常
     */
    public ServiceException(String message, Throwable e) {
        super(message, e);
        this.exceptionCode = ExceptionCodeEnum.SERVICE_ERROR;
    }

    /**
     * 创建异常
     *
     * @param exceptionCode 错误码
     * @param params        参数
     */
    public ServiceException(IExceptionCode<?> exceptionCode, Object... params) {
        super(ArrayUtil.isNotEmpty(params) ? String.format(exceptionCode.text(), params) : exceptionCode.text());
        this.exceptionCode = exceptionCode;
    }

    /**
     * 创建异常
     */
    public ServiceException(Throwable e, IExceptionCode<?> exceptionCode, Object... params) {
        super(ArrayUtil.isNotEmpty(params) ? String.format(exceptionCode.text(), params) : exceptionCode.text(), e);
        this.exceptionCode = exceptionCode;
    }

    /**
     * 错误码
     */
    public Integer getCode() {
        return exceptionCode.intVal();
    }
}
