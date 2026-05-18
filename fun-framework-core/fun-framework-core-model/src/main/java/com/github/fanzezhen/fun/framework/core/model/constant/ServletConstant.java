package com.github.fanzezhen.fun.framework.core.model.constant;

/**
 * Servlet 常量
 * <p>
 * 定义 Servlet 相关的常量，如请求头名称等。
 * </p>
 */
public class ServletConstant {
    /**
     * 工具类不允许实例化
     */
    private ServletConstant() {
    }

    /**
     * User-Agent 请求头名称
     */
    public static final String USER_AGENT = "User-Agent";

    /**
     * X-Forwarded-For 请求头名称（用于获取客户端真实 IP）
     */
    public static final String X_FORWARDED_FOR = "x-forwarded-for";

    /**
     * Proxy-Client-IP 请求头名称（用于获取代理客户端 IP）
     */
    public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";

    /**
     * WL-Proxy-Client-IP 请求头名称（WebLogic 代理客户端 IP）
     */
    public static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

}
