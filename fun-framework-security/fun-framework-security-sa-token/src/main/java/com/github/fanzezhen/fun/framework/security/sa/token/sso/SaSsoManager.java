package com.github.fanzezhen.fun.framework.security.sa.token.sso;

import com.github.fanzezhen.fun.framework.security.sa.token.sso.config.SaSsoConfig;

/**
 * Sa-Token SSO 模块总控类。
 * <p>
 * 管理 SSO 模块的全局配置，使用单例模式确保配置的唯一性。
 * <p>
 * <b>线程安全说明：</b>使用双重检查锁定（Double-Checked Locking）模式，
 * volatile 关键字确保了可见性和禁止指令重排序，在 JDK 5+ 中是线程安全的。
 */
@SuppressWarnings("all")
public final class SaSsoManager {
    /**
     * 私有构造方法，禁止实例化。
     */
    private SaSsoManager() {
    }

    /**
     * SSO 配置对象（单例）。
     * <p>
     * 使用 volatile 确保多线程环境下的可见性和禁止指令重排序。
     */
    private static volatile SaSsoConfig config;

    /**
     * 获取 SSO 配置对象。
     * <p>
     * 如果配置对象未初始化，则创建默认配置对象。
     *
     * @return SSO 配置对象
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
     * 设置 SSO 配置对象。
     *
     * @param config SSO 配置对象
     * @return 设置后的配置对象（支持链式调用）
     */
    @SuppressWarnings("UnusedReturnValue")
    public static SaSsoConfig setConfig(final SaSsoConfig config) {
        SaSsoManager.config = config;
        return config;
    }

}
