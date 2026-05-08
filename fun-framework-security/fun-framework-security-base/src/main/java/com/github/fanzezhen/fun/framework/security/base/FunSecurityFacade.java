package com.github.fanzezhen.fun.framework.security.base;

import com.github.fanzezhen.fun.framework.core.model.common.IUser;

import java.io.Serializable;
import java.util.List;

/**
 * 统一权限管理门面接口
 * <p>
 * 定义权限系统需要实现的核心能力：接口权限管控、用户权限查询、登录回调。
 * 由业务系统实现具体的权限数据来源（数据库、LDAP、第三方系统等）。
 * <p>
 * <b>使用场景：</b>与Spring Security或Sa-Token集成，提供统一的权限数据源
 *
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
