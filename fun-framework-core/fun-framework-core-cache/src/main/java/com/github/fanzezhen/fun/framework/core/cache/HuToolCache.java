package com.github.fanzezhen.fun.framework.core.cache;

import cn.hutool.cache.impl.TimedCache;
import com.github.fanzezhen.fun.framework.core.model.constant.NormalTypeConstant;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import jakarta.annotation.Nonnull;

import java.util.concurrent.Callable;

/**
 * 基于HuTool的缓存实现.
 * <p>
 * 包装HuTool的TimedCache，实现Spring Cache接口。
 * 支持自动过期和定期清理。
 *
 * @since 3.1.8
 */
public class HuToolCache extends AbstractValueAdaptingCache {

    /**
     * 缓存名称.
     */
    private final String name;

    /**
     * 底层缓存存储.
     */
    private final TimedCache<Object, Object> store;

    /**
     * 构造HuTool缓存实例.
     *
     * @param allowNullValues 是否允许空值
     * @param cacheName 缓存名称
     * @param cacheStore 底层缓存存储
     * @param schedulePruneSeconds 定期清理周期（秒）
     */
    public HuToolCache(final boolean allowNullValues,
                       final String cacheName,
                       final TimedCache<Object, Object> cacheStore,
                       final int schedulePruneSeconds) {
        super(allowNullValues);
        this.name = cacheName;
        this.store = cacheStore;
        this.store.schedulePrune(schedulePruneSeconds * NormalTypeConstant.LONG_1000);
    }

    /**
     * 获取缓存名称.
     *
     * @return 缓存名称
     */
    @Override
    @Nonnull
    public final String getName() {
        return this.name;
    }

    /**
     * 获取底层缓存存储.
     *
     * @return HuTool定时缓存实例
     */
    @Override
    @Nonnull
    public final TimedCache<Object, Object> getNativeCache() {
        return this.store;
    }

    /**
     * 查找缓存值.
     *
     * @param key 缓存键
     * @return 缓存值，不存在返回null
     */
    @Override
    protected Object lookup(@Nonnull final Object key) {
        return this.store.get(key, false);
    }

    /**
     * 获取缓存值，不存在时通过valueLoader加载.
     *
     * @param <T> 值类型
     * @param key 缓存键
     * @param valueLoader 值加载器
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(@Nonnull final Object key, @Nonnull final Callable<T> valueLoader) {
        Object value = this.store.get(key, () -> toStoreValue(valueLoader.call()));
        return (T) fromStoreValue(value);
    }

    /**
     * 存入缓存.
     *
     * @param key 缓存键
     * @param value 缓存值
     */
    @Override
    public void put(@Nonnull final Object key, final Object value) {
        this.store.put(key, toStoreValue(value));
    }

    /**
     * 删除缓存项.
     *
     * @param key 缓存键
     */
    @Override
    public void evict(@Nonnull final Object key) {
        this.store.remove(key);
    }

    /**
     * 清空所有缓存.
     */
    @Override
    public void clear() {
        this.store.clear();
    }

    /**
     * 使缓存失效并清空.
     *
     * @return true表示清空前缓存非空，false表示本来就是空的
     */
    @Override
    public boolean invalidate() {
        boolean notEmpty = !this.store.isEmpty();
        this.store.clear();
        return notEmpty;
    }
}
