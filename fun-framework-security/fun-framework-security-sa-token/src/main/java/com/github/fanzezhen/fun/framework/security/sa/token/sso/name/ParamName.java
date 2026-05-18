package com.github.fanzezhen.fun.framework.security.sa.token.sso.name;

import lombok.Data;

/**
 * SSO 模块所有参数名称定义。
 * <p>
 * 定义 SSO 协议中使用的各个参数名称。
 */
@Data
public class ParamName {

    /**
     * 重定向地址参数名称。
     */
    private String redirect = "redirect";

    /**
     * Ticket 参数名称。
     */
    private String ticket = "token";

    /**
     * 回调地址参数名称。
     */
    private String back = "back";

    /**
     * 模式参数名称。
     */
    private String mode = "mode";

    /**
     * 登录ID参数名称。
     */
    private String loginId = "loginId";

    /**
     * 客户端标识参数名称。
     */
    private String client = "platform_id";

    /**
     * 密钥参数名称。
     */
    private String secretKey = "secretKey";

    /**
     * Client端单点注销回调URL参数名称。
     */
    private String ssoLogoutCall = "ssoLogoutCall";

    /**
     * 用户名参数名称。
     */
    private String name = "name";

    /**
     * 密码参数名称。
     */
    private String pwd = "pwd";

    /**
     * 时间戳参数名称。
     */
    private String timestamp = "timestamp";

    /**
     * 随机字符串参数名称。
     */
    private String nonce = "nonce";

    /**
     * 签名参数名称。
     */
    private String sign = "sign";

}
