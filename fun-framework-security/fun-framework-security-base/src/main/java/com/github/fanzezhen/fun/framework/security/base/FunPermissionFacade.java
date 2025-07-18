package com.github.fanzezhen.fun.framework.security.base;

import java.util.List;

/**
 * 权限接口
 */
public interface FunPermissionFacade {
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
}
