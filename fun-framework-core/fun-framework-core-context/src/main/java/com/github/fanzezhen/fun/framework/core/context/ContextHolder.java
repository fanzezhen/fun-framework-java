package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.properties.ContextConstant;
import com.github.fanzezhen.fun.framework.core.context.properties.FunCoreContextProperties;
import com.github.fanzezhen.fun.framework.core.model.util.ValidUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.IUser;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author fanzezhen
 */
@Slf4j
@SuppressWarnings("unused")
public class ContextHolder {
    private ContextHolder() {
    }

    /**
     * 上下文数据
     */
    private static final ThreadLocal<Context> CONTEXT_MAP = new ThreadLocal<>();
    /**
     * 上下文参数配置
     */
    static FunCoreContextProperties properties = new FunCoreContextProperties();

    public static Context getContext() {
        Context systemContext = CONTEXT_MAP.get();
        if (systemContext == null) {
            systemContext = new Context();
            CONTEXT_MAP.set(systemContext);
        }
        return systemContext;
    }

    public static JSONObject getCopyOfContextMap() {
        return getContext().getCopyOfContextMap();
    }

    public static Context getSysContext() {
        return CONTEXT_MAP.get();
    }

    public static void setContextMap(Map<String, String> contextMap) {
        getContext().putAll(contextMap);
    }

    public static void setContextMap(JSONObject contextMap) {
        getContext().putAll(contextMap);
    }

    public static void setOriginHeaders(JSONObject originHeaders) {
        put(properties.getKey().getOriginHeaders(), originHeaders);
    }

