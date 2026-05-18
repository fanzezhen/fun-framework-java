package com.github.fanzezhen.fun.framework.security.sa.token.oauth.name;

/**
 *  模块所有 API 路由名称定义。
 * <p>
 * 提供  协议中各个端点的路由配置。
 */
public class ApiName {

    /**
     *  Server端授权地址。
     */
    private String authorizeUrl = "/oauth/authorize";

    /**
     *  Server端登出接口。
     */
    private String oauthLogoutUrl = "/oauth/logout";

    /**
     *  Server端 Token 获取地址。
     */
    private String tokenUrl = "/oauth/token";

    /**
     *  Server端用户信息获取地址。
     */
    private String userInfoUrl = "/oauth/user/getuserInfo";

    /**
     * 批量修改路径，新增固定前缀。
     *
     * @param prefix 前缀，例如：/sso-user、/sso-admin
     * @return 对象自身
     */
    public ApiName addPrefix(final String prefix) {
        this.authorizeUrl = prefix + this.authorizeUrl;
        this.oauthLogoutUrl = prefix + this.oauthLogoutUrl;
        this.tokenUrl = prefix + this.tokenUrl;
        this.userInfoUrl = prefix + this.userInfoUrl;
        return this;
    }

    /**
     * 批量修改路径，替换掉 /oauth 固定前缀。
     *
     * @param prefix 前缀，例如：/oauth-user、/oauth-admin
     * @return 对象自身
     */
    public ApiName replacePrefix(final String prefix) {
        String oldPrefix = "/oauth";
        this.authorizeUrl = this.authorizeUrl.replaceFirst(oldPrefix, prefix);
        this.oauthLogoutUrl = this.oauthLogoutUrl.replaceFirst(oldPrefix, prefix);
        this.tokenUrl = this.tokenUrl.replaceFirst(oldPrefix, prefix);
        this.userInfoUrl = this.userInfoUrl.replaceFirst(oldPrefix, prefix);
        return this;
    }


    /**
     * 返回对象的字符串表示形式。
     *
     * @return 包含所有API路由的字符串
     */
    @Override
    public String toString() {
        return "ApiName [oauthAuthorizeUrl=" + authorizeUrl + ", ssoOauthLogoutUrl=" + oauthLogoutUrl +
            ", ssoTokenUrl=" + tokenUrl + ", ssoUserInfoUrl=" + userInfoUrl + "]";
    }

}
