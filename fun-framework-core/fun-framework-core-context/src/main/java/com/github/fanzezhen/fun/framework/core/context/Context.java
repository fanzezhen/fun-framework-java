package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * 上下文数据容器，用于存储请求链路中的元数据（用户ID、租户ID、traceId等）
 * <p>
 * key大小写不敏感：使用 {@link CaseInsensitiveMap} 保证 "userId" 和 "USERID" 指向同一个值，
 * 避免因大小写不一致导致的取值失败（常见于HTTP Header和配置文件场景）。
 * <p>
 * 容量限制原因：
 * - MAX_SIZE(1024)：单个key/value限制，防止存储大对象（如完整JSON文档）导致跨服务调用时HTTP Header超限
 * - MAX_CAPACITY(100)：键值对总数限制，防止上下文无限膨胀导致内存泄漏
 *
 */
@Slf4j
@SuppressWarnings("unused")
public class Context {
    protected final JSONObject contextMap = new JSONObject(new CaseInsensitiveMap<>());

    /**
     * 最多允许100个键值对，防止上下文无限膨胀
     */
    public static final Integer MAX_CAPACITY = 100;
    /**
     * 单个key/value最大1024字符，防止HTTP Header超限（Nginx默认8KB）
     */
    public static final Integer MAX_SIZE = 1024;

    public JSONObject getCopyOfContextMap() {
        return contextMap.clone();
    }

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
                throw new ServiceException("key is more than " + MAX_SIZE + ", i can't set it into the context map");
            } else if (value.length() > MAX_SIZE) {
                throw new ServiceException("value is more than " + MAX_SIZE + ", i can't set it into the context map");
            } else {
                if (this.size() > MAX_CAPACITY) {
                    throw new ServiceException("the context map is full, can't set anything");
                } else {
                    this.put(key.toLowerCase(), value);
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

    public void clean() {
        contextMap.clear();
    }

    public int size() {
        return contextMap.size();
    }

    public boolean isEmpty() {
        return contextMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return contextMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return contextMap.containsValue(value);
    }

    public Object get(Object key) {
        return contextMap.get(key);
    }

    public Object put(String key, Object value) {
        return contextMap.put(key, value);
    }

    public void putAll(Map<String, ?> m) {
        contextMap.putAll(m);
    }

    public void clear() {
        contextMap.clear();
    }

    public Set<String> keySet() {
        return contextMap.keySet();
    }

    public Collection<Object> values() {
        return contextMap.values();
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return contextMap.entrySet();
    }

    public String getString(String lowerCase) {
        return contextMap.getString(lowerCase);
    }
}

