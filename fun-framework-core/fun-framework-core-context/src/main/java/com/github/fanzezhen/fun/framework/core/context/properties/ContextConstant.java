package com.github.fanzezhen.fun.framework.core.context.properties;

import java.util.Locale;

/**
 * 上下文常量类.
 * <p>
 * 定义上下文模块使用的默认常量，包括请求头Key、区域语言等。
 */
public final class ContextConstant {

    /**
     * 默认的区域（简体中文）.
     */
    public static final String DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE.toString();

    /**
     * 公共请求头的默认前缀.
     */
    public static final String DEFAULT_HEADER_PREFIX = "fun-";
    /**
     * Token请求头Key.
     */
    public static final String DEFAULT_HEADER_TOKEN = "token";

    /**
     * 应用ID请求头Key.
     */
    public static final String DEFAULT_HEADER_APP_CODE = "app-code";
    /**
     * 租户ID请求头Key.
     */
    public static final String DEFAULT_HEADER_TENANT_ID = "tenant-id";
    /**
     * 客户端标识请求头Key.
     */
    public static final String DEFAULT_HEADER_CLIENT_CODE = "client-code";

    /**
     * 项目ID请求头Key.
     */
    public static final String DEFAULT_HEADER_PROJECT_ID = "project-id";

    /**
     * 用户ID请求头Key.
     */
    public static final String DEFAULT_HEADER_USER_ID = "user-id";

    /**
     * 用户姓名请求头Key.
     */
    public static final String DEFAULT_HEADER_USER_NAME = "user-name";

    /**
     * 客户端IP请求头Key.
     */
    public static final String DEFAULT_HEADER_USER_IP = "user-ip";

    /**
     * 用户账号请求头Key.
     */
    public static final String DEFAULT_HEADER_ACCOUNT_ID = "account-id";

    /**
     * 用户名称请求头Key.
     */
    public static final String DEFAULT_HEADER_ACCOUNT_NAME = "account-name";

    /**
     * 区域和语言请求头Key.
     */
    public static final String DEFAULT_HEADER_LOCALE = "locale";

    /**
     * 时区请求头Key.
     */
    public static final String DEFAULT_HEADER_TIME_ZONE = "Time-Zone";

    /**
     * 系统域名请求头Key.
     */
    public static final String DEFAULT_HEADER_SERVER_HOST = "Server-Host";

    /**
     * 设备型号请求头Key.
     */
    public static final String DEFAULT_HEADER_DEVICE = "Device";
    /**
     * 日志跟踪标识请求头Key.
     */
    public static final String DEFAULT_HEADER_TRACE_ID = "trace-id";

    /**
     * 节点ID请求头Key.
     */
    public static final String DEFAULT_HEADER_NODE_ID = "Node-Id";
    /**
     * User-Agent请求头Key.
     */
    public static final String DEFAULT_HEADER_USER_AGENT = "User-Agent";

    /**
     * 私有构造函数，防止实例化.
     */
    private ContextConstant() {
        throw new UnsupportedOperationException("Utility class");
    }
}
