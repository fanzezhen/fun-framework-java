package com.github.fanzezhen.fun.framework.security.base;

import com.github.fanzezhen.fun.framework.core.model.IUser;

import java.io.Serializable;
import java.util.List;

/**
 * 权限接口
 */
public interface FunSecurityFacade {
    /**
     * 获取需要管控的接口列表
     *
     * @param serviceCode 应用标识
     */
    List<String> needManageUriList(String serviceCode);

    /**
     * 获取用户有权限的接口列表
     *
     * @param serviceCode 应用标识
     * @param username    用户名
     */
    List<String> holdUriList(String serviceCode, String username);

    /**
     * 获取用户有权限的接口列表
     *
     * @param serviceCode 应用标识
     * @param username    用户名
     */
    List<String> holdRoleList(String serviceCode, String username);

    /**
     * 登录验证后的回调
     */
    default <K extends Serializable, U extends IUser<K>> U callbackForCheckLogin(Object loginId) {
        return null;
    }
}
