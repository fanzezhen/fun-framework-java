package com.github.fanzezhen.fun.framework.security.sa.token.permission;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpInterface;
import com.github.fanzezhen.fun.framework.security.base.FunSecurityFacade;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sa-Token 权限管理接口实现类。
 * <p>
 * 实现 Sa-Token 的权限和角色查询接口，通过 {@link FunSecurityFacade} 获取用户的权限和角色信息。
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * Sa-Token 配置对象。
     */
    @Resource
    private SaTokenConfig tokenConfig;

    /**
     * 权限门面，用于查询用户权限和角色。
     */
    @Resource
    private FunSecurityFacade funPermissionFacade;

    /**
     * 获取用户的权限列表。
     *
     * @param loginId   登录ID
     * @param loginType 登录类型（设备类型）
     * @return 权限标识列表
     */
    @Override
    public List<String> getPermissionList(final Object loginId, final String loginType) {
        return funPermissionFacade.holdUriList(loginType, String.valueOf(loginId));
    }

    /**
     * 获取用户的角色列表。
     *
     * @param loginId   登录ID
     * @param loginType 登录类型（设备类型）
     * @return 角色标识列表
     */
    @Override
    public List<String> getRoleList(final Object loginId, final String loginType) {
        return funPermissionFacade.holdRoleList(loginType, String.valueOf(loginId));
    }
}
