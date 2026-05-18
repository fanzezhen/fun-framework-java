package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * 上下文数据容器，用于存储请求链路中的元数据（用户ID、租户ID、traceId等）.
 * <p>
 * key大小写不敏感：使用 {@link CaseInsensitiveMap} 保证 "userId" 和 "USERID" 指向同一个值，
 * 避免因大小写不一致导致的取值失败（常见于HTTP Header和配置文件场景）。
 * <p>
 * 容量限制原因：
 * - MAX_SIZE(1024)：单个key/value限制，防止存储大对象（如完整JSON文档）导致跨服务调用时HTTP Header超限
 * - MAX_CAPACITY(100)：键值对总数限制，防止上下文无限膨胀导致内存泄漏
 */
@Slf4j
@SuppressWarnings("unused")
public class Context {
    /**
     * 上下文数据存储容器（大小写不敏感）.
     */
    protected final JSONObject contextMap = new JSONObject(new CaseInsensitiveMap<>());

    /**
     * 最多允许100个键值对，防止上下文无限膨胀.
     */
    public static final Integer MAX_CAPACITY = 100;
    /**
     * 单个key/value最大1024字符，防止HTTP Header超限（Nginx默认8KB）.
     */
    public static final Integer MAX_SIZE = 1024;

    /**
     * 获取上下文数据的副本.
     *
     * @return 上下文数据的克隆对象
     */
    public JSONObject getCopyOfContextMap() {
        return contextMap.clone();
    }

    /**
     * 根据Key获取值.
     *
     * @param key 键
     * @return 值对象
     */
    public Object get(final String key) {
        return contextMap.get(key);
    }

    /**
     * 根据Key获取字符串值.
     *
     * @param key 键
     * @return 字符串值
     */
    public String getStr(final String key) {
        return contextMap.getString(key);
    }

    /**
     * 设置名值对.
     * <p>
     * 如果Map之前为null，则会被初始化。
     * 限制单个key/value为1024字符，总容量不超过100个键值对。
     *
     * @param key   键
     * @param value 值
     * @return 之前的值
     */
    public String set(final String key, final String value) {
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
     * 移除一个key.
     *
     * @param key 键
     * @return 移除的值
     */
    public Object remove(final String key) {
        return this.contextMap.remove(key);
    }

    /**
     * 清空上下文数据.
     */
    public void clean() {
        contextMap.clear();
    }

    /**
     * 获取上下文数据容量.
     *
     * @return 键值对数量
     */
    public int size() {
        return contextMap.size();
    }

    /**
     * 判断上下文是否为空.
     *
     * @return true-为空，false-不为空
     */
    public boolean isEmpty() {
        return contextMap.isEmpty();
    }

    /**
     * 判断是否包含指定Key.
     *
     * @param key 键
     * @return true-包含，false-不包含
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean containsKey(final Object key) {
        return contextMap.containsKey(key);
    }

    /**
     * 判断是否包含指定Value.
     *
     * @param value 值
     * @return true-包含，false-不包含
     */
    public boolean containsValue(final Object value) {
        return contextMap.containsValue(value);
    }

    /**
     * 根据Key获取值.
     *
     * @param key 键
     * @return 值对象
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public Object get(final Object key) {
        return contextMap.get(key);
    }

    /**
     * 添加键值对.
     *
     * @param key   键
     * @param value 值
     * @return 之前的值
     */
    public Object put(final String key, final Object value) {
        return contextMap.put(key, value);
    }

    /**
     * 批量添加键值对.
     *
     * @param m 键值对集合
     */
    public void putAll(final Map<String, ?> m) {
        contextMap.putAll(m);
    }

    /**
     * 清空上下文数据.
     */
    public void clear() {
        contextMap.clear();
    }

    /**
     * 获取所有Key的集合.
     *
     * @return Key集合
     */
    public Set<String> keySet() {
        return contextMap.keySet();
    }

    /**
     * 获取所有Value的集合.
     *
     * @return Value集合
     */
    public Collection<Object> values() {
        return contextMap.values();
    }

    /**
     * 获取所有键值对的集合.
     *
     * @return 键值对集合
     */
    public Set<Map.Entry<String, Object>> entrySet() {
        return contextMap.entrySet();
    }

    /**
     * 根据Key获取字符串值.
     *
     * @param lowerCase 键（小写）
     * @return 字符串值
     */
    public String getString(final String lowerCase) {
        return contextMap.getString(lowerCase);
    }

    /**
     * 获取上下文数据存储容器.
     * <p>
     * 供 ContextHolder 使用。
     *
     * @return 上下文数据存储容器
     */
    JSONObject getContextMap() {
        return contextMap;
    }
}

