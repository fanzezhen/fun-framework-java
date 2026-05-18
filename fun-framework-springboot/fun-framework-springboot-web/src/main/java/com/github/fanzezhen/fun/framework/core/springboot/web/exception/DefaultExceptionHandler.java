package com.github.fanzezhen.fun.framework.core.springboot.web.exception;

import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.ExceptionCodeEnum;
import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
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
 * 全局异常处理器.
 * <p>
 * 将各类异常统一转换为 ActionResult 格式返回给前端，
 * 支持参数校验异常、业务异常和系统异常的统一处理。
 */
@Slf4j
@RestControllerAdvice
@SuppressWarnings("unused")
public class DefaultExceptionHandler {
    /**
     * 处理未捕获的通用异常.
     *
     * @param e 异常对象
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> exception(final Exception e) {
        log.error("全局异常信息 ex={}", e.getMessage(), e);
        return ActionResult.failed(ExceptionCodeEnum.SERVICE_ERROR);
    }

    /**
     * 处理参数校验异常（方法参数校验失败）.
     *
     * @param exception 参数校验异常
     * @return 统一响应结果，包含校验错误详情
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> bodyValidExceptionHandler(final MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        log.error("参数校验异常", exception);
        return ActionResult.failed(fieldErrors.stream().map(FieldError::toString).toList());
    }

    /**
     * 处理约束校验异常（Bean Validation约束违反）.
     *
     * @param exception 约束校验异常
     * @return 统一响应结果
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> bodyValidExceptionHandler(final ConstraintViolationException exception) {
        log.error("约束校验异常：{}", exception.getMessage(), exception);
        return ActionResult.failed(exception);
    }

    /**
     * 处理通用校验异常.
     *
     * @param exception 校验异常
     * @return 统一响应结果
     */
    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> bodyValidExceptionHandler(final ValidationException exception) {
        log.error("校验异常：{}", exception.getMessage(), exception);
        return ActionResult.failed(exception.getMessage());
    }

    /**
     * 处理业务异常.
     *
     * @param e 业务异常
     * @param request HTTP请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(value = {ServiceException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> businessException(final ServiceException e, final HttpServletRequest request) {
        log.error("request:{} Method:{} message:{}", request.getRequestURI(), request.getMethod(), e.getMessage(), e);
        return ActionResult.failed(e);
    }
}
