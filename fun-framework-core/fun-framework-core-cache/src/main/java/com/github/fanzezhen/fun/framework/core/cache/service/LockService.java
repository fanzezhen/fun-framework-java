package com.github.fanzezhen.fun.framework.core.cache.service;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 常规锁相关接口
 * @author fanzezhen
 */
public interface LockService {
    /**
     * 获取锁并执行supplier，执行完成后会释放锁，默认时间内获取不到锁则抛出异常
     */
    @SneakyThrows
    default <T> T lockAndExecute(Supplier<T> supplier, String key) {
        return lockAndExecute(supplier, key, 120, TimeUnit.SECONDS);
    }

    /**
     * 获取锁并执行supplier，执行完成后会释放锁，规定时间内获取不到锁则抛出异常
     */
    <T> T lockAndExecute(Supplier<T> supplier, String key, long waitTime, TimeUnit timeUnit);
}
