package com.github.fanzezhen.fun.framework.core.cache.service;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

/**
 * 常规锁相关接口
 *
 * @author fanzezhen
 */
public interface LockService {
    /**
     * 获取锁并执行supplier，执行完成后会释放锁，默认时间内获取不到锁则抛出异常
     */
    @SneakyThrows
    default <T> T lockAndExecute(FunSupplier<T> supplier, String key) {
        return lockAndExecute(supplier, key, 6, 10, TimeUnit.SECONDS);
    }

    /**
     * 获取锁并执行函数，执行完成后会释放锁，规定时间内获取不到锁则抛出异常
     *
     * @param supplier 业务函数
     * @param key      键
     * @param limit    尝试次数
     * @param waitTime 每次等待时间
     * @param timeUnit 时间单位
     *
     * @return 业务函数结果
     */
    <T> T lockAndExecute(FunSupplier<T> supplier, String key, int limit, long waitTime, TimeUnit timeUnit);

    @FunctionalInterface
    interface FunSupplier<T> extends java.util.function.Supplier<T> {
        T call() throws Throwable;

        @SneakyThrows
        default T get() {
            return call();
        }
    }
}
