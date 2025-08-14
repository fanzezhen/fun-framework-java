package com.github.fanzezhen.fun.framework.security.sa.token.oauth;

/**
 * Sa-Token-SSO 单点登录模块 工具类
 */
public final class SaOauthUtil {


    /**
     * 底层 SaSsoTemplate 对象
     */
    private static SaOauthTemplate oauthTemplate = new SaOauthTemplate();

    /**
     * 构造函数私有化
     */
    private SaOauthUtil() {
    }

    /**
     * 获取SaSsoTemplate 对象
     */
    public static SaOauthTemplate getOauthTemplate() {
        return oauthTemplate;
    }

    /**
     * 设置SaSsoTemplate 对象
     */
    public static void setOauthTemplate(SaOauthTemplate template) {
        oauthTemplate = template;
    }

}
