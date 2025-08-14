package com.github.fanzezhen.fun.framework.security.spring.security;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.security.base.FunSecurityFacade;
import com.github.fanzezhen.fun.framework.security.base.FunSpringSecurityProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PatternMatchUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义权限评估器，用于 Spring Security 方法级别的权限控制（如 @PreAuthorize("hasPermission(...)")）
 * <p>
 * 该类主要用于判断当前登录用户是否拥有访问某个接口（URL）的权限。
 * </p>
 */
@Component
public class FunPermissionEvaluator implements PermissionEvaluator {

    /**
     * 权限门面类，用于调用权限相关的业务逻辑。
     */
    @Resource
    private FunSecurityFacade funPermissionFacade;
    /**
     * 权限门面类，用于调用权限相关的业务逻辑。
     */
    @Autowired(required = false)
    private DefaultWebSecurityExpressionHandler webSecurityExpressionHandler;

    /**
     * 安全模块的配置属性，包含路由规则、权限模式等。
     */
    @Resource
    private FunSpringSecurityProperties funSpringSecurityProperties;

    /**
     * Ant风格路径匹配器，用于匹配路径模式（如 /api/**）
     */
    static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 判断用户是否拥有对某个目标对象的权限。
     * <p>
     * 此方法主要用于方法级别的权限控制，例如：
     * </p>
     * <pre>{@code
     * @PreAuthorize("hasPermission('/user/list', 'read')")
     * }</pre>
     *
     * @param authentication 当前用户的认证信息
     * @param targetDomainObject 目标对象，通常是一个请求路径（String）
     * @param permission 权限标识，如 "read", "write"
     * @return 是否有权限
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // 判断用户是否已登录（Principal 是否为 UserDetails 实例）
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            return false;
        }

        // 获取当前登录用户名
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        // 获取目标路径（即接口路径）
        String requestPath = (String) targetDomainObject;

        // 用于存储服务编码（如：system、user）
        String serviceCode = null;

        // 最终需要匹配的接口路径
        String requestUri;

        // 如果路径匹配路由模式（如 /api/{serviceCode}/**）
        if (ANT_PATH_MATCHER.match(funSpringSecurityProperties.getRoutePattern(), requestPath)) {
            // 提取服务编码（如 /api/system/user/list 提取 serviceCode 为 "system"）
            serviceCode = ANT_PATH_MATCHER
                .extractUriTemplateVariables(funSpringSecurityProperties.getRoutePattern(), requestPath)
                .get(funSpringSecurityProperties.getRouteServiceCodeKey());

            // 截取服务编码之后的路径，作为真正的接口路径
            requestUri = requestPath.substring((funSpringSecurityProperties.getRoutePrefix() + serviceCode).length());
        } else {
            // 如果不匹配路由模式，直接使用原路径作为请求路径
            requestUri = requestPath;
        }

        // 获取该服务下需要权限控制的 URI 列表
        List<String> needManageUriList = funPermissionFacade.needManageUriList(serviceCode);

        // 如果无需要权限管理的接口，直接放行
        if (CollUtil.isEmpty(needManageUriList)) {
            return true;
        }

        // 如果当前请求路径匹配需要权限控制的列表
        if (needManageUriList.stream().anyMatch(uri -> PatternMatchUtils.simpleMatch(uri, requestUri))) {
            // 获取该用户在该服务下拥有的接口权限列表
            List<String> holdUriList = funPermissionFacade.holdUriList(serviceCode, username);

            // 判断用户是否拥有该接口权限
            return holdUriList != null && holdUriList.stream().anyMatch(uri -> PatternMatchUtils.simpleMatch(uri, requestUri));
        }

        // 默认允许访问
        return true;
    }

    /**
     * 判断用户是否拥有对某个目标对象 ID 的权限。
     * <p>
     * 此方法用于通过 ID 判断权限，如：
     * </p>
     * <pre>{@code
     * @PreAuthorize("hasPermission(#id, 'com.example.model.User', 'read')")
     * }</pre>
     *
     * <p>
     * 本系统暂未使用此方法，因此直接返回 false。
     * </p>
     *
     * @param authentication 当前用户的认证信息
     * @param targetId 目标对象 ID（如 Long）
     * @param targetType 目标类型（如类名）
     * @param permission 权限标识
     * @return 是否有权限
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
    
    @PostConstruct
    public void init(){
        if (webSecurityExpressionHandler!=null){
        webSecurityExpressionHandler.setPermissionEvaluator(this);
        }
    }
}
