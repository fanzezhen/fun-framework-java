package com.github.fanzezhen.fun.framework.mp.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author fanzezhen
 */
public enum WorkingStatusEnum {
    /**
     * 在职的
     */
    INCUMBENT(0, "在职的"),
    /**
     * 离职的
     */
    OUTGOING(1, "离职的");

    @EnumValue
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    public final int code;
    @Getter
    private final String desc;

    WorkingStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
