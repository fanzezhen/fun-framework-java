package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.fun.framework.core.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author zezhen.fan
 */
@Slf4j
@SuppressWarnings("unused")
public class SysContextHolder {
    private SysContextHolder() {
    }

    private static final ThreadLocal<SysContext> CONTEXT_MAP = new ThreadLocal<>();

    public static JSONObject getContextMap() {
        SysContext systemContext = CONTEXT_MAP.get();
        if (systemContext == null) {
            systemContext = new SysContext();
            CONTEXT_MAP.set(systemContext);
        }

        return systemContext.getContextMap();
    }

    public static SysContext getSysContext() {
        return CONTEXT_MAP.get();
    }

    public static void setContextMap(Map<String, String> contextMap) {
        getContextMap().putAll(contextMap);
    }

    public static void setContextMap(JSONObject contextMap) {
        getContextMap().putAll(contextMap);
    }

    public static String get(String key) {
        JSONObject contextMap = getContextMap();
        return contextMap == null ? null : contextMap.getString(key.toLowerCase());
    }

    public static <T> T get(String key, Class<T> clazz) {
        JSONObject contextMap = getContextMap();
        return contextMap == null ? null : contextMap.getObject(key.toLowerCase(), clazz);
    }

    public static long getLongOrZero(String key) {
        JSONObject contextMap = getContextMap();
        return contextMap == null ? 0 : contextMap.getLongValue(key.toLowerCase());
    }

    public static void set(SysContext systemContext) {
        CONTEXT_MAP.set(systemContext);
    }

    public static void clear(String key) {
        JSONObject contextMap = getContextMap();
        if (contextMap != null) {
            contextMap.remove(key);
        }

    }

    public static void set(String key, Object value) {
        if (key == null) {
            log.warn("key is null, can't set it into the context map");
        } else if (key.length() > SysContext.MAX_SIZE) {
            throw ExceptionUtil.wrapException("key is more than " + SysContext.MAX_SIZE + ", i can't set it into the context map");
        } else if (value != null && value.toString().length() > SysContext.MAX_SIZE) {
            throw ExceptionUtil.wrapException("value is more than " + SysContext.MAX_SIZE + ", i can't set it into the context map");
        } else {
            JSONObject contextMap = getContextMap();
            if (contextMap.size() > SysContext.MAX_CAPACITY) {
                throw ExceptionUtil.wrapException("the context map is full, can't set anything");
            } else {
                contextMap.put(key.toLowerCase(), value);
            }
        }
    }

    /**
     * 获取当前登录用户的Id
     *
     * @return userId
     */
    public static String getUserId() {
        return get(ContextConstant.HEADER_USER_ID);
    }

    public static void setUserId(String userId) {
        set(ContextConstant.HEADER_USER_ID, userId);
    }

    public static String getAccountId() {
        return get(ContextConstant.HEADER_ACCOUNT_ID);
    }

    public static void setAccountId(String accountId) {
        set(ContextConstant.HEADER_ACCOUNT_ID, accountId);
    }

    public static String getAccountName() {
        return get(ContextConstant.HEADER_ACCOUNT_NAME);
    }

    public static void setAccountName(String accountName) {
        set(ContextConstant.HEADER_ACCOUNT_NAME, accountName);
    }

    public static String getProjectId() {
        return get(ContextConstant.HEADER_PROJECT_ID);
    }

    public static void setProjectId(String projectId) {
        set(ContextConstant.HEADER_PROJECT_ID, projectId);
    }

    public static String getAppId() {
        return get(ContextConstant.HEADER_APP_CODE);
    }

    public static void setAppId(String appId) {
        set(ContextConstant.HEADER_APP_CODE, appId);
    }

    public static String getCurrentAppCode() {
        return get(ContextConstant.CURRENT_PROJECT_ID_KEY);
    }

