package com.github.fanzezhen.fun.framework.mp.enums;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 性别枚举
 * <p>
 * 定义用户性别类型，遵循国家标准 GB/T 2261.1-2003。
 * <ul>
 *   <li>WOMAN (0) - 女性</li>
 *   <li>MAN (1) - 男性</li>
 *   <li>UNKNOWN (2) - 未知性别</li>
 *   <li>UNSPECIFIED (3) - 未说明性别</li>
 * </ul>
 */
public enum SexEnum {
    /**
     * 女性
     */
    WOMAN(0, "女"),
    /**
     * 男性
     */
    MAN(1, "男"),
    /**
     * 未知性别
     */
    UNKNOWN(2, "未知"),
    /**
     * 未说明性别
     */
    UNSPECIFIED(3, "未说明");

    /**
     * 性别码（用于数据库存储和 JSON 序列化）
     */
    @EnumValue
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    public final int code;

    /**
     * 性别描述
     */
    @Getter
    private final String desc;

    /**
     * 构造函数
     *
     * @param code 性别码
     * @param desc 性别描述
     */
    SexEnum(final int code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据性别码获取性别描述
     *
     * @param code 性别码
     * @return 性别描述，不存在时返回性别码字符串，code 为 null 时返回空字符串
     */
    public static String getNameByCode(final Integer code) {
        if (code != null) {
            SexEnum[] var1 = values();
            for (SexEnum enumItem : var1) {
                if (code.equals(enumItem.code)) {
                    return enumItem.getDesc();
                }
            }
            return String.valueOf(code);
        }
        return CharSequenceUtil.EMPTY;
    }

    /**
     * 根据性别码转换为枚举对象
     *
     * @param code 性别码
     * @return 对应的枚举对象，不存在时返回 null
     */
    public static SexEnum toEnum(final int code) {
        for (SexEnum statusEnum : values()) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
