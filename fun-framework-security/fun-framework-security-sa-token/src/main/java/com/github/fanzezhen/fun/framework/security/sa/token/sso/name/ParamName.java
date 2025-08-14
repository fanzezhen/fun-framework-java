package com.github.fanzezhen.fun.framework.security.sa.token.sso.name;

import lombok.Data;

/**
 * SSO 模块所有参数名称定义
 */
@Data
public class ParamName {

    /**
     * redirect参数名称
     */
    private String redirect = "redirect";

    /**
     * ticket参数名称
     */
    private String ticket = "token";

    /**
     * back参数名称
     */
    private String back = "back";

    /**
     * mode参数名称
     */
    private String mode = "mode";

    /**
     * loginId参数名称
     */
    private String loginId = "loginId";

    /**
     * client参数名称
     */
    private String client = "platform_id";

    /**
     * secretKey参数名称
     */
    private String secretKey = "secretKey";

    /**
     * Client端单点注销时-回调URL 参数名称
     */
    private String ssoLogoutCall = "ssoLogoutCall";

    /**
     * name
     */
    private String name = "name";

    /**
     * pwd
     */
    private String pwd = "pwd";

    /**
     * 时间戳
     */
    private String timestamp = "timestamp";

    /**
     * nonce
     */
    private String nonce = "nonce";

    /**
     * 签名
     */
    private String sign = "sign";

}
