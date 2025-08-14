package com.github.fanzezhen.fun.framework.security.sa.token.permission;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpInterface;
import com.github.fanzezhen.fun.framework.security.base.FunSecurityFacade;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * sa-token 权限管理实现类
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * sa配置
     */
    @Resource
    private SaTokenConfig tokenConfig;

    /**
     * redis
     */
    @Resource
    private FunSecurityFacade funPermissionFacade;

    /**
     * 获取菜单权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return funPermissionFacade.holdUriList(loginType, String.valueOf(loginId));
    }

    /**
     * 获取角色权限列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return funPermissionFacade.holdRoleList(loginType, String.valueOf(loginId));
    }
}
