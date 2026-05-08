package com.github.fanzezhen.fun.framework.core.cache.service.impl;

import cn.hutool.cache.impl.TimedCache;
import com.github.fanzezhen.fun.framework.core.cache.CacheConstant;
import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 基于内存的缓存服务实现
 * <p>
 * 使用HuTool的TimedCache实现，适用于单机环境的轻量级缓存需求。
 * 当没有Redis等分布式缓存时作为降级方案。
 * <p>
 * <b>线程安全性：</b>setIfAbsent方法通过synchronized保证原子性
 *
 */
@Slf4j
public class FunMemoryCacheServiceImpl implements CacheService {
    @Override
    public String get(String k) {
        TimedCache<String, Object> timedCache = CacheConstant.getHourTimedCacheInstance();
        Object value = timedCache.get(k, false);
        return value !=null?value.toString():null;
    }

    @Override
    public void put(String k, String v, long timeoutMillis) {
        TimedCache<String, Object> timedCache = CacheConstant.getHourTimedCacheInstance();
        timedCache.put(k, String.valueOf(System.currentTimeMillis()), timeoutMillis);
    }

    @Override
    public void remove(String k) {
        TimedCache<String, Object> timedCache = CacheConstant.getHourTimedCacheInstance();
        timedCache.remove(k);
    }

    @Override
    public Boolean setIfAbsent(String k, Serializable v, long timeout, TimeUnit timeUnit) {
        if (timeUnit == null) {
            throw new ServiceException("timeUnit 不能为空");
        }
        TimedCache<String, Object> timedCache = CacheConstant.getHourTimedCacheInstance();
        long timeoutMillis = timeUnit.toMillis(timeout);

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
