package com.github.fanzezhen.fun.framework.core.cache.service;

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

    void set(String k, String v, long timeout, TimeUnit timeUnit);
}
