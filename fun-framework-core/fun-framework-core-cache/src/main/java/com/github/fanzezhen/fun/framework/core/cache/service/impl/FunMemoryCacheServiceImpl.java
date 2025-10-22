package com.github.fanzezhen.fun.framework.core.cache.service.impl;

import cn.hutool.cache.impl.TimedCache;
import com.github.fanzezhen.fun.framework.core.cache.CacheConstant;
import com.github.fanzezhen.fun.framework.core.cache.service.CacheService;
import com.github.fanzezhen.fun.framework.core.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author fanzezhen
 */
@Slf4j
public class FunMemoryCacheServiceImpl implements CacheService {
    @Override
    public String get(String k) {
        TimedCache<String, String> timedCache = CacheConstant.getHourTimedCacheInstance();
        return timedCache.get(k, false);
    }

    @Override
    public void put(String k, String v, long timeoutMillis) {
        TimedCache<String, String> timedCache = CacheConstant.getHourTimedCacheInstance();
        timedCache.put(k, String.valueOf(System.currentTimeMillis()), timeoutMillis);
    }

    @Override
    public void remove(String k) {
        TimedCache<String, String> timedCache = CacheConstant.getHourTimedCacheInstance();
        timedCache.remove(k);
    }

    @Override
    public void set(String k, String v, long timeout, TimeUnit timeUnit) {
        TimedCache<String, String> timedCache = CacheConstant.getHourTimedCacheInstance();
        if (timeUnit == null){
            throw ExceptionUtil.wrapException("timeUnit 不能为空");
        }
        long timeoutMillis =timeUnit.toMillis(timeout);
        timedCache.put(k, String.valueOf(System.currentTimeMillis()), timeoutMillis);
    }

}
