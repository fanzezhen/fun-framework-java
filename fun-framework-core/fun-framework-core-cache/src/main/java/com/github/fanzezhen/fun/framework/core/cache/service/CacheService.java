package com.github.fanzezhen.fun.framework.core.cache.service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口
 * <p>
 * 提供基础的缓存操作（读、写、删除）和原子性操作（setIfAbsent）。
 * 实现类需保证线程安全和原子性。
 *
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

    /**
     * CAS原子操作：当key不存在时设置值
     * <p>
     * 用于实现分布式锁等场景，需保证原子性
     *
     * @param k        key
     * @param v        value
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return true表示设置成功（原先不存在），false表示key已存在
     */
    Boolean setIfAbsent(String k, Serializable v, long timeout, TimeUnit timeUnit);
}
