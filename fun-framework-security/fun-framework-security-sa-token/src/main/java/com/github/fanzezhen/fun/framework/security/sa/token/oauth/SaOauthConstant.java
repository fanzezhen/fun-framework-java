package com.github.fanzezhen.fun.framework.security.sa.token.oauth;

/**
 * Sa-Token OAuth2.0 模块相关常量。
 * <p>
 * 定义 OAuth2.0 单点登录中使用的常量值。
 */
public final class SaOauthConstant {

    /**
     * Client端单点注销回调URL的Set集合在Session中的key。
     */
    public static final String SLO_CALLBACK_SET_KEY = "SLO_CALLBACK_SET_KEY_";

    /**
     * 表示操作成功的返回结果。
     */
    public static final String OK = "ok";

    /**
     * 表示当前系统自己。
     */
    public static final String SELF = "self";

    /**
     * 表示请求没有得到任何有效处理的返回结果。
     */
    public static final String NOT_HANDLE = "{\"msg\": \"not handle\"}";

    /**
     * 私有构造方法，禁止实例化。
     */
    private SaOauthConstant() {
    }

}
