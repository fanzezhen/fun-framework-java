package com.github.fanzezhen.fun.framework.security.sa.token.oauth.config;


import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * Sa-Token OAuth2.0 单点登录模块配置类。
 * <p>
 * 提供 OAuth2.0 客户端和服务端的配置参数。
 */
@Data
public class SaOauthConfig implements Serializable {

    /**
     * 十分钟（单位：分钟）。
     */
    private static final long TEN_MINUTES = 10L;

    /**
     * 客户端ID。
     */
    private String clientId;

    /**
     * 客户端密钥。
     */
    private String clientSecret;

    /**
     * 授权类型。
     * <p>
     * 默认为 "authorization_code"（授权码模式）。
     */
    private String grantType = "authorization_code";

    /**
     * Token 获取地址。
     */
    private String tokenUrl;

    /**
     * 用户信息获取地址。
     */
    private String userInfoUrl;

    /**
     * 授权地址。
     */
    private String authorizeUrl;

    /**
     * 登出地址。
     */
    private String logoutUrl;

    /**
     * 重定向URI。
     */
    private String redirectUri;

    /**
     * 授权范围。
     */
    private String scope;

    /**
     * JSON 解析规则路径。
     */
    private String jsonPath;

    /**
     * 响应类型。
     */
    private String responseType;

    /**
     * 接口调用时的时间戳允许的差距（单位：毫秒）。
     * <p>
     * -1 代表不校验差距，默认为 10 分钟。
     */
    private long timestampDisparity = NormalTypeConstant.INT_MILLIS_PER_SECOND * NormalTypeConstant.INT_ONE_MINUTE_SECONDS * TEN_MINUTES;

    /**
     * OAuth2.0 Client端发送HTTP请求的处理函数。
     * <p>
     * 默认实现会抛出异常，使用时需要配置具体的HTTP请求实现。
     */
    private UnaryOperator<String> sendHttp = url -> {
        throw new SecurityException("请配置 Http 请求处理器");
    };

}
