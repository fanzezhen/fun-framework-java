package com.github.fanzezhen.fun.framework.mp.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author fanzezhen
 */
public enum OperationTypeEnum {
    /**
     * 新增
     */
    INSERT(0, "新增"),
    /**
     * 修改
     */
    UPDATE(1, "修改"),
    /**
     * 删除
     */
    DELETE(2, "删除"),
    /**
     * 迁入
     */
    TRANSFER_IN(12, "迁入"),
    /**
     * 迁出
     */
    TRANSFER_OUT(13, "迁出");

    @EnumValue
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final int value;
    @Getter
    private final String desc;

    OperationTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static String getDesc(int code) {
        for (OperationTypeEnum type : OperationTypeEnum.values()) {
            if (type.value == code) {
                return type.getDesc();
            }
        }
        return null;
    }
}
