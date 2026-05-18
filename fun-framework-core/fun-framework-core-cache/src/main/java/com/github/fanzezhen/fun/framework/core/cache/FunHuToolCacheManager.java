package com.github.fanzezhen.fun.framework.core.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;
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
 * 基于HuTool的Spring缓存管理器实现.
 * <p>
 * 当没有其他CacheManager实现时作为降级方案，
 * 使用HuTool的TimedCache实现Spring Cache抽象。
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

    /**
     * 缓存过期时间（秒）.
     */
    @Value("${com.github.fanzezhen.fun.framework.core.cache.time-out.seconds:600}")
    private Integer timeoutSeconds;

    /**
     * 定期清理周期（秒）.
     */
    @Value("${com.github.fanzezhen.fun.framework.core.cache.schedule-prune.seconds:180}")
    private Integer schedulePruneSeconds;

    /**
     * 定时缓存实例.
     */
    private final TimedCache<String, HuToolCache> timedCache = CacheUtil.newTimedCache(NormalTypeConstant.INT_TWELVE_HOURS_MILLIS);

    /**
     * 初始化缓存管理器，启动定期清理任务.
     */
    @Override
    public void afterPropertiesSet() {
        this.timedCache.schedulePrune(NormalTypeConstant.INT_ONE_HOUR_MILLIS);
    }

    /**
     * 获取指定名称的缓存实例.
     *
     * @param name 缓存名称
     * @return 缓存实例，不存在则创建
     */
    @Override
    public Cache getCache(@Nonnull final String name) {
        return this.timedCache.get(name, () -> createCache(name));
    }

    /**
     * 获取所有缓存名称.
     *
     * @return 缓存名称集合
     */
    @Override
    @Nonnull
    public Set<String> getCacheNames() {
        return timedCache.keySet();
    }

    /**
     * 创建HuTool缓存实例.
     *
     * @param name 缓存名称
     * @return HuTool缓存实例
     */
    protected HuToolCache createCache(final String name) {
        final long timeoutMillis = (long) timeoutSeconds * NormalTypeConstant.INT_MILLIS_PER_SECOND;
        return new HuToolCache(true, name, CacheUtil.newWeakCache(timeoutMillis), schedulePruneSeconds);
    }

}
