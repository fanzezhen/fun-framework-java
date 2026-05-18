package com.github.fanzezhen.fun.framework.security.sa.token.oauth.name;

import lombok.Data;

/**
 * OAuth2.0 模块所有参数名称定义。
 * <p>
 * 定义 OAuth2.0 协议中使用的各个参数名称。
 */
@Data
public class ParamName {

    /**
     * 授权类型参数名称。
     */
    private String grantType = "grant_type";

    /**
     * 票据参数名称。
     */
    private String ticket = "ticket";

    /**
     * 重定向URI参数名称。
     */
    private String redirectUri = "redirect_uri";

    /**
     * 客户端密钥参数名称。
     */
    private String clientSecret = "client_secret";

    /**
     * 客户端ID参数名称。
     */
    private String clientId = "client_id";

    /**
     * 访问令牌参数名称。
     */
    private String accessToken = "access_token";

    /**
     * 令牌类型参数名称。
     */
    private String tokenType = "token_type";

    /**
     * 刷新令牌参数名称。
     */
    private String refreshToken = "refresh_token";

    /**
     * 过期时间参数名称。
     */
    private String expiresIn = "expires_in";

    /**
     * 响应类型参数名称。
     */
    private String responseType = "response_type";

    /**
     * 授权范围参数名称。
     */
    private String scope = "scope";

}
