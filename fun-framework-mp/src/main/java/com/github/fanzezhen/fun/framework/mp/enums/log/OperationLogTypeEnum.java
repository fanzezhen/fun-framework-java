package com.github.fanzezhen.fun.framework.mp.enums.log;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

/**
 * 登录日志类型枚举
 *
 * @author fanzezhen
 */
public enum OperationLogTypeEnum {
    /**
     * 默认
     */
    DEFAULT(0, "默认"),
    /**
     * 新增
     */
    ADD(1, "新增"),
    /**
     * 修改
     */
    UPDATE(2, "修改");

    @EnumValue
    @JsonValue
    public final int type;
    @Getter
    private final String desc;

    OperationLogTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean needCompare(int type) {
        return type != ADD.type;
    }

    public static OperationLogTypeEnum getOrDefaultByType(int type) {
        for (OperationLogTypeEnum typeEnum : OperationLogTypeEnum.values()) {
            if (Objects.equals(typeEnum.type, type)) {
                return typeEnum;
            }
        }
        return OperationLogTypeEnum.DEFAULT;
    }
}
