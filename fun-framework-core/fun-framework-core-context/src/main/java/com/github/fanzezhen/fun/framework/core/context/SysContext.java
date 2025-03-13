package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.fun.framework.core.exception.ExceptionUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.*;

/**
 * @author fanzezhen
 */
@Slf4j
@Getter
@SuppressWarnings("unused")
public class SysContext {
    private final JSONObject contextMap = new JSONObject(new CaseInsensitiveMap<>());

    /**
     * context map 最大容量
     */
    public static final Integer MAX_CAPACITY = 100;
    /**
     * context map key 或者 value 最大值
     */
    public static final Integer MAX_SIZE = 1024;

    public Object get(String key) {
        return contextMap.get(key);
    }

    public String getStr(String key) {
        return contextMap.getString(key);
    }

    /**
     * （设置名值对。如果Map之前为null，则会被初始化） Put the key-value into the context map;
     * <p/>
     * Initialize the map if it doesn't exist.
     *
     * @param key   键
     * @param value 值
     *
     * @return 之前的值
     */
    public String set(String key, String value) {
        if (key != null && value != null) {
            if (key.length() > MAX_SIZE) {
                throw ExceptionUtil.wrapException("key is more than " + MAX_SIZE + ", i can't set it into the context map");
            } else if (value.length() > MAX_SIZE) {
                throw ExceptionUtil.wrapException("value is more than " + MAX_SIZE + ", i can't set it into the context map");
            } else {
                if (this.getContextMap().size() > MAX_CAPACITY) {
                    throw ExceptionUtil.wrapException("the context map is full, can't set anything");
                } else {
                    this.getContextMap().put(key.toLowerCase(), value);
                }
            }
        } else {
            log.error("key:" + key + " or value:" + value + " is null,i can't set it into the context map");
        }
        return value;
    }

    /**
     * 移除一个key
     *
     * @param key key
     */
    public Object remove(String key) {
        return this.contextMap.remove(key);
    }

    /**
     * 获取当前登录用户的Id
     *
     * @return userId
     */
    public String getUserId() {
        return getStr(ContextConstant.HEADER_USER_ID);
    }

    public void setUserId(String userId) {
        set(ContextConstant.HEADER_USER_ID, userId);
    }

    public String getAccountId() {
        return getStr(ContextConstant.HEADER_ACCOUNT_ID);
    }

    public void setAccountId(String accountId) {
        set(ContextConstant.HEADER_ACCOUNT_ID, accountId);
    }

    public String getAccountName() {
        return getStr(ContextConstant.HEADER_ACCOUNT_NAME);
    }

    public void setAccountName(String accountName) {
        set(ContextConstant.HEADER_ACCOUNT_NAME, accountName);
    }

    public String getProjectId() {
        return getStr(ContextConstant.HEADER_PROJECT_ID);
    }

    public void setProjectId(String projectId) {
        set(ContextConstant.HEADER_PROJECT_ID, projectId);
    }

    public String getAppId() {
        return getStr(ContextConstant.HEADER_APP_CODE);
    }

    public void setAppId(String appId) {
        set(ContextConstant.HEADER_APP_CODE, appId);
    }

    public String getCurrentAppCode() {
        return getStr(ContextConstant.CURRENT_PROJECT_ID_KEY);
    }

    public void setCurrentAppCode(String appCode) {
        set(ContextConstant.CURRENT_PROJECT_ID_KEY, appCode);
    }

    public String getTraceId() {
        return getStr(ContextConstant.HEADER_TRACE_ID);
    }

    public void setTraceId(String traceId) {
        set(ContextConstant.HEADER_TRACE_ID, traceId);
    }

    public String getNodeId() {
        return getStr(ContextConstant.HEADER_NODE_ID);
    }

    public void setNodeId(String nodeId) {
        set(ContextConstant.HEADER_NODE_ID, nodeId);
    }

