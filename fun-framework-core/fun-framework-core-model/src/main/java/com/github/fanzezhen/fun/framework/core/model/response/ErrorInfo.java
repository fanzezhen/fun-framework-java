package com.github.fanzezhen.fun.framework.core.model.response;

import com.github.fanzezhen.fun.framework.core.model.enums.ICodeTextEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.ErrorResponseException;

import java.io.Serializable;

/**
 * @author fanzezhen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class ErrorInfo implements Serializable {
    /**
     * 消息code
     */
    private int code;

    private String message;

    public ErrorInfo(String message) {
        this.message = message;
    }

    public ErrorInfo(String code, String message) {
        this.code = Integer.parseInt(code);
        this.message = message;
    }

    public ErrorInfo(ICodeTextEnum<?> exceptionEnum) {
        this.code = exceptionEnum.intVal();
        this.message = exceptionEnum.text();
    }

    public ErrorInfo(ServiceException serviceException) {
        this.code = serviceException.getCode();
        this.message = serviceException.getMessage();
    }

    public ErrorInfo(ErrorResponseException errorResponseException) {
        this.code = errorResponseException.getStatusCode().value();
        this.message = errorResponseException.getMessage();
    }
}
