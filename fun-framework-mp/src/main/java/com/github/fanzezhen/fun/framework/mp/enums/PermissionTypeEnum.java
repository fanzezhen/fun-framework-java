package com.github.fanzezhen.fun.framework.mp.enums;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 权限类型枚举类
 * @author fanzezhen
 */
public enum PermissionTypeEnum {
    /**
     * 菜单
     */
    MENU(1, "菜单"),
    /**
     * 按钮
     */
    BUTTON(2, "按钮");

    @EnumValue
    @JsonValue
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteEnumUsingToString)
    private final int type;
    private final String desc;

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    PermissionTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
