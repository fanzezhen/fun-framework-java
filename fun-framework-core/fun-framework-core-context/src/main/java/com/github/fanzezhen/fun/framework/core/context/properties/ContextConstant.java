package com.github.fanzezhen.fun.framework.core.context.properties;

import java.util.Locale;

/**
 * @author fanzezhen
 */
public class ContextConstant {

    /**
     * 默认的区域
     */
    public static final String DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE.toString();

    /***************公共请求头的key********************/
    public static final String DEFAULT_HEADER_PREFIX = "fun-";
    /**
     * token
     */
    public static final String DEFAULT_HEADER_TOKEN = "token";

    /**
     * 应用Id
     */
    public static final String DEFAULT_HEADER_APP_CODE = "app-code";
    /**
     * 租户id
     */
    public static final String DEFAULT_HEADER_TENANT_ID = "tenant-id";
    /**
     * 客户端标识
     */
    public static final String DEFAULT_HEADER_CLIENT_CODE = "client-code";

    /**
     * 项目Id
     */
    public static final String DEFAULT_HEADER_PROJECT_ID = "project-id";

    /**
     * 用户Id
     */
    public static final String DEFAULT_HEADER_USER_ID = "user-id";

    /**
     * 用户姓名
     */
    public static final String DEFAULT_HEADER_USER_NAME = "user-name";

    /**
     * 客户端IP
     */
    public static final String DEFAULT_HEADER_USER_IP = "user-ip";

    /**
     * 用户账号
     */
    public static final String DEFAULT_HEADER_ACCOUNT_ID = "account-id";

    /**
     * 用户名称
     */
    public static final String DEFAULT_HEADER_ACCOUNT_NAME = "account-name";

    /**
     * 区域和语言
     */
    public static final String DEFAULT_HEADER_LOCALE = "locale";

    /**
     * 时区
     */
    public static final String DEFAULT_HEADER_TIME_ZONE = "Time-Zone";

    /**
     * 系统域名
     */
    public static final String DEFAULT_HEADER_SERVER_HOST = "Server-Host";

    /**
     * 设备型号
     */
    public static final String DEFAULT_HEADER_DEVICE = "Device";
    /**
     * 日志跟踪标识
     */
    public static final String DEFAULT_HEADER_TRACE_ID = "trace-id";

    /**
     * NodeId
     */
    public static final String DEFAULT_HEADER_NODE_ID = "Node-Id";
    /**
     * User-Agent
     */
    public static final String DEFAULT_HEADER_USER_AGENT = "User-Agent";

    // 添加私有构造函数
    private ContextConstant() {
        throw new UnsupportedOperationException("Utility class");
    }
}
