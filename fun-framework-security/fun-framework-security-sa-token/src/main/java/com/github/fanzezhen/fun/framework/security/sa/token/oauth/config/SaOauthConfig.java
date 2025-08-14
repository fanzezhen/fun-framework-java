package com.github.fanzezhen.fun.framework.security.sa.token.oauth.config;


import lombok.Data;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * Sa-Token Oauth 单点登录模块 配置类 Model
 */
@Data
public class SaOauthConfig implements Serializable {

    /**
     * 毫秒单位
     */
    private static final int MILL_SECONDS = 1000;

    /**
     * 秒单位
     */
    private static final int SECONDS = 60;

    /**
     * 十分钟
     */
    private static final long TEN_MINUTES = 10L;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端secret
     */
    private String clientSecret;

    /**
     * 配置 Server 端单点登录授权地址
     */
    private String grantType = "authorization_code";

    /**
     * tokenUrl
     */
    private String tokenUrl;

    /**
     * userInfoUrl
     */
    private String userInfoUrl;

    /**
     * authorizeUrl
     */
    private String authorizeUrl;

    /**
     * logoutUrl
     */
    private String logoutUrl;

    /**
     * redirectUri
     */
    private String redirectUri;

    /**
     * scope
     */
    private String scope;

    /**
     * 解析规则路径
     */
    private String jsonPath;
    /**
     * 返回类型
     */
    private String responseType;

    /**
     * 接口调用时的时间戳允许的差距（单位：ms），-1代表不校验差距 10分钟
     */
    private long timestampDisparity = MILL_SECONDS * SECONDS * TEN_MINUTES;

    /**
     * Oauth-Client端：发送Http请求的处理函数
     */
    private UnaryOperator<String> sendHttp = url -> {
        throw new SecurityException("请配置 Http 请求处理器");
    };

}
