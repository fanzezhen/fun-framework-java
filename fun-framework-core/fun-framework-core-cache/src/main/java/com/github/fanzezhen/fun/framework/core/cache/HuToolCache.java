package com.github.fanzezhen.fun.framework.core.cache;

import cn.hutool.cache.impl.TimedCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import jakarta.annotation.Nonnull;

import java.util.concurrent.Callable;

/**
 * 基于HuTool的缓存实现
 *
 * @author fanzezhen
 * @since 3.1.8
 */
public class HuToolCache extends AbstractValueAdaptingCache {

    private final String name;

    private final TimedCache<Object, Object> store;

    public HuToolCache(boolean allowNullValues, String name, TimedCache<Object, Object> store, int schedulePruneSeconds) {
        super(allowNullValues);
        this.name = name;
        this.store = store;
        this.store.schedulePrune(schedulePruneSeconds * 1000L);
    }


    @Override
    @Nonnull
    public final String getName() {
        return this.name;
    }

    @Override
    @Nonnull
    public final TimedCache<Object, Object> getNativeCache() {
        return this.store;
    }

    @Override
    protected Object lookup(@Nonnull Object key) {
        return this.store.get(key, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(@Nonnull Object key, @Nonnull Callable<T> valueLoader) {
        Object value = this.store.get(key, () -> toStoreValue(valueLoader.call()));
        return (T) fromStoreValue(value);
    }

    @Override
    public void put(@Nonnull Object key,  Object value) {
        this.store.put(key, toStoreValue(value));
    }


    @Override
    public void evict(@Nonnull Object key) {
        this.store.remove(key);
    }

    @Override
    public void clear() {
        this.store.clear();
    }

    @Override
    public boolean invalidate() {
        boolean notEmpty = !this.store.isEmpty();
        this.store.clear();
        return notEmpty;
    }
}
