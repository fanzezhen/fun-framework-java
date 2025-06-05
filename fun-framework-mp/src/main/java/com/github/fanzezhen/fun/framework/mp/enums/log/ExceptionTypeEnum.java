package com.github.fanzezhen.fun.framework.mp.enums.log;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 异常类型枚举
 * @author fanzezhen
 */
public enum ExceptionTypeEnum {
    /**
     * 登录成功
     */
    DEFAULT(0, "默认");

    @EnumValue
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    public final int type;
    @Getter
    private final String desc;

    ExceptionTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
