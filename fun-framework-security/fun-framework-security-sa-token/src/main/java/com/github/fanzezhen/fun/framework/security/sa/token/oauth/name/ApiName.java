package com.github.fanzezhen.fun.framework.security.sa.token.oauth.name;

/**
 * Oauth 模块所有 API 路由名称定义
 */
public class ApiName {

    /**
     * SSO-Server端：授权地址
     */
    private String authorizeUrl = "/oauth/authorize";

    /**
     * SSO-Server端：RestAPI 登录接口
     */
    private String oauthLogoutUrl = "/oauth/logout";

    /**
     * SSO-Server端：校验ticket 获取账号id
     */
    private String tokenUrl = "/oauth/token";

    /**
     * SSO-Server端：获取userinfo
     */
    private String userInfoUrl = "/oauth/user/getuserInfo";

    /**
     * 批量修改path，新增固定前缀
     *
     * @param prefix 示例值：/sso-user、/sso-admin
     *
     * @return 对象自身
     */
    public ApiName addPrefix(String prefix) {
        this.authorizeUrl = prefix + this.authorizeUrl;
        this.oauthLogoutUrl = prefix + this.oauthLogoutUrl;
        this.tokenUrl = prefix + this.tokenUrl;
        this.userInfoUrl = prefix + this.userInfoUrl;
        return this;
    }

    /**
     * 批量修改path，替换掉 /sso 固定前缀
     *
     * @param prefix 示例值：/sso-user、/sso-admin
     *
     * @return 对象自身
     */
    public ApiName replacePrefix(String prefix) {
        String oldPrefix = "/oauth";
        this.authorizeUrl = this.authorizeUrl.replaceFirst(oldPrefix, prefix);
        this.oauthLogoutUrl = this.oauthLogoutUrl.replaceFirst(oldPrefix, prefix);
        this.tokenUrl = this.tokenUrl.replaceFirst(oldPrefix, prefix);
        this.userInfoUrl = this.userInfoUrl.replaceFirst(oldPrefix, prefix);
        return this;
    }


    /**
     * toString
     */
    @Override
    public String toString() {
        return "ApiName [oauthAuthorizeUrl=" + authorizeUrl + ", ssoOauthLogoutUrl=" + oauthLogoutUrl + ", ssoTokenUrl=" + tokenUrl
                + ", ssoUserInfoUrl=" + userInfoUrl + "]";
    }

}
