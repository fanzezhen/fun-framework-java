package com.github.fanzezhen.fun.framework.security.sa.token.oauth;

import com.github.fanzezhen.fun.framework.security.sa.token.oauth.config.SaOauthConfig ;

/**
 * Sa-Token OAuth2.0 模块总控类。
 * <p>
 * 管理 OAuth2.0 模块的全局配置，使用单例模式确保配置的唯一性。
 */
public final class SaOauthManager {

    /**
     * 私有构造方法，禁止实例化。
     */
    private SaOauthManager() {
    }

    /**
     * OAuth2.0 配置对象（单例）。
     */
    private static volatile SaOauthConfig config;

    /**
     * 获取 OAuth2.0 配置对象。
     * <p>
     * 如果配置对象未初始化，则创建默认配置对象。
     *
     * @return OAuth2.0 配置对象
     */
    public static SaOauthConfig getConfig() {
        if (config == null) {
            synchronized (SaOauthManager.class) {
                if (config == null) {
                    setConfig(new SaOauthConfig());
                }
            }
        }
        return config;
    }

    /**
     * 设置 OAuth2.0 配置对象。
     *
     * @param config OAuth2.0 配置对象
     */
    public static void setConfig(final SaOauthConfig config) {
        SaOauthManager.config = config;
    }

}
