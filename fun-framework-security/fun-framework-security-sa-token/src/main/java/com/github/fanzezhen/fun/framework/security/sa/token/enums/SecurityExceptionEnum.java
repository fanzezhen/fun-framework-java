package com.github.fanzezhen.fun.framework.security.sa.token.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.IExceptionCode;
import lombok.Getter;

/**
 * 安全错误码
 */
@Getter
public enum SecurityExceptionEnum implements IExceptionCode<SecurityExceptionEnum> {
    /**
     * token请求超时
     */
    LOGIN_MODEL_UNSUPPORTED(400001, "登录模式不支持 %s"),
    /**
     * 登录失败：用户名或密码错误
     */
    LOGIN_FAILED_USER_PARAM_ERROR(400101, "登录失败：用户名或密码错误"),
    /**
     * 登录失败：用户验证错误
     */
    LOGIN_FAILED_USER_VERIFY_ERROR(400101, "登录失败：用户验证错误"),
    /**
     * token请求超时
     */
    TIMESTAMP_OUT_RANGE(400101, "权限校验不通过：timestamp 超出允许的范围（%s）"),
    /**
     * 校验 ticket 与 client 是否一致
     */
    CONSISTENCY_VERIFICATION_TICKET_CLIENT(400102, "该 ticket 不属于 client=%s, ticket 值: %s"),
    /**
     * 签名无效
     */
    SIGN_INVALID(400103, "签名无效: %s"),
    ;

    SecurityExceptionEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final Integer code;
    private final String text;

}
