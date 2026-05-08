package com.github.fanzezhen.fun.framework.core.exception;

import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.ExceptionCodeEnum;
import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常处理器，将各类异常统一转换为 ActionResult 格式返回给前端
 *
 */
@Slf4j
@Hidden
@RestControllerAdvice
@SuppressWarnings("unused")
public class DefaultExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> exception(Exception e) {
        log.error("全局异常信息 ex={}", e.getMessage(), e);
        return ActionResult.failed(ExceptionCodeEnum.SERVICE_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> bodyValidExceptionHandler(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        log.error("参数校验异常", exception);
        return ActionResult.failed(fieldErrors);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> bodyValidExceptionHandler(ConstraintViolationException exception) {
        log.error("约束校验异常：{}", exception.getMessage(), exception);
        return ActionResult.failed(exception);
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> bodyValidExceptionHandler(ValidationException exception) {
        log.error("校验异常：{}", exception.getMessage(), exception);
        return ActionResult.failed(exception.getMessage());
    }

    @ExceptionHandler(value = {ServiceException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> businessException(ServiceException e, HttpServletRequest request) {
        log.error("request:{} Method:{} message:{}", request.getRequestURI(), request.getMethod(), e.getMessage(), e);
        return ActionResult.failed(e);
    }
}
