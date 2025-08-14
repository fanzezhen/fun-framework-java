package com.github.fanzezhen.fun.framework.security.sa.token.sso;

import com.github.fanzezhen.fun.framework.security.sa.token.sso.config.SaSsoConfig;

/**
 * Sa-Token-SSO 模块 总控类
 */
public final class SaSsoManager {
    /**
     * 私有化构造函数
     */
    private SaSsoManager() {
    }

    /**
     * Sso 配置 Bean
     */
    private static volatile SaSsoConfig config;


    /**
     * 获取SaSsoConfig配置
     */
    public static SaSsoConfig getConfig() {
        if (config == null) {
            synchronized (SaSsoManager.class) {
                if (config == null) {
                    setConfig(new SaSsoConfig());
                }
            }
        }
        return config;
    }


    /**
     * 设置SaSsoConfig配置
     */
    public static SaSsoConfig setConfig(SaSsoConfig config) {
        SaSsoManager.config = config;
        return config;
    }

}
