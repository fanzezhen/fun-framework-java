package com.github.fanzezhen.fun.framework.core.cache.service.impl;

import cn.hutool.cache.impl.TimedCache;
import com.github.fanzezhen.fun.framework.core.cache.CacheConstant;
import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Duration;

/**
 * 基于内存的缓存服务实现.
 * <p>
 * 使用HuTool的TimedCache实现，适用于单机环境的轻量级缓存需求。
 * 当没有Redis等分布式缓存时作为降级方案。
 * <p>
 * <b>线程安全性：</b>setIfAbsent方法通过synchronized保证原子性。
 */
@Slf4j
public class FunMemoryCacheServiceImpl implements CacheService {

    /**
     * 查询缓存值.
     *
     * @param k 缓存键
     * @return 缓存值，不存在返回null
     */
    @Override
    public String get(String k) {
        TimedCache<String, Object> timedCache = CacheConstant.getHourTimedCacheInstance();
        Object value = timedCache.get(k, false);
        return value != null ? value.toString() : null;
    }

    /**
     * 存入缓存.
     *
     * @param k 缓存键
     * @param v 缓存值
     * @param timeoutMillis 过期时间（毫秒）
     */
    @Override
    public void put(String k, String v, long timeoutMillis) {
        TimedCache<String, Object> timedCache = CacheConstant.getHourTimedCacheInstance();
        timedCache.put(k, String.valueOf(System.currentTimeMillis()), timeoutMillis);
    }

    /**
     * 删除缓存项.
     *
     * @param k 缓存键
     */
    @Override
    public void remove(String k) {
        TimedCache<String, Object> timedCache = CacheConstant.getHourTimedCacheInstance();
        timedCache.remove(k);
    }

    /**
     * CAS原子操作：当key不存在时设置值.
     *
     * @param k 缓存键
     * @param v 缓存值
     * @param timeout 过期时间（Duration对象）
     * @return true表示设置成功，false表示key已存在
     */
    @Override
    public Boolean setIfAbsent(String k, Serializable v, Duration timeout) {
        if (timeout == null) {
            throw new ServiceException("timeout 不能为空");
        }
        TimedCache<String, Object> timedCache = CacheConstant.getHourTimedCacheInstance();
        long timeoutMillis = timeout.toMillis();

        synchronized (timedCache) {
            Object existingValue = timedCache.get(k, false);
            if (existingValue == null) {
                timedCache.put(k, String.valueOf(v), timeoutMillis);
                return true;
            }
            return false;
        }
    }

}
