package com.github.fanzezhen.fun.framework.core.cache.service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 缓存接口，提供了常规的缓存方法
 *
 * @author fanzezhen
 */
public interface CacheService {
    /**
     * 查询并保存
     *
     * @param k key
     *
     * @return value
     */
    String get(String k);

    /**
     * 保存
     *
     * @param k             key
     * @param v             value
     * @param timeoutMillis 过期时间（毫秒）
     */
    void put(String k, String v, long timeoutMillis);

    /**
     * 删除
     *
     * @param k key
     */
    void remove(String k);

    Boolean setIfAbsent(String k, Serializable v, long timeout, TimeUnit timeUnit);
}
