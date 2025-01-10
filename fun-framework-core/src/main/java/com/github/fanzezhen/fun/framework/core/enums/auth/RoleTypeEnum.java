package com.github.fanzezhen.fun.framework.core.enums.auth;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.core.constant.SecurityConstant;

import java.util.*;

/**
 * @author fanzezhen
 * 角色类型枚举类
 */
public enum RoleTypeEnum {
    /**
     * 超级管理员
     */
    SPECIAL_ADMIN(0, "SPECIAL_ADMIN", "超级管理员"),
    /**
     * 管理员
     */
    ADMIN(1, "ADMIN", "管理员"),
    /**
     * 普通角色
     */
    NORMAL(2, "NORMAL", "普通角色"),
    /**
     * 访客角色
     */
    GUEST(3, "GUEST", "访客角色");

    private final int type;
    private final String code;
    private final String desc;

    RoleTypeEnum(int type, String code, String desc) {
        this.type = type;
        this.code = code;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static List<RoleTypeEnum> queryRoleTypeEnumListByType(Collection<? extends Integer> typeList) {
        List<RoleTypeEnum> roleTypeEnumList = new ArrayList<>();
        for (RoleTypeEnum roleTypeEnum : values()) {
            if (typeList.contains(roleTypeEnum.type)) {
                roleTypeEnumList.add(roleTypeEnum);
            }
        }
        return roleTypeEnumList;
    }

    public static Set<String> queryRoleTypeCodeSetByType(Collection<? extends Integer> typeList) {
        Set<String> roleTypeCodeSet = new HashSet<>();
        for (RoleTypeEnum roleTypeEnum : values()) {
            if (typeList.contains(roleTypeEnum.type)) {
                roleTypeCodeSet.add(roleTypeEnum.code);
            }
        }
        return roleTypeCodeSet;
    }

    public static Set<String> securityRoleTypeCodeSetByType(Collection<? extends Integer> types) {
        Set<String> roleTypeCodeSet = new HashSet<>();
        if (CollUtil.isEmpty(types)) {
            return roleTypeCodeSet;
        }
        for (RoleTypeEnum roleTypeEnum : values()) {
            if (types.contains(roleTypeEnum.type)) {
                roleTypeCodeSet.add(SecurityConstant.ROLE_PREFIX + roleTypeEnum.code);
            }
        }
        return roleTypeCodeSet;
    }

    public static Set<String> queryRoleTypeCodeSetAll() {
        Set<String> roleTypeCodeSet = new HashSet<>();
        for (RoleTypeEnum roleTypeEnum : values()) {
            roleTypeCodeSet.add(roleTypeEnum.code);
        }
        return roleTypeCodeSet;
    }
}
