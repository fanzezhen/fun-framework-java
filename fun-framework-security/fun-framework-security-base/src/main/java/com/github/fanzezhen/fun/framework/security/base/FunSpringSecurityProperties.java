package com.github.fanzezhen.fun.framework.security.base;

import cn.hutool.core.text.StrPool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
 * 安全模块配置属性
 * <p>
 * 配置前缀：fun.security
 * <p>
 * 提供接口白名单、登录页配置、微服务路由规则、CAS/OAuth单点登录等功能的配置支持。
 */
@Data
@ConfigurationProperties(prefix = "fun.security")
public class FunSpringSecurityProperties {
    /**
     * 忽略验证的接口集合。
     * <p>
     * 配置在此集合中的URI将不进行权限验证，直接放行。
     */
    private Set<String> ignoreUris = Collections.emptySet();

    /**
     * 登录页面地址。
     */
    private String loginPage = "/login";

    /**
     * 微服务路由标识。
     * <p>
     * 用于标识负载均衡路由，默认为 "lb"。
     */
    private String routeFlag = "lb";

    /**
     * 微服务路由请求路径前缀。
     * <p>
     * 例如：/lb/
     */
    private String routePrefix = StrPool.SLASH + routeFlag + StrPool.SLASH;

    /**
     * 微服务路由服务名标识的占位符键名。
     * <p>
     * 用于从路径模板中提取服务名称。
     */
    private String routeServiceCodeKey = "service-code";

    /**
     * 微服务路由请求路径匹配模板。
     * <p>
     * 例如：/lb/{service-code}/**
     */
    private String routePattern = routePrefix + "{" + routeServiceCodeKey + "}/**";

    /**
     * 微服务路由注册中心转发地址前缀。
     * <p>
     * 例如：lb://
     */
    private String routeUri = routeFlag + StrPool.COLON + StrPool.SLASH;

    /**
     * CAS 单点登录配置。
     */
    private Cas cas = new Cas();

    /**
     * OAuth2.0 单点登录配置。
     */
    private Oauth oauth = new Oauth();

    /**
     * CAS 单点登录配置。
     */
    @Data
    public static class Cas {
        /**
         * CasAuthenticationProvider 的唯一标识。
         */
        private String authenticationProviderKey = "fun-cas-authentication-provider";

        /**
         * 客户端应用（Service）的回调地址。
         * <p>
         * CAS Server 验证成功后将重定向到此地址。
         */
        private String serviceUrl;

        /**
         * CAS 服务器的地址前缀。
         * <p>
         * 用于验证票据（ticket）和进行其他 CAS 操作。
         */
        private String serverUrlPrefix;
    }

    /**
     * OAuth2.0 单点登录配置。
     */
    @Data
    public static class Oauth {
        /**
         * 客户端应用（Service）的回调地址。
         * <p>
         * OAuth2.0 授权服务器验证成功后将重定向到此地址。
         */
        private String serviceUrl;
    }

    /**
     * 获取忽略验证的接口数组。
     *
     * @return 接口URI数组
     */
    public String[] getIgnoreUriArr() {
        return ignoreUris.toArray(new String[0]);
    }
}
