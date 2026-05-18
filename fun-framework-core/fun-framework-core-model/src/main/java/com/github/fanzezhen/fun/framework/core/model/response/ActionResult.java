package com.github.fanzezhen.fun.framework.core.model.response;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.core.model.enums.ICodeTextEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.ExceptionCodeEnum;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.ConstraintViolationException;
import lombok.*;
import lombok.experimental.Accessors;
import java.util.Collection;
import java.util.List;


/**
 * 统一响应结构
 * <p>
 * 用于封装 API 返回结果。设计意图：
 * - 前端可通过 success 字段快速判断接口调用成功与否，无需解析 HTTP 状态码
 * - 失败时通过 errors 列表提供详细错误信息（支持多字段校验错误）
 * - 成功时通过 data 字段返回业务数据
 * </p>
 *
 * <p>典型响应格式：</p>
 * <pre>{@code
 * // 成功：{"success": true, "data": {...}}
 * // 失败：{"success": false, "errors": [{"code": "400", "message": "参数错误"}]}
 * }</pre>
 *
 * @param <T> 响应数据类型
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class ActionResult<T> {

    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 错误信息列表
     */
    private List<ErrorInfo> errors;

    /**
     * 获取响应消息
     * <p>
     * 成功时返回 "success"，失败时返回第一个错误信息的消息内容
     * </p>
     *
     * @return 响应消息
     */
    public String getMsg() {
        if (success) {
            return "success";
        }
        if (CollUtil.isEmpty(errors)) {
            return ExceptionCodeEnum.SERVICE_ERROR.getText();
        }
        return errors.get(0).getMessage();
    }

    /**
     * 构造响应对象
     *
     * @param success 是否成功
     */
    public ActionResult(boolean success) {
        this.success = success;
    }

    /**
     * 构造响应对象
     *
     * @param success 是否成功
     * @param data    响应数据
     */
    public ActionResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    /**
     * 构造失败响应对象
     *
     * @param errors 错误信息列表
     */
    public ActionResult(List<ErrorInfo> errors) {
        this.success = false;
        this.errors = errors;
    }

    /**
     * 构造失败响应对象
     *
     * @param error 错误信息
     */
    public ActionResult(ErrorInfo error) {
        this.success = false;
        this.errors = List.of(error);
    }

    /**
     * 构造响应对象
     *
     * @param success 是否成功
     * @param data    响应数据
     * @param error   错误信息
     */
    public ActionResult(boolean success, T data, ErrorInfo error) {
        this.success = success;
        this.data = data;
        this.errors = List.of(error);
    }

    /**
     * 创建成功响应
     *
     * @param <T> 响应数据类型
     * @return 成功响应对象
     */
    public static <T> ActionResult<T> success() {
        return new ActionResult<>(true);
    }

    /**
     * 创建成功响应并返回数据
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功响应对象
     */
    public static <T> ActionResult<T> success(T data) {
        return new ActionResult<>(true, data);
    }

    /**
     * 创建失败响应
     *
     * @param <T> 响应数据类型
     * @return 失败响应对象
     */
    public static <T> ActionResult<T> failed() {
        return new ActionResult<>(false);
    }

    /**
     * 创建失败响应并指定错误消息
     *
     * @param msg 错误消息
     * @param <T> 响应数据类型
     * @return 失败响应对象
     */
    public static <T> ActionResult<T> failed(String msg) {
        return new ActionResult<>(new ErrorInfo(msg));
    }

    /**
     * 创建失败响应并指定错误信息
     *
     * @param errorInfo 错误信息
     * @param <T>       响应数据类型
     * @return 失败响应对象
     */
    public static <T> ActionResult<T> failed(ErrorInfo errorInfo) {
        return new ActionResult<>(errorInfo);
    }

    /**
     * 创建失败响应并指定多个错误消息
     *
     * @param errormessages 错误消息集合
     * @param <T>           响应数据类型
     * @return 失败响应对象
     */
    public static <T> ActionResult<T> failed(Collection<String> errormessages) {
        if (CollUtil.isEmpty(errormessages)) {
            return failed();
        }
        List<ErrorInfo> errorList = errormessages.stream().map(ErrorInfo::new).toList();
        return new ActionResult<>(errorList);
    }

    /**
     * 从参数校验异常创建失败响应
     *
     * @param constraintViolationException 参数校验异常
     * @param <T>                          响应数据类型
     * @return 失败响应对象
     */
    public static <T> ActionResult<T> failed(ConstraintViolationException constraintViolationException) {
        if (CollUtil.isEmpty(constraintViolationException.getConstraintViolations())) {
            return failed(constraintViolationException.getMessage());
        }
        List<ErrorInfo> errorList = constraintViolationException.getConstraintViolations().stream()
            .map(constraintViolation -> new ErrorInfo(constraintViolation.getMessage()))
            .toList();
        return new ActionResult<>(errorList);
    }

    /**
     * 从异常码枚举创建失败响应
     *
     * @param exceptionEnum 异常码枚举
     * @param <T>           响应数据类型
     * @return 失败响应对象
     */
    public static <T> ActionResult<T> failed(ICodeTextEnum<?> exceptionEnum) {
        return new ActionResult<>(new ErrorInfo(exceptionEnum));
    }

    /**
     * 从业务异常创建失败响应
     *
     * @param serviceException 业务异常
     * @param <T>              响应数据类型
     * @return 失败响应对象
     */
    public static <T> ActionResult<T> failed(ServiceException serviceException) {
        return new ActionResult<>(new ErrorInfo(serviceException));
    }

    /**
     * 创建失败响应并返回数据和错误消息
     *
     * @param data 响应数据
     * @param msg  错误消息
     * @param <T>  响应数据类型
     * @return 失败响应对象
     */
    public static <T> ActionResult<T> failed(T data, String msg) {
        return new ActionResult<>(false, data, new ErrorInfo(msg));
    }
}

