package com.github.fanzezhen.fun.framework.core.cache;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author zezhen.fan
 */
public interface LockService {

    @SneakyThrows
    default <T> T lockKey(String key, Supplier<T> supplier) {
        return lockKey(key, 120, TimeUnit.SECONDS, supplier);
    }

    <T> T lockKey(String key, long waitTime, TimeUnit timeUnit, Supplier<T> supplier);
}
