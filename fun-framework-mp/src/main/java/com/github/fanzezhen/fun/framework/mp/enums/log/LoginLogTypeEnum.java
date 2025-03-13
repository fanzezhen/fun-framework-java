package com.github.fanzezhen.fun.framework.mp.enums.log;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 登录日志类型枚举
 * @author fanzezhen
 */
public enum LoginLogTypeEnum {
    /**
     * 登录成功
     */
    LOGIN_SUCCEED(1, "登录成功"),
    /**
     * 登录失败
     */
    LOGIN_FAILED(2, "登录失败"),
    /**
     * 退出登录
     */
    LOGOUT(3, "退出登录");

    @EnumValue
    @JsonValue
    public final int type;
    @Getter
    private final String desc;

    LoginLogTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