    public static void setCurrentAppCode(String appCode) {
        set(ContextConstant.CURRENT_PROJECT_ID_KEY, appCode);
    }

    public static String getTraceId() {
        return get(ContextConstant.HEADER_TRACE_ID);
    }

    public static void setTraceId(String traceId) {
        set(ContextConstant.HEADER_TRACE_ID, traceId);
    }

    public static String getNodeId() {
        return get(ContextConstant.HEADER_NODE_ID);
    }

    public static void setNodeId(String nodeId) {
        set(ContextConstant.HEADER_NODE_ID, nodeId);
    }

    /**
     * 获取当前登录用户的name
     *
     * @return username
     */
    public static String getUserName() {
        return get(ContextConstant.HEADER_USER_NAME);
    }

    public static void setUserName(String userName) {
        set(ContextConstant.HEADER_USER_NAME, userName);
    }

    /**
     * 获取当前登录用户的浏览器信息
     *
     * @return userAgent
     */
    public static String getUseAgent() {
        return get(ContextConstant.HEADER_USER_AGENT);
    }

    /**
     * 获取当前登录用户所属的租户Id
     *
     * @return tenantId
     */
    public static String getTenantId() {
        return get(ContextConstant.HEADER_TENANT_ID);
    }

    public static void setTenantId(String tenantId) {
        set(ContextConstant.HEADER_TENANT_ID, tenantId);
    }

    /**
     * 获取当前登录用户的区域和语言
     * 如果没有设置 返回简体中文(zh_CN)
     *
     * @return locale
     */
    public static String getLocale() {
        String locale = get(ContextConstant.HEADER_LOCALE);
        if (locale == null || locale.isEmpty()) {
            return ContextConstant.DEFAULT_LOCALE;
        }
        return locale;
    }

    public static void setLocale(String locale) {
        set(ContextConstant.HEADER_LOCALE, locale);
    }

    /**
     * 获取当前登录用户的时区设置
     * 如果没有设置 返回服务器默认时区
     *
     * @return 时区
     */
    public static TimeZone getTimeZone() {
        String zoneOffset = get(ContextConstant.HEADER_TIME_ZONE);
        if (CharSequenceUtil.isBlank(zoneOffset)) {
            return TimeZone.getDefault();
        }
        // 和user profile的timezone的格式匹配
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(Integer.parseInt(zoneOffset) * 3600);
        return TimeZone.getTimeZone(offset);
    }

    public static void setTimeZone(String timeZone) {
        set(ContextConstant.HEADER_TIME_ZONE, timeZone);
    }

    /**
     * 获取登录用户的IP地址
     *
     * @return clientIp
     */
    public static String getClientIp() {
        return get(ContextConstant.HEADER_USER_IP);
    }

    public static void setClientIp(String clientIp) {
        set(ContextConstant.HEADER_USER_IP, clientIp);
    }

    public static void setUserAgent(String userAgent) {
        set(ContextConstant.HEADER_USER_AGENT, userAgent);
    }

    public static String getServerHost() {
        return get(ContextConstant.HEADER_SERVER_HOST);
    }

    public static void setServerHost(String serverHost) {
        set(ContextConstant.HEADER_SERVER_HOST, serverHost);
    }

    public static void removeServerHost() {
        getContextMap().remove(ContextConstant.HEADER_SERVER_HOST);
    }

    public static void clean() {
        CONTEXT_MAP.remove();
    }

    public static void remove(String key) {
        getContextMap().remove(key);
    }

    public static String getHeaderJsonStr(String[] headerArgs) {
        if (ArrayUtil.isEmpty(headerArgs)) {
            return CharSequenceUtil.EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String headerKey : headerArgs) {
            if (CharSequenceUtil.isBlank(headerKey)) {
                continue;
            }
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(StrPool.COMMA);
            }
            stringBuilder.append(headerKey).append("=").append(get(headerKey));
        }
        return stringBuilder.toString();
    }
}
