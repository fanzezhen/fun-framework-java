package com.github.fanzezhen.fun.framework.core.context;

import java.util.Locale;

/**
 * @author fanzezhen
 */
public class ContextConstant {
    /**
     * 当前的项目Id的键
     */
    public static final String CURRENT_PROJECT_ID_KEY = "project-id";

    /**
     * 默认的区域
     */
    public static final String DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE.toString();

    public static final String HEADER_USER_AGENT = "User-Agent";

    /***************公共header********************/
    public static final String CONTEXT_HEADER_PREFIX = "common-header-";
    public static final String HEADER_TOKEN = CONTEXT_HEADER_PREFIX + "token";

    /**
     * 应用Id
     */
    public static final String HEADER_APP_CODE = CONTEXT_HEADER_PREFIX + "app-code";

    public static final String HEADER_TENANT_ID = CONTEXT_HEADER_PREFIX + "tenant-id";
    public static final String HEADER_CLIENT_ID = CONTEXT_HEADER_PREFIX + "client-id";
    public static final String HEADER_PLATFORM = CONTEXT_HEADER_PREFIX + "platform";
    public static final String HEADER_TOKEN_ENV = CONTEXT_HEADER_PREFIX + "Token-env";

    /**
     * 项目Id
     */
    public static final String HEADER_PROJECT_ID = CURRENT_PROJECT_ID_KEY;

    /**
     * 用户Id
     */
    public static final String HEADER_USER_ID = CONTEXT_HEADER_PREFIX + "user-id";

    /**
     * 用户姓名
     */
    public static final String HEADER_USER_NAME = CONTEXT_HEADER_PREFIX + "user-name";

    /**
     * 客户端IP
     */
    public static final String HEADER_USER_IP = CONTEXT_HEADER_PREFIX + "user-ip";

    /**
     * 用户账号
     */
    public static final String HEADER_ACCOUNT_ID = CONTEXT_HEADER_PREFIX + "account-id";

    /**
     * 用户名称
     */
    public static final String HEADER_ACCOUNT_NAME = CONTEXT_HEADER_PREFIX + "account-name";

    /**
     * 区域和语言
     */
    public static final String HEADER_LOCALE = CONTEXT_HEADER_PREFIX + "locale";

    /**
     * 时区
     */
    public static final String HEADER_TIME_ZONE = CONTEXT_HEADER_PREFIX + "TimeZone";

    /**
     * 系统域名
     */
    public static final String HEADER_SERVER_HOST = CONTEXT_HEADER_PREFIX + "ServerHost";

    /**
     * 设备型号
     */
    public static final String HEADER_DEVICE = CONTEXT_HEADER_PREFIX + "Device";

    /**
     * TraceId
     */
    public static final String HEADER_TRACE_ID = CONTEXT_HEADER_PREFIX + "TraceId";

    /**
     * NodeId
     */
    public static final String HEADER_NODE_ID = CONTEXT_HEADER_PREFIX + "NodeId";

    // 添加私有构造函数
    private ContextConstant() {
        throw new UnsupportedOperationException("Utility class");
    }
}
