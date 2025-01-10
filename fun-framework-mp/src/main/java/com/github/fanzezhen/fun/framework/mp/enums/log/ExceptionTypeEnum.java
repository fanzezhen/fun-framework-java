package com.github.fanzezhen.fun.framework.mp.enums.log;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 异常类型枚举
 * @author zezhen.fan
 */
public enum ExceptionTypeEnum {
    /**
     * 登录成功
     */
    DEFAULT(0, "默认");

    @EnumValue
    @JsonValue
    public final int type;
    @Getter
    private final String desc;

    ExceptionTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
