package com.github.fanzezhen.fun.framework.core.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author fanzezhen
 */
public interface CacheService {
    /**
     * 查询并保存
     *
     * @param k                  key
     * @return value
     */
    String get(String k);

    /**
     * 保存
     *
     * @param k       key
     * @param v       value
     * @param timeoutMillis 过期时间（毫秒）
     */
    void put(String k, String v, long timeoutMillis);
    /**
     * 删除
     *
     * @param k       key
     */
    void remove(String k);

    void set(String k, String v, long timeout, TimeUnit timeUnit);
}
