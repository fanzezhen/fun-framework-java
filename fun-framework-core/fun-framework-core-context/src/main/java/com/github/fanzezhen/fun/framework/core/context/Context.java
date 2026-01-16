package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * @author fanzezhen
 */
@Slf4j
@SuppressWarnings("unused")
public class Context {
    protected final JSONObject contextMap = new JSONObject(new CaseInsensitiveMap<>());

    /**
     * context map 最大容量
     */
    public static final Integer MAX_CAPACITY = 100;
    /**
     * context map key 或者 value 最大值
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

