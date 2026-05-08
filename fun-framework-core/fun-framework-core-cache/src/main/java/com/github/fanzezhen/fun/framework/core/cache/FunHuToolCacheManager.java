package com.github.fanzezhen.fun.framework.core.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 基于HuTool的Spring缓存管理器实现
 * <p>
 * 当没有其他CacheManager实现时作为降级方案，使用HuTool的TimedCache实现Spring Cache抽象。
 * 支持自动过期和定期清理，适用于单机环境。
 * <p>
 * <b>性能特性：</b>
 * <ul>
 *   <li>默认缓存12小时过期</li>
 *   <li>每小时执行一次过期数据清理</li>
 *   <li>使用弱引用缓存避免OOM</li>
 * </ul>
 *
 * @since 3.1.8
 */
@Component
@EnableCaching
@ConditionalOnMissingBean(value = CacheManager.class, ignored = FunHuToolCacheManager.class)
public class FunHuToolCacheManager implements CacheManager, InitializingBean {
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
    public Cache getCache(@Nonnull String name) {
        return this.timedCache.get(name, () -> createCache(name));
    }

    @Override
    @Nonnull
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
