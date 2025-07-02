package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.exception.ExceptionUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author fanzezhen
 */
@Slf4j
@Getter
@SuppressWarnings("unused")
public class Context {
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

    public void clean() {
        contextMap.clear();
    }
}

