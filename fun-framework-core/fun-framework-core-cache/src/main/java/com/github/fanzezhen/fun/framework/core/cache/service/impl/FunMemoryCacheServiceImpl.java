package com.github.fanzezhen.fun.framework.core.cache.service.impl;

import cn.hutool.cache.impl.TimedCache;
import com.github.fanzezhen.fun.framework.core.cache.CacheConstant;
import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author fanzezhen
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
