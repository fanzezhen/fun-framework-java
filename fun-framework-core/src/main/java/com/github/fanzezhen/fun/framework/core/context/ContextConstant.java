package com.github.fanzezhen.fun.framework.core.context;

import java.util.Locale;

/**
 * @author zezhen.fan
 */
public interface ContextConstant {
    /**
     * 当前的项目Id的键
     */
    String CURRENT_PROJECT_ID_KEY = "project-id";
    /**
     * 默认的区域
     */
    String DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE.toString();
    String HEADER_USER_AGENT = "User-Agent";
    /***************公共header********************/
    String CONTEXT_HEADER_PREFIX = "common-header-";
    String HEADER_TOKEN = CONTEXT_HEADER_PREFIX + "token";
    /**
     * 应用Id
     */
    String HEADER_APP_CODE = CONTEXT_HEADER_PREFIX + "app-code";
    String HEADER_TENANT_ID = CONTEXT_HEADER_PREFIX + "tenant-id";
    String HEADER_CLIENT_ID = CONTEXT_HEADER_PREFIX + "client-id";
    String HEADER_PLATFORM = CONTEXT_HEADER_PREFIX + "platform";
    String HEADER_TOKEN_ENV = CONTEXT_HEADER_PREFIX + "Token-env";
    /**
     * 项目Id
     */
    String HEADER_PROJECT_ID = CONTEXT_HEADER_PREFIX + "project-id";
    /**
     * 用户Id
     */
    String HEADER_USER_ID = CONTEXT_HEADER_PREFIX + "user-id";
    /**
     * 用户姓名
     */
    String HEADER_USER_NAME = CONTEXT_HEADER_PREFIX + "user-name";
    /**
     * 客户端IP
     */
    String HEADER_USER_IP = CONTEXT_HEADER_PREFIX + "user-ip";
    /**
     * 用户账号
     */
    String HEADER_ACCOUNT_ID = CONTEXT_HEADER_PREFIX + "account-id";
    /**
     * 用户名称
     */
    String HEADER_ACCOUNT_NAME = CONTEXT_HEADER_PREFIX + "account-name";
    /**
     * 区域和语言
     */
    String HEADER_LOCALE = CONTEXT_HEADER_PREFIX + "locale";
    /**
     * 时区
     */
    String HEADER_TIME_ZONE = CONTEXT_HEADER_PREFIX + "TimeZone";
    /**
     * 系统域名
     */
    String HEADER_SERVER_HOST = CONTEXT_HEADER_PREFIX + "ServerHost";
    /**
     * 设备型号
     */
    String HEADER_DEVICE = CONTEXT_HEADER_PREFIX + "Device";
    /**
     * TraceId
     */
    String HEADER_TRACE_ID = CONTEXT_HEADER_PREFIX + "TraceId";
    /**
     * NodeId
     */
    String HEADER_NODE_ID = CONTEXT_HEADER_PREFIX + "NodeId";

}
