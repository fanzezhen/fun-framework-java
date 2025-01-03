package com.github.fanzezhen.fun.framework.mp.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 状态是否启用
 *
 * @author zezhen.fan
 */
public enum StatusEnum {
    /**
     * 启用
     */
    ENABLE(0, "启用"),
    /**
     * 禁用
     */
    DISABLE(1, "禁用");

    @EnumValue
    @JsonValue
    public final int code;
    @Getter
    private final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getNameByCode(Integer code) {
        if (code != null) {
            StatusEnum[] var1 = values();
            for (StatusEnum enumItem : var1) {
                if (code.equals(enumItem.code)) {
                    return enumItem.getDesc();
                }
            }
            return String.valueOf(code);
        }
        return StrUtil.EMPTY;
    }

    public static StatusEnum toEnum(int code) {
        for (StatusEnum statusEnum : values()) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

    public static String getColumn() {
        return "status";
    }
}
