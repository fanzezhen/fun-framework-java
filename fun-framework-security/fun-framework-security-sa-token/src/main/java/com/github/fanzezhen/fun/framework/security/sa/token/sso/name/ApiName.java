package com.github.fanzezhen.fun.framework.security.sa.token.sso.name;

import lombok.Data;

/**
 * SSO 模块所有 API 路由名称定义。
 * <p>
 * 提供 SSO 协议中各个端点的路由配置。
 */
@Data
public class ApiName {

    /**
     * SSO Server端授权地址。
     */
    private String ssoAuth = "/sso/auth-url";

    /**
     * SSO Server端 RestAPI 登录接口。
     */
    private String ssoDoLogin = "/sso/doLogin";

    /**
     * SSO Server端校验 Ticket 并获取账号ID的接口。
     */
    private String ssoCheckTicket = "/sso/checkTicket";

    /**
     * SSO Server端获取 userinfo 接口。
     */
    private String ssoUserinfo = "/sso/userinfo";

    /**
     * SSO Server端单点注销地址。
     */
    private String ssoSignOut = "/sso/sign-out";

    /**
     * SSO Client端登录地址。
     */
    private String ssoLogin = "/auth/verify-access-token";

    /**
     * SSO Client端单点注销地址。
     */
    private String ssoLogout = "/sso/logout";

    /**
     * SSO Client端单点注销的回调地址。
     */
    private String ssoLogoutCall = "/sso/logoutCall";

    /**
     * 批量修改路径，新增固定前缀。
     *
     * @param prefix 前缀，例如：/sso-user、/sso-admin
     * @return 对象自身（支持链式调用）
     */
    public ApiName addPrefix(final String prefix) {
        this.ssoAuth = prefix + this.ssoAuth;
        this.ssoDoLogin = prefix + this.ssoDoLogin;
        this.ssoCheckTicket = prefix + this.ssoCheckTicket;
        this.ssoUserinfo = prefix + this.ssoUserinfo;
        this.ssoSignOut = prefix + this.ssoSignOut;
        this.ssoLogin = prefix + this.ssoLogin;
        this.ssoLogout = prefix + this.ssoLogout;
        this.ssoLogoutCall = prefix + this.ssoLogoutCall;
        return this;
    }

    /**
     * 批量修改路径，替换掉 /sso 固定前缀。
     *
     * @param prefix 新前缀，例如：/sso-user、/sso-admin
     * @return 对象自身（支持链式调用）
     */
    public ApiName replacePrefix(final String prefix) {
        String oldPrefix = "/sso";
        this.ssoAuth = this.ssoAuth.replaceFirst(oldPrefix, prefix);
        this.ssoDoLogin = this.ssoDoLogin.replaceFirst(oldPrefix, prefix);
        this.ssoCheckTicket = this.ssoCheckTicket.replaceFirst(oldPrefix, prefix);
        this.ssoUserinfo = this.ssoUserinfo.replaceFirst(oldPrefix, prefix);
        this.ssoSignOut = this.ssoSignOut.replaceFirst(oldPrefix, prefix);
        this.ssoLogin = this.ssoLogin.replaceFirst(oldPrefix, prefix);
        this.ssoLogout = this.ssoLogout.replaceFirst(oldPrefix, prefix);
        this.ssoLogoutCall = this.ssoLogoutCall.replaceFirst(oldPrefix, prefix);
        return this;
    }


}
