package com.github.fanzezhen.fun.framework.core.cache.service;

import java.io.Serializable;
import java.time.Duration;

/**
 * 缓存服务接口.
 * <p>
 * 提供基础的缓存操作（读、写、删除）和原子性操作（setIfAbsent）。
 * 实现类需保证线程安全和原子性。
 */
public interface CacheService {
    /**
     * 查询缓存值.
     *
     * @param k 缓存键
     * @return 缓存值，不存在返回null
     */
    String get(String k);

    /**
     * 存入缓存.
     *
     * @param k 缓存键
     * @param v 缓存值
     * @param timeoutMillis 过期时间（毫秒）
     */
    void put(String k, String v, long timeoutMillis);

    /**
     * 删除缓存项.
     *
     * @param k 缓存键
     */
    void remove(String k);

    /**
     * CAS原子操作：当key不存在时设置值.
     * <p>
     * 用于实现分布式锁等场景，需保证原子性。
     *
     * @param k 缓存键
     * @param v 缓存值
     * @param timeout 过期时间（Duration对象）
     * @return true表示设置成功（原先不存在），false表示key已存在
     */
    Boolean setIfAbsent(String k, Serializable v, Duration timeout);
}
