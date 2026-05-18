package com.github.fanzezhen.fun.framework.core.model.exception;

import cn.hutool.core.util.ArrayUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.ExceptionCodeEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;

/**
 * 业务异常
 * <p>
 * 封装业务层抛出的异常，包含异常码和错误信息，支持参数化消息格式。
 * </p>
 */
public class ServiceException extends RuntimeException {
    /**
     * 异常码
     */
    private final IExceptionCode<?> exceptionCode;

    /**
     * 创建业务异常
     * <p>
     * 使用默认异常码 SERVICE_ERROR(500)
     * </p>
     *
     * @param message 异常消息
     */
    public ServiceException(String message) {
        super(message);
        this.exceptionCode = ExceptionCodeEnum.SERVICE_ERROR;
    }

    /**
     * 创建业务异常并包装原始异常
     * <p>
     * 使用默认异常码 SERVICE_ERROR(500)
     * </p>
     *
     * @param message 异常消息
     * @param e       原始异常
     */
    public ServiceException(String message, Throwable e) {
        super(message, e);
        this.exceptionCode = ExceptionCodeEnum.SERVICE_ERROR;
    }

    /**
     * 创建业务异常（支持参数化消息）
     *
     * @param exceptionCode 异常码枚举
     * @param params        消息格式化参数（支持 String.format 格式）
     */
    public ServiceException(IExceptionCode<?> exceptionCode, Object... params) {
        super(ArrayUtil.isNotEmpty(params) ? String.format(exceptionCode.text(), params) : exceptionCode.text());
        this.exceptionCode = exceptionCode;
    }

    /**
     * 创建业务异常并包装原始异常（支持参数化消息）
     *
     * @param e             原始异常
     * @param exceptionCode 异常码枚举
     * @param params        消息格式化参数（支持 String.format 格式）
     */
    public ServiceException(Throwable e, IExceptionCode<?> exceptionCode, Object... params) {
        super(ArrayUtil.isNotEmpty(params) ? String.format(exceptionCode.text(), params) : exceptionCode.text(), e);
        this.exceptionCode = exceptionCode;
    }

    /**
     * 获取异常码
     *
     * @return 异常码整数值
     */
    public Integer getCode() {
        return exceptionCode.intVal();
    }
}
