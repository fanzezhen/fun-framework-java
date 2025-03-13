package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.cache.impl.TimedCache;
import com.github.fanzezhen.fun.framework.core.model.ClassInfoBean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fanzezhen
 */
public class SingletonUtil {
    private SingletonUtil() {
    }

    /**
     * Byte 1 ：map = Getter's special annotations
     * Byte 2 ：map = Setter's special annotations
     * Byte 3 ：map = Getters or Field
     * Byte 4 ：map = Setters or Field
     * Byte 1 ：Boolean = if getters have special annotations
     * Byte 2 ：Boolean = if setters have special annotations
     *
     * @return <Class<?>, Map<Byte, Map<String, Object>>>
     */
    static TimedCache<Class<?>, ClassInfoBean> getHourTimedCacheForClassInfo() {
        return ClassInfoHourCacheSingletonHolder.HOUR_TIMED_CACHE_FOR_CLASS_INFO;
    }

    static class ClassInfoHourCacheSingletonHolder {
        private ClassInfoHourCacheSingletonHolder() {
        }

        private static final TimedCache<Class<?>, ClassInfoBean> HOUR_TIMED_CACHE_FOR_CLASS_INFO = new TimedCache<>(60 * 60 * 1000L, new ConcurrentHashMap<>());

        static {
            HOUR_TIMED_CACHE_FOR_CLASS_INFO.schedulePrune(60 * 60 * 1000L);
        }
    }

}
