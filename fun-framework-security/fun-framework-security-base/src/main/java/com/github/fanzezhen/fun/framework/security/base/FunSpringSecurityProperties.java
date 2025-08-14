package com.github.fanzezhen.fun.framework.security.base;

import cn.hutool.core.text.StrPool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

@Data
@ConfigurationProperties(prefix = "fun.security")
public class FunSpringSecurityProperties {
    /**
     * 忽略验证的接口集合
     */
    private Set<String> ignoreUris = Collections.emptySet();
    /**
     * 登录
     */
    private String loginPage = "/login";
    /**
     * 微服务：路由标识
     */
    private String routeFlag = "lb";
    /**
     * 微服务：路由请求路径前缀匹配
     */
    private String routePrefix = StrPool.SLASH + routeFlag + StrPool.SLASH;
    /**
     * 微服务：路由服务名标识
     */
    private String routeServiceCodeKey = "service-code";
    /**
     * 微服务：路由请求路径匹配模板
     */
    private String routePattern = routePrefix + "{" + routeServiceCodeKey + "}/**";
    /**
     * 微服务：路由注册中心转发接口
     */
    private String routeUri = routeFlag + StrPool.COLON + StrPool.SLASH;
    /**
     * cas单点登录配置
     */
    private Cas cas = new Cas();
    /**
     * Oauth单点登录配置
     */
    private Oauth oauth = new Oauth();

    /**
     * cas单点登录配置
     */
    @Data
    public static class Cas {
        /**
         * CasAuthenticationProvider 的 标识
         */
        private String authenticationProviderKey = "fun-cas-authentication-provider";
        /**
         * 客户端应用（Service）的回调地址
         */
        private String serviceUrl;
        /**
         * CAS服务器的地址，用来验证票据（ticket）等
         */
        private String serverUrlPrefix;
    }

    /**
     * Oauth单点登录配置
     */
    @Data
    public static class Oauth {
        /**
         * 客户端应用（Service）的回调地址
         */
        private String serviceUrl;
    }

    /**
     * 忽略验证的接口数组
     */
    public String[] getIgnoreUriArr() {
        return ignoreUris.toArray(new String[]{});
    }
}
