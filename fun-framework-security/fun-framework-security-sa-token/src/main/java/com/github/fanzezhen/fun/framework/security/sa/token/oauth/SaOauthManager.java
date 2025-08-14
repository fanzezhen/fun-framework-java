package com.github.fanzezhen.fun.framework.security.sa.token.oauth;

import com.github.fanzezhen.fun.framework.security.sa.token.oauth.config.SaOauthConfig ;

/**
 * Sa-Token-SSO 模块 总控类
 */
public final class SaOauthManager {

    /**
     * 构造函数私有化
     */
    private SaOauthManager() {
    }

    /**
     * Sso 配置 Bean
     */
    private static volatile SaOauthConfig config;


    /**
     * 获取配置
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
     * 设置配置
     */
    public static void setConfig(SaOauthConfig config) {
        SaOauthManager.config = config;
    }

}
