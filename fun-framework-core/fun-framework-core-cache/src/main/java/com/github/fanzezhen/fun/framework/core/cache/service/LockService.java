package com.github.fanzezhen.fun.framework.core.cache.service;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁服务接口.
 * <p>
 * 提供基于回调的锁操作，自动管理锁的获取和释放。
 * 适用于需要原子性执行的业务场景。
 * <p>
 * <b>注意：</b>使用FunSupplier而非标准Supplier以支持受检异常传播。
 */
public interface LockService {

    /**
     * 默认尝试次数.
     */
    int DEFAULT_LIMIT = 6;

    /**
     * 默认等待时间.
     */
    int DEFAULT_WAIT_TIME = 10;

    /**
     * 获取锁并执行supplier，执行完成后会释放锁，默认时间内获取不到锁则抛出异常.
     *
     * @param <T> 返回值类型
     * @param supplier 业务函数
     * @param key 锁键
     * @return 业务函数结果
     */
    @SneakyThrows
    default <T> T lockAndExecute(FunSupplier<T> supplier, String key) {
        return lockAndExecute(supplier, key, DEFAULT_LIMIT, DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
    }

    /**
     * 获取锁并执行函数，执行完成后会释放锁，规定时间内获取不到锁则抛出异常.
     *
     * @param <T> 返回值类型
     * @param supplier 业务函数
     * @param key 锁键
     * @param limit 尝试次数
     * @param waitTime 每次等待时间
     * @param timeUnit 时间单位
     * @return 业务函数结果
     */
    <T> T lockAndExecute(FunSupplier<T> supplier, String key, int limit, long waitTime, TimeUnit timeUnit);

    /**
     * 支持受检异常的Supplier接口.
     *
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    interface FunSupplier<T> extends java.util.function.Supplier<T> {
        /**
         * 执行业务逻辑.
         *
         * @return 业务结果
         * @throws Throwable 业务异常
         */
        T call() throws Throwable;

        /**
         * Supplier接口实现.
         *
         * @return 业务结果
         */
        @SneakyThrows
        default T get() {
            return call();
        }
    }
}
