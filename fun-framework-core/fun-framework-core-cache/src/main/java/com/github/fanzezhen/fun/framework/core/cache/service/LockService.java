package com.github.fanzezhen.fun.framework.core.cache.service;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁服务接口
 * <p>
 * 提供基于回调的锁操作，自动管理锁的获取和释放。
 * 适用于需要原子性执行的业务场景。
 * <p>
 * <b>注意：</b>使用FunSupplier而非标准Supplier以支持受检异常传播
 *
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
