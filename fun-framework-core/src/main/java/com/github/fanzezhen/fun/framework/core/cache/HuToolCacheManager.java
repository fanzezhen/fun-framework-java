package com.github.fanzezhen.fun.framework.core.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 基于HuTool的缓存实现
 *
 * @author fanzezhen
 * @since 3.1.8
 */
@Component
@EnableCaching
public class HuToolCacheManager implements CacheManager, InitializingBean {
    @Value("${com.github.fanzezhen.fun.framework.core.cache.time-out.seconds:600}")
    private Integer timeoutSeconds;
    @Value("${com.github.fanzezhen.fun.framework.core.cache.schedule-prune.seconds:180}")
    private Integer schedulePruneSeconds;

    private final TimedCache<String, HuToolCache> timedCache = CacheUtil.newTimedCache(12 * 60 * 60 * 1000L);

    @Override
    public void afterPropertiesSet() {
        this.timedCache.schedulePrune(60 * 60 * 1000L);
    }

    @Override
    public Cache getCache(@NonNull String name) {
        return this.timedCache.get(name, () -> createCache(name));
    }

    @Override
    @NonNull
    public Set<String> getCacheNames() {
        return timedCache.keySet();
    }

    /**
     * Create a new ConcurrentMapCache instance for the specified cache name.
     *
     * @param name the name of the cache
     *
     * @return the ConcurrentMapCache (or a decorator thereof)
     */
    protected HuToolCache createCache(String name) {
        return new HuToolCache(true, name, CacheUtil.newWeakCache(timeoutSeconds * 1000L), schedulePruneSeconds);
    }

}
