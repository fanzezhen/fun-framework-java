package com.github.fanzezhen.fun.framework.mp.enums;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 状态枚举
 * <p>
 * 定义记录的启用/禁用状态，常用于控制功能开关、数据可见性等场景。
 * <ul>
 *   <li>ENABLE (0) - 启用状态</li>
 *   <li>DISABLE (1) - 禁用状态</li>
 * </ul>
 */
public enum StatusEnum {
    /**
     * 启用状态
     */
    ENABLE(0, "启用"),
    /**
     * 禁用状态
     */
    DISABLE(1, "禁用");

    /**
     * 状态码（用于数据库存储和 JSON 序列化）
     */
    @EnumValue
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    public final int code;

    /**
     * 状态描述
     */
    @Getter
    private final String desc;

    /**
     * 构造函数
     *
     * @param code 状态码
     * @param desc 状态描述
     */
    StatusEnum(final int code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取状态描述
     *
     * @param code 状态码
     * @return 状态描述，不存在时返回状态码字符串，code 为 null 时返回空字符串
     */
    public static String getNameByCode(final Integer code) {
        if (code != null) {
            StatusEnum[] var1 = values();
            for (StatusEnum enumItem : var1) {
                if (code.equals(enumItem.code)) {
                    return enumItem.getDesc();
                }
            }
            return String.valueOf(code);
        }
        return CharSequenceUtil.EMPTY;
    }

    /**
     * 根据状态码转换为枚举对象
     *
     * @param code 状态码
     * @return 对应的枚举对象，不存在时返回 null
     */
    public static StatusEnum toEnum(final int code) {
        for (StatusEnum statusEnum : values()) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }
}
