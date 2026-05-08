package com.github.fanzezhen.fun.framework.core.model.response;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.core.model.enums.ICodeTextEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.ExceptionCodeEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.ConstraintViolationException;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;


/**
 * 统一响应结构，用于封装API返回结果
 * <p>
 * 设计意图：
 * - 前端可通过 success 字段快速判断接口调用成功与否，无需解析 HTTP 状态码
 * - 失败时通过 errors 列表提供详细错误信息（支持多字段校验错误）
 * - 成功时通过 data 字段返回业务数据
 * <p>
 * 典型响应格式：
 * <pre>{@code
 * // 成功：{"success": true, "data": {...}}
 * // 失败：{"success": false, "errors": [{"code": "400", "message": "参数错误"}]}
 * }</pre>
 *
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class ActionResult<T> {

    private boolean success;
    private T data;
    private List<ErrorInfo> errors;

    public String getMsg() {
        if (success) {
            return "success";
        }
        if (CollUtil.isEmpty(errors)) {
            return ExceptionCodeEnum.SERVICE_ERROR.getText();
        }
        return errors.get(0).getMessage();
    }

    public ActionResult(boolean success) {
        this.success = success;
    }

    public ActionResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public ActionResult(List<ErrorInfo> errors) {
        this.success = false;
        this.errors = errors;
    }

    public ActionResult(ErrorInfo error) {
        this.success = false;
        this.errors = List.of(error);
    }

    public ActionResult(boolean success, T data, ErrorInfo error) {
        this.success = success;
        this.data = data;
        this.errors = List.of(error);
    }

    public static <T> ActionResult<T> success() {
        return new ActionResult<>(true);
    }

    public static <T> ActionResult<T> success(T data) {
        return new ActionResult<>(true, data);
    }

    public static <T> ActionResult<T> failed() {
        return new ActionResult<>(false);
    }

    public static <T> ActionResult<T> failed(String msg) {
        return new ActionResult<>(new ErrorInfo(msg));
    }

    public static <T> ActionResult<T> failed(ErrorInfo errorInfo) {
        return new ActionResult<>(errorInfo);
    }

    public static <T> ActionResult<T> failed(List<FieldError> fieldErrors) {
        if (CollUtil.isEmpty(fieldErrors)) {
            return failed();
        }
        List<ErrorInfo> errorList = new ArrayList<>();
        fieldErrors.forEach(fieldError -> errorList.add(new ErrorInfo(String.valueOf(fieldError))));
        return new ActionResult<>(errorList);
    }

    public static <T> ActionResult<T> failed(ConstraintViolationException constraintViolationException) {
        if (CollUtil.isEmpty(constraintViolationException.getConstraintViolations())) {
            return failed(constraintViolationException.getMessage());
        }
        List<ErrorInfo> errorList = constraintViolationException.getConstraintViolations().stream()
            .map(constraintViolation -> new ErrorInfo(constraintViolation.getMessage()))
            .toList();
        return new ActionResult<>(errorList);
    }

    public static <T> ActionResult<T> failed(ICodeTextEnum exceptionEnum) {
        return new ActionResult<>(new ErrorInfo(exceptionEnum));
    }

    public static <T> ActionResult<T> failed(ServiceException serviceException) {
        return new ActionResult<>(new ErrorInfo(serviceException));
    }

    public static <T> ActionResult<T> failed(T data, String msg) {
        return new ActionResult<>(false, data, new ErrorInfo(msg));
    }
}