    public static String get(String key) {
        return getContext().getString(key.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getJavaObject(String key) {
        return (T) getContext().get(key.toLowerCase());
    }

    public static <T> T get(String key, Class<T> clazz) {
        return getContext().contextMap.getObject(key.toLowerCase(), clazz);
    }

    public static long getLongOrZero(String key) {
        return getContext().contextMap.getLongValue(key.toLowerCase(), 0);
    }

    public static void put(Context systemContext) {
        CONTEXT_MAP.set(systemContext);
    }

    public static void clear(String key) {
            getContext().remove(key);
    }

    public static void clear() {
        getContext().clear();
    }

    public static void put(String key, Object value) {
        if (key == null) {
            log.warn("key is null, can't set it into the context map");
        } else if (key.length() > Context.MAX_SIZE) {
            throw new ServiceException("key is more than " + Context.MAX_SIZE + ", i can't set it into the context map");
        } else if (value != null && value.toString().length() > Context.MAX_SIZE) {
            throw new ServiceException("value is more than " + Context.MAX_SIZE + ", i can't set it into the context map");
        } else {
            if (getContext().size() > Context.MAX_CAPACITY) {
                throw new ServiceException("the context map is full, can't set anything");
            } else {
                getContext().put(key.toLowerCase(), value);
            }
        }
    }

    /**
     * 获取当前登录用户的Id
     *
     * @return userId
     */
    public static <U> U getLoginUser() {
        return getJavaObject("loginUser");
    }

    public static <K extends Serializable, U extends IUser<K>> void setLoginUser(U loginUser) {
        put("loginUser", loginUser);
        if (loginUser!=null) {
            setUserId(loginUser.getLoginCode());
            setUsername(loginUser.getUsername());
        }
    }

    /**
     * 获取当前登录用户的Id
     *
     * @return userId
     */
    public static String getUserId() {
        return get(properties.getKey().getUserIdWithPrefix());
    }

    public static void setUserId(Serializable userId) {
        put(properties.getKey().getUserIdWithPrefix(), userId);
    }

    public static String getAccountId() {
        return get(properties.getKey().getAccountIdWithPrefix());
    }

    public static void setAccountId(String accountId) {
        put(properties.getKey().getAccountIdWithPrefix(), accountId);
    }

    public static String getAccountName() {
        return get(properties.getKey().getAccountNameWithPrefix());
    }

    public static void setAccountName(String accountName) {
        put(properties.getKey().getAccountNameWithPrefix(), accountName);
    }

    public static String getProjectId() {
        return get(properties.getKey().getProjectIdWithPrefix());
    }

    public static void setProjectId(String projectId) {
        put(properties.getKey().getProjectIdWithPrefix(), projectId);
    }

    public static String getAppId() {
        return get(properties.getKey().getAppCodeWithPrefix());
    }

    public static void setAppId(String appId) {
        put(properties.getKey().getAppCodeWithPrefix(), appId);
    }

    public static String getCurrentAppCode() {
        return get(properties.getKey().getProjectIdWithPrefix());
    }

    public static void setCurrentAppCode(String appCode) {
        put(properties.getKey().getProjectIdWithPrefix(), appCode);
    }

    public static String getTraceId() {
        return get(properties.getKey().getTraceIdWithPrefix());
    }

    public static void setTraceId(String traceId) {
        put(properties.getKey().getTraceIdWithPrefix(), traceId);
    }

    public static String getNodeId() {
        return get(properties.getKey().getNodeIdWithPrefix());
    }

    public static void setNodeId(String nodeId) {
        put(properties.getKey().getNodeIdWithPrefix(), nodeId);
    }

    /**
     * 获取当前登录用户的name
     *
     * @return username
     */
    public static String getUsername() {
        return get(properties.getKey().getUserNameWithPrefix());
    }

    public static void setUsername(String userName) {
        put(properties.getKey().getUserNameWithPrefix(), userName);
    }

    /**
     * 获取当前登录用户的浏览器信息
     *
     * @return userAgent
     */
    public static String getUseAgent() {
        return get(properties.getKey().getDeviceWithPrefix());
    }

    /**
     * 获取当前登录用户所属的租户Id
     *
     * @return tenantId
     */
    public static String getTenantId() {
        return get(properties.getKey().getTenantIdWithPrefix());
    }

    public static void setTenantId(String tenantId) {
        put(properties.getKey().getTenantIdWithPrefix(), tenantId);
    }

    /**
     * 获取当前登录用户的区域和语言
     * 如果没有设置 返回简体中文(zh_CN)
     *
     * @return locale
     */
    public static String getLocale() {
        String locale = get(properties.getKey().getLocaleWithPrefix());
        if (locale == null || locale.isEmpty()) {
            return ContextConstant.DEFAULT_LOCALE;
        }
        return locale;
    }

    public static void setLocale(String locale) {
        put(properties.getKey().getLocaleWithPrefix(), locale);
    }

    /**
     * 获取当前登录用户的时区设置
     * 如果没有设置 返回服务器默认时区
     *
     * @return 时区
     */
    public static TimeZone getTimeZone() {
        String zoneOffset = get(properties.getKey().getTimeZoneWithPrefix());
        if (CharSequenceUtil.isBlank(zoneOffset)) {
            return TimeZone.getDefault();
        }
        // 和user profile的timezone的格式匹配
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(Integer.parseInt(zoneOffset) * 3600);
        return TimeZone.getTimeZone(offset);
    }

    public static void setTimeZone(String timeZone) {
        put(properties.getKey().getTimeZoneWithPrefix(), timeZone);
    }

    /**
     * 获取登录用户的IP地址
     *
     * @return clientIp
     */
    public static String getClientIp() {
        return get(properties.getKey().getUserIpWithPrefix());
    }

    public static void setClientIp(String clientIp) {
        put(properties.getKey().getUserIpWithPrefix(), clientIp);
    }

    public static void setUserAgent(String userAgent) {
        put(properties.getKey().getDeviceWithPrefix(), userAgent);
    }

    public static String getServerHost() {
        return get(properties.getKey().getServerHostWithPrefix());
    }

    public static void setServerHost(String serverHost) {
        put(properties.getKey().getServerHostWithPrefix(), serverHost);
    }

    public static void removeServerHost() {
        getContext().remove(properties.getKey().getServerHostWithPrefix());
    }

    public static void clean() {
        CONTEXT_MAP.remove();
    }

    public static Object remove(String key) {
        return getContext().remove(key);
    }

    public static void put(Map<String, Object> contextMap) {
        if (contextMap != null) {
            contextMap.forEach(ContextHolder::put);
        }
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

    public static List<Pair<String, String>> toHeaders() {
        if (getContext().isEmpty()) {
            return Collections.emptyList();
        } else {
            FunCoreContextProperties.Key headerKey = properties.getKey();
            List<Pair<String, String>> pairs = new ArrayList<>();
            getContext().contextMap.forEach((key, value) -> {
                if (ValidUtil.isBlank(value)) {
                    log.warn("header:{}'s value:{} is empty,will not add to headers", key, value);
                } else if (CharSequenceUtil.startWithIgnoreCase(key, headerKey.getPrefix())) {
                    log.debug("adding header{{}:{}}", key, value);
                    if (!CharSequenceUtil.equalsIgnoreCase(key, headerKey.getAccountNameWithPrefix()) &&
                        !CharSequenceUtil.equalsIgnoreCase(key, headerKey.getUserNameWithPrefix())) {
                        pairs.add(convertKey(key, value.toString(), false));
                    } else {
                        pairs.add(convertKey(key, value.toString(), true));
                    }
                }
            });
            return pairs;
        }
    }

    static Pair<String, String> convertKey(String name, String value, boolean encoding) {
        return encoding ? Pair.of(name, URLEncoder.encode(value, StandardCharsets.UTF_8)) : Pair.of(name, value);
    }

    static void setProperties(FunCoreContextProperties properties) {
        ContextHolder.properties = properties;
    }
}
