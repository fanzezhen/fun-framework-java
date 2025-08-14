package com.github.fanzezhen.fun.framework.security.sa.token.oauth.name;

import lombok.Data;

/**
 * SSO 模块所有参数名称定义
 */
@Data
public class ParamName {

    /**
     * redirect参数名称
     */
    private String grantType = "grant_type";

    /**
     * code参数名称
     */
    private String ticket = "ticket";

    /**
     * back参数名称
     */
    private String redirectUri = "redirect_uri";

    /**
     * mode参数名称
     */
    private String clientSecret = "client_secret";

    /**
     * loginId参数名称
     */
    private String clientId = "client_id";

    /**
     * client参数名称
     */
    private String accessToken = "access_token";

    /**
     * secretkey参数名称
     */
    private String tokenType = "token_type";

    /**
     * Client端单点注销时-回调URL 参数名称
     */
    private String refreshToken = "refresh_token";


    /**
     * 过期
     */
    private String expiresIn = "expires_in";

    /**
     * 响应类型
     */
    private String responseType = "response_type";


    /**
     * 作用域
     */
    private String scope = "scope";

}
