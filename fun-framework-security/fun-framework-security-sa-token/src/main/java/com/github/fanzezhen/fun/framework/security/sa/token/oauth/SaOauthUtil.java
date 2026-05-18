package com.github.fanzezhen.fun.framework.security.sa.token.oauth;

/**
 * Sa-Token OAuth2.0 单点登录模块工具类。
 * <p>
 * 提供 OAuth2.0 模板对象的访问和设置方法。
 */
@SuppressWarnings("lombok")
public final class SaOauthUtil {

    /**
     * OAuth2.0 模板对象。
     */
    private static SaOauthTemplate oauthTemplate = new SaOauthTemplate();

    /**
     * 私有构造方法，禁止实例化。
     */
    private SaOauthUtil() {
    }

    /**
     * 获取 OAuth2.0 模板对象。
     *
     * @return OAuth2.0 模板对象
     */
    public static SaOauthTemplate getOauthTemplate() {
        return oauthTemplate;
    }

    /**
     * 设置 OAuth2.0 模板对象。
     *
     * @param template OAuth2.0 模板对象
     */
    public static void setOauthTemplate(final SaOauthTemplate template) {
        oauthTemplate = template;
    }

}
