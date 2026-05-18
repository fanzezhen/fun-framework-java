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
 */
public interface FunSecurityFacade {
    /**
     * 获取需要管控的接口列表。
     *
     * @param serviceCode 应用标识，用于区分不同的服务
     * @return 需要权限控制的接口URI列表
     */
    List<String> needManageUriList(String serviceCode);

    /**
     * 获取用户有权限的接口列表。
     *
     * @param serviceCode 应用标识，用于区分不同的服务
     * @param username    用户名
     * @return 用户拥有权限的接口URI列表
     */
    List<String> holdUriList(String serviceCode, String username);

    /**
     * 获取用户拥有的角色列表。
     *
     * @param serviceCode 应用标识，用于区分不同的服务
     * @param username    用户名
     * @return 用户拥有的角色标识列表
     */
    List<String> holdRoleList(String serviceCode, String username);

    /**
     * 登录验证通过后的回调方法。
     * <p>
     * 用于在登录验证通过后，加载用户的完整信息。
     *
     * @param <K>     用户ID类型
     * @param <U>     用户实体类型
     * @param loginId 登录ID
     * @return 用户实体对象，如果获取失败则返回 null
     */
    @SuppressWarnings("UnusedReturnValue")
    default <K extends Serializable, U extends IUser<K>> U callbackForCheckLogin(final Object loginId) {
        return null;
    }
}
