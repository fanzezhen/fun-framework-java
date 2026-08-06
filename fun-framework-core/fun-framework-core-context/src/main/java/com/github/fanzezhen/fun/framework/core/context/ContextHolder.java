package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.github.fanzezhen.fun.framework.core.context.properties.ContextConstant;
import com.github.fanzezhen.fun.framework.core.context.properties.FunCoreContextProperties;
import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;
import com.github.fanzezhen.fun.framework.core.model.util.ValidUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.common.IUser;
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
 * 线程上下文管理器，用于在请求链路中传递用户、租户、追踪等信息.
 * <p>
 * 基于ThreadLocal实现，保证线程隔离。适用场景：
 * - HTTP请求链路中传递登录用户、租户ID等上下文信息
 * - 微服务调用时透传traceId、nodeId等追踪信息
 * - 跨层级方法调用时避免参数传递
 * <p>
 * 线程安全性：ThreadLocal保证线程隔离，但需要注意：
 * - 请求结束后必须调用 {@link #clean()} 清理，避免内存泄漏（通常由Filter/Interceptor自动处理）
 * - 使用线程池时需手动传递上下文（参考 {@link #toHeaders()}）
 * <p>
 * 容量限制：单个key/value不超过1000字符，总容量不超过100个键值对，防止上下文膨胀导致内存问题
 */
@Slf4j
@SuppressWarnings("unused")
public final class ContextHolder {
    private ContextHolder() {
    }

    /**
     * 上下文数据存储（ThreadLocal隔离，请求结束后务必调用clean()清理）.
     */
    private static final TransmittableThreadLocal<Context> CONTEXT_MAP = new TransmittableThreadLocal<>();
    /**
     * 上下文参数配置.
     */
    private static FunCoreContextProperties properties = new FunCoreContextProperties();

    /**
     * 获取当前线程的上下文对象.
     * <p>
     * 如果不存在则自动创建。
     *
     * @return 当前线程的上下文对象
     */
    public static Context getContext() {
        Context systemContext = CONTEXT_MAP.get();
        if (systemContext == null) {
            systemContext = new Context();
            CONTEXT_MAP.set(systemContext);
        }
        return systemContext;
    }

    /**
     * 获取上下文数据的副本.
     *
     * @return 上下文数据的克隆对象
     */
    public static JSONObject getCopyOfContextMap() {
        return getContext().getCopyOfContextMap();
    }

    /**
     * 获取当前线程的上下文对象（可能为null）.
     *
     * @return 当前线程的上下文对象，如果不存在则返回null
     */
    public static Context getSysContext() {
        return CONTEXT_MAP.get();
    }

    /**
     * 批量设置上下文数据.
     *
     * @param contextMap 上下文数据Map
     */
    public static void setContextMap(final Map<String, String> contextMap) {
        getContext().putAll(contextMap);
    }

    /**
     * 批量设置上下文数据.
     *
     * @param contextMap 上下文数据JSONObject
     */
    public static void setContextMap(final JSONObject contextMap) {
        getContext().putAll(contextMap);
    }

    /**
     * 设置原始请求头.
     *
     * @param originHeaders 原始请求头数据
     */
    public static void setOriginHeaders(final JSONObject originHeaders) {
        put(properties.getKey().getOriginHeaders(), originHeaders);
    }

    /**
     * 根据Key获取上下文值.
     *
     * @param key 键
     * @return 字符串值
     */
    public static String get(final String key) {
        return getContext().getString(key.toLowerCase());
    }

    /**
     * 根据Key获取Java对象.
     *
     * @param <T> 对象类型
     * @param key 键
     * @return 对象实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getJavaObject(final String key) {
        return (T) getContext().get(key.toLowerCase());
    }

    /**
     * 根据Key获取指定类型的对象.
     *
     * @param <T>   对象类型
     * @param key   键
     * @param clazz 目标类型
     * @return 对象实例
     */
    public static <T> T get(final String key, final Class<T> clazz) {
        return getContext().getContextMap().getObject(key.toLowerCase(), clazz);
    }

    /**
     * 根据Key获取Long值，如果不存在则返回0.
     *
     * @param key 键
     * @return Long值，不存在则返回0
     */
    public static long getLongOrZero(final String key) {
        return getContext().getContextMap().getLongValue(key.toLowerCase(), 0);
    }

    /**
     * 设置当前线程的上下文对象.
     *
     * @param systemContext 上下文对象
     */
    public static void put(final Context systemContext) {
        CONTEXT_MAP.set(systemContext);
    }

    /**
     * 清除指定Key的上下文数据.
     *
     * @param key 键
     */
    public static void clear(final String key) {
        getContext().remove(key);
    }

    /**
     * 清空当前线程的所有上下文数据.
     */
    public static void clear() {
        getContext().clear();
    }

    /**
     * 添加键值对到上下文.
     * <p>
     * 限制单个key/value为1000字符，因为上下文用于传递轻量元数据（userId、traceId等），
     * 过大的值（如完整JSON文档）会导致跨服务调用时HTTP Header膨胀，甚至超出服务器限制（Nginx默认8KB）
     *
     * @param key   键
     * @param value 值
     */
    public static void put(final String key, final Object value) {
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
     * 获取当前登录用户对象.
     *
     * @param <U> 用户对象类型
     * @return 用户对象
     */
    public static <U> U getLoginUser() {
        return getJavaObject("loginUser");
    }

    /**
     * 设置当前登录用户对象.
     *
     * @param <K>       用户ID类型
     * @param <U>       用户对象类型
     * @param loginUser 用户对象
     */
    public static <K extends Serializable, U extends IUser<K>>
    void setLoginUser(final U loginUser) {
        put("loginUser", loginUser);
        if (loginUser != null) {
            setUserId(loginUser.getLoginCode());
            setUsername(loginUser.getUsername());
        }
    }

    /**
     * 获取当前登录用户的ID.
     *
     * @return 用户ID
     */
    public static String getUserId() {
        return get(properties.getKey().getUserIdWithPrefix());
    }

    /**
     * 设置当前登录用户的ID.
     *
     * @param userId 用户ID
     */
    public static void setUserId(final Serializable userId) {
        put(properties.getKey().getUserIdWithPrefix(), userId);
    }

    /**
     * 获取账户ID.
     *
     * @return 账户ID
     */
    public static String getAccountId() {
        return get(properties.getKey().getAccountIdWithPrefix());
    }

    /**
     * 设置账户ID.
     *
     * @param accountId 账户ID
     */
    public static void setAccountId(final String accountId) {
        put(properties.getKey().getAccountIdWithPrefix(), accountId);
    }

    /**
     * 获取账户名称.
     *
     * @return 账户名称
     */
    public static String getAccountName() {
        return get(properties.getKey().getAccountNameWithPrefix());
    }

    /**
     * 设置账户名称.
     *
     * @param accountName 账户名称
     */
    public static void setAccountName(final String accountName) {
        put(properties.getKey().getAccountNameWithPrefix(), accountName);
    }

    /**
     * 获取项目ID.
     *
     * @return 项目ID
     */
    public static String getProjectId() {
        return get(properties.getKey().getProjectIdWithPrefix());
    }

    /**
     * 设置项目ID.
     *
     * @param projectId 项目ID
     */
    public static void setProjectId(final String projectId) {
        put(properties.getKey().getProjectIdWithPrefix(), projectId);
    }

    /**
     * 获取应用ID.
     *
     * @return 应用ID
     */
    public static String getAppId() {
        return get(properties.getKey().getAppCodeWithPrefix());
    }

    /**
     * 设置应用ID.
     *
     * @param appId 应用ID
     */
    public static void setAppId(final String appId) {
        put(properties.getKey().getAppCodeWithPrefix(), appId);
    }

    /**
     * 获取当前应用编码.
     *
     * @return 应用编码
     */
    public static String getCurrentAppCode() {
        return get(properties.getKey().getProjectIdWithPrefix());
    }

    /**
     * 设置当前应用编码.
     *
     * @param appCode 应用编码
     */
    public static void setCurrentAppCode(final String appCode) {
        put(properties.getKey().getProjectIdWithPrefix(), appCode);
    }

    /**
     * 获取追踪ID.
     *
     * @return 追踪ID
     */
    public static String getTraceId() {
        return get(properties.getKey().getTraceIdWithPrefix());
    }

    /**
     * 设置追踪ID.
     *
     * @param traceId 追踪ID
     */
    public static void setTraceId(final String traceId) {
        put(properties.getKey().getTraceIdWithPrefix(), traceId);
    }

    /**
     * 获取节点ID.
     *
     * @return 节点ID
     */
    public static String getNodeId() {
        return get(properties.getKey().getNodeIdWithPrefix());
    }

    /**
     * 设置节点ID.
     *
     * @param nodeId 节点ID
     */
    public static void setNodeId(final String nodeId) {
        put(properties.getKey().getNodeIdWithPrefix(), nodeId);
    }

    /**
     * 获取当前登录用户的用户名.
     *
     * @return 用户名
     */
    public static String getUsername() {
        return get(properties.getKey().getUserNameWithPrefix());
    }

    /**
     * 设置当前登录用户的用户名.
     *
     * @param userName 用户名
     */
    public static void setUsername(final String userName) {
        put(properties.getKey().getUserNameWithPrefix(), userName);
    }

    /**
     * 获取当前登录用户的浏览器信息.
     *
     * @return User-Agent
     */
    public static String getUseAgent() {
        return get(properties.getKey().getDeviceWithPrefix());
    }

    /**
     * 获取当前登录用户所属的租户ID.
     *
     * @return 租户ID
     */
    public static String getTenantId() {
        return get(properties.getKey().getTenantIdWithPrefix());
    }

    /**
     * 设置租户ID.
     *
     * @param tenantId 租户ID
     */
    public static void setTenantId(final String tenantId) {
        put(properties.getKey().getTenantIdWithPrefix(), tenantId);
    }

    /**
     * 清除租户ID.
     * <p>
     * 只移除租户键，不影响 traceId、userId 等同上下文中的其他数据。
     * 供请求入口的租户拦截器在 finally 中调用——拦截器无法调用 {@link #clean()}，
     * 那会连带清掉外层过滤器写入的上下文。
     *
     * @since 4.1.1
     */
    public static void clearTenantId() {
        clear(properties.getKey().getTenantIdWithPrefix());
    }

    /**
     * 获取当前登录用户的区域和语言.
     * <p>
     * 如果没有设置，返回简体中文(zh_CN)
     *
     * @return 区域和语言
     */
    public static String getLocale() {
        String locale = get(properties.getKey().getLocaleWithPrefix());
        if (locale == null || locale.isEmpty()) {
            return ContextConstant.DEFAULT_LOCALE;
        }
        return locale;
    }

    /**
     * 设置区域和语言.
     *
     * @param locale 区域和语言
     */
    public static void setLocale(final String locale) {
        put(properties.getKey().getLocaleWithPrefix(), locale);
    }

    /**
     * 获取当前登录用户的时区设置.
     * <p>
     * 如果没有设置，返回服务器默认时区
     *
     * @return 时区
     */
    public static TimeZone getTimeZone() {
        String zoneOffset = get(properties.getKey().getTimeZoneWithPrefix());
        if (CharSequenceUtil.isBlank(zoneOffset)) {
            return TimeZone.getDefault();
        }
        // 和user profile的timezone的格式匹配
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(
                Integer.parseInt(zoneOffset) * NormalTypeConstant.INT_ONE_HOUR_SECONDS);
        return TimeZone.getTimeZone(offset);
    }

    /**
     * 设置时区.
     *
     * @param timeZone 时区
     */
    public static void setTimeZone(final String timeZone) {
        put(properties.getKey().getTimeZoneWithPrefix(), timeZone);
    }

    /**
     * 获取登录用户的IP地址.
     *
     * @return 客户端IP
     */
    public static String getClientIp() {
        return get(properties.getKey().getUserIpWithPrefix());
    }

    /**
     * 设置客户端IP.
     *
     * @param clientIp 客户端IP
     */
    public static void setClientIp(final String clientIp) {
        put(properties.getKey().getUserIpWithPrefix(), clientIp);
    }

    /**
     * 设置User-Agent.
     *
     * @param userAgent User-Agent
     */
    public static void setUserAgent(final String userAgent) {
        put(properties.getKey().getDeviceWithPrefix(), userAgent);
    }

    /**
     * 获取服务器主机.
     *
     * @return 服务器主机
     */
    public static String getServerHost() {
        return get(properties.getKey().getServerHostWithPrefix());
    }

    /**
     * 设置服务器主机.
     *
     * @param serverHost 服务器主机
     */
    public static void setServerHost(final String serverHost) {
        put(properties.getKey().getServerHostWithPrefix(), serverHost);
    }

    /**
     * 移除服务器主机.
     */
    public static void removeServerHost() {
        getContext().remove(properties.getKey().getServerHostWithPrefix());
    }

    /**
     * 清理当前线程的上下文.
     */
    public static void clean() {
        CONTEXT_MAP.remove();
    }

    /**
     * 移除指定Key的上下文数据.
     *
     * @param key 键
     * @return 移除的值
     */
    public static Object remove(final String key) {
        return getContext().remove(key);
    }

    /**
     * 批量添加上下文数据.
     *
     * @param contextMap 上下文数据Map
     */
    public static void put(final Map<String, Object> contextMap) {
        if (contextMap != null) {
            contextMap.forEach(ContextHolder::put);
        }
    }

    /**
     * 获取指定请求头的JSON字符串表示.
     *
     * @param headerArgs 请求头Key数组
     * @return JSON格式的请求头字符串
     */
    public static String getHeaderJsonStr(final String[] headerArgs) {
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

    /**
     * 将上下文数据转换为HTTP请求头列表.
     * <p>
     * 仅转换带有指定前缀的Key，并对用户名和账户名进行URL编码。
     *
     * @return 请求头键值对列表
     */
    public static List<Pair<String, String>> toHeaders() {
        if (getContext().isEmpty()) {
            return Collections.emptyList();
        } else {
            FunCoreContextProperties.Key headerKey = getProperties().getKey();
            List<Pair<String, String>> pairs = new ArrayList<>();
            getContext().getCopyOfContextMap().forEach((key, value) -> {
                if (ValidUtil.isBlank(value)) {
                    log.warn("header:{}'s value:{} is empty, will not add to headers", key, value);
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

    /**
     * 转换请求头Key-Value对.
     * <p>
     * 如果需要编码，则对值进行URL编码。
     *
     * @param name     Key
     * @param value    Value
     * @param encoding 是否需要URL编码
     * @return 键值对
     */
    static Pair<String, String> convertKey(final String name, final String value, final boolean encoding) {
        return encoding ? Pair.of(name, URLEncoder.encode(value, StandardCharsets.UTF_8)) : Pair.of(name, value);
    }

    /**
     * 获取上下文配置属性.
     *
     * @return 配置属性
     */
    static FunCoreContextProperties getProperties() {
        return properties;
    }

    /**
     * 设置上下文配置属性.
     *
     * @param contextProperties 配置属性
     */
    static void setProperties(final FunCoreContextProperties contextProperties) {
        properties = contextProperties;
    }
}
