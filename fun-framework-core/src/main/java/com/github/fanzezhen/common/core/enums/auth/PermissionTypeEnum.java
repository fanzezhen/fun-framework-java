package com.github.fanzezhen.fun.framework.core.enums.auth;

/**
 * 权限类型枚举类
 * @author zezhen.fan
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