    /**
     * 获取当前登录用户的name
     *
     * @return username
     */
    public String getUserName() {
        return getStr(ContextConstant.HEADER_USER_NAME);
    }

    public void setUserName(String userName) {
        set(ContextConstant.HEADER_USER_NAME, userName);
    }

    /**
     * 获取当前登录用户的浏览器信息
     *
     * @return userAgent
     */
    public String getUseAgent() {
        return getStr(ContextConstant.HEADER_USER_AGENT);
    }

    /**
     * 获取当前登录用户所属的租户Id
     *
     * @return tenantId
     */
    public String getTenantId() {
        return getStr(ContextConstant.HEADER_TENANT_ID);
    }

    public void setTenantId(String tenantId) {
        set(ContextConstant.HEADER_TENANT_ID, tenantId);
    }

    /**
     * 获取当前登录用户的区域和语言
     * 如果没有设置 返回简体中文(zh_CN)
     *
     * @return locale
     */
    public String getLocale() {
        String locale = getStr(ContextConstant.HEADER_LOCALE);
        if (locale == null || locale.isEmpty()) {
            return ContextConstant.DEFAULT_LOCALE;
        }
        return locale;
    }

    public void setLocale(String locale) {
        set(ContextConstant.HEADER_LOCALE, locale);
    }

    /**
     * 获取当前登录用户的时区设置
     * 如果没有设置 返回服务器默认时区
     *
     * @return 时区
     */
    public TimeZone getTimeZone() {
        String zoneOffset = getStr(ContextConstant.HEADER_TIME_ZONE);
        if (CharSequenceUtil.isBlank(zoneOffset)) {
            return TimeZone.getDefault();
        }
        // 和user profile的timezone的格式匹配
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(Integer.parseInt(zoneOffset) * 3600);
        return TimeZone.getTimeZone(offset);
    }

    public void setTimeZone(String timeZone) {
        set(ContextConstant.HEADER_TIME_ZONE, timeZone);
    }

    /**
     * 获取登录用户的IP地址
     *
     * @return clientIp
     */
    public String getClientIp() {
        return getStr(ContextConstant.HEADER_USER_IP);
    }

    public void setClientIp(String clientIp) {
        set(ContextConstant.HEADER_USER_IP, clientIp);
    }

    public void setUserAgent(String userAgent) {
        set(ContextConstant.HEADER_USER_AGENT, userAgent);
    }

    public String getServerHost() {
        return getStr(ContextConstant.HEADER_SERVER_HOST);
    }

    public void setServerHost(String serverHost) {
        set(ContextConstant.HEADER_SERVER_HOST, serverHost);
    }

    public void removeServerHost() {
        log.debug("removeServerHost {}", this.remove(ContextConstant.HEADER_SERVER_HOST));
    }

    public void clean() {
        contextMap.clear();
    }

    public List<Pair<String, String>> toHeaders() {
        if (this.contextMap.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<Pair<String, String>> pairs = new ArrayList<>();
            this.contextMap.forEach((key, value) -> {
                if (ExceptionUtil.isBlank(value)) {
                    log.warn("header:{}'s value:{} is empty,will not add to headers", key, value);
                } else if (CharSequenceUtil.startWithIgnoreCase(key, ContextConstant.CONTEXT_HEADER_PREFIX)) {
                    log.debug("adding header{" + key + ":" + value + "}");
                    if (!CharSequenceUtil.equalsIgnoreCase(key, ContextConstant.HEADER_ACCOUNT_NAME) && !CharSequenceUtil.equalsIgnoreCase(key, ContextConstant.HEADER_USER_NAME)) {
                        pairs.add(this.convertKey(key, value.toString(), false));
                    } else {
                        pairs.add(this.convertKey(key, value.toString(), true));
                    }
                }
            });
            return pairs;
        }
    }

    private Pair<String, String> convertKey(String name, String value, boolean encoding) {
        return encoding ? Pair.of(name, URLEncoder.encode(value, StandardCharsets.UTF_8)) : Pair.of(name, value);
    }
}

