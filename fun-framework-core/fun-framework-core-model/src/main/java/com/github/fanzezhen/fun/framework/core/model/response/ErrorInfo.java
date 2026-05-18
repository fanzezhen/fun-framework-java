package com.github.fanzezhen.fun.framework.core.model.response;

import com.github.fanzezhen.fun.framework.core.model.enums.ICodeTextEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一错误信息模型
 * <p>
 * 用于在 HTTP 响应中封装错误码和错误消息。
 * 支持从多种异常类型（ServiceException 等）和枚举构建。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class ErrorInfo implements Serializable {
    /**
     * 错误码
     */
    private int code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 构造错误信息
     *
     * @param message 错误消息
     */
    public ErrorInfo(String message) {
        this.message = message;
    }

    /**
     * 构造错误信息
     *
     * @param code    错误码（字符串格式）
     * @param message 错误消息
     */
    public ErrorInfo(String code, String message) {
        this.code = Integer.parseInt(code);
        this.message = message;
    }

    /**
     * 从异常码枚举构造错误信息
     *
     * @param exceptionEnum 异常码枚举
     */
    public ErrorInfo(ICodeTextEnum<?> exceptionEnum) {
        this.code = exceptionEnum.intVal();
        this.message = exceptionEnum.text();
    }

    /**
     * 从业务异常构造错误信息
     *
     * @param serviceException 业务异常
     */
    public ErrorInfo(ServiceException serviceException) {
        this.code = serviceException.getCode();
        this.message = serviceException.getMessage();
    }
}
