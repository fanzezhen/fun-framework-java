package com.github.fanzezhen.fun.framework.core.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ThreadPoolExecutorRepository 单元测试
 */
@Slf4j
class ThreadPoolExecutorRepositoryTest {

    @BeforeEach
    void setUp() {
        // 每个测试前清理所有线程池，确保测试独立性
        ThreadPoolExecutorRepository.destroy(1);
    }

    @AfterEach
    void tearDown() {
        // 每个测试后清理所有线程池
        ThreadPoolExecutorRepository.destroy(5);
    }

    @Test
    void testCreateAndGetThreadPool() {
        // 创建线程池
        ExecutorService executor = ThreadPoolExecutorRepository.computeThreadPoolExecutor(
            "test-pool",
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10)
        );

        assertNotNull(executor);

        // 验证可以获取
        ExecutorService retrieved = ThreadPoolExecutorRepository.get("test-pool");
        assertNotNull(retrieved);
        // 由于是代理对象，直接比较引用
        assertSame(executor, retrieved);
    }

    @Test
    void testContains() {
        // 创建线程池
        ThreadPoolExecutorRepository.computeThreadPoolExecutor(
            "test-pool",
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10)
        );

        assertTrue(ThreadPoolExecutorRepository.contains("test-pool"));
        assertFalse(ThreadPoolExecutorRepository.contains("non-existent-pool"));
    }

    @Test
    void testGetPoolNames() {
        // 创建多个线程池
        ThreadPoolExecutorRepository.computeThreadPoolExecutor(
            "pool-1",
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10)
        );

        ThreadPoolExecutorRepository.computeThreadPoolExecutor(
            "pool-2",
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10)
        );

        Set<String> poolNames = ThreadPoolExecutorRepository.getPoolNames();
        assertEquals(2, poolNames.size());
        assertTrue(poolNames.contains("pool-1"));
        assertTrue(poolNames.contains("pool-2"));
    }

    @Test
    void testDestroySingleThreadPool() throws InterruptedException {
        // 创建线程池
        ExecutorService executor = ThreadPoolExecutorRepository.computeThreadPoolExecutor(
            "test-pool",
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10)
        );

        // 提交任务
        CountDownLatch latch = new CountDownLatch(1);
        executor.execute(() -> {
            try {
                latch.countDown();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 等待任务开始
        latch.await();

        // 销毁线程池
        boolean destroyed = ThreadPoolExecutorRepository.destroy("test-pool", 5);
        assertTrue(destroyed);

        // 验证已被移除
        assertFalse(ThreadPoolExecutorRepository.contains("test-pool"));
        assertNull(ThreadPoolExecutorRepository.get("test-pool"));
    }

    @Test
    void testDestroyNonExistentThreadPool() {
        // 销毁不存在的线程池应该返回 false
        boolean destroyed = ThreadPoolExecutorRepository.destroy("non-existent", 5);
        assertFalse(destroyed);
    }

    @Test
    void testDestroyAllThreadPools() throws InterruptedException {
        // 创建多个线程池
        ExecutorService executor1 = ThreadPoolExecutorRepository.computeThreadPoolExecutor(
            "pool-1",
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10)
        );

        ExecutorService executor2 = ThreadPoolExecutorRepository.computeThreadPoolExecutor(
            "pool-2",
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10)
        );

        // 提交任务
        AtomicInteger counter = new AtomicInteger(0);
        executor1.execute(() -> {
            try {
                Thread.sleep(100);
                counter.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor2.execute(() -> {
            try {
                Thread.sleep(100);
                counter.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 销毁所有线程池
        ThreadPoolExecutorRepository.destroy(5);

        // 验证所有线程池已被移除
        assertTrue(ThreadPoolExecutorRepository.getPoolNames().isEmpty());

        // 等待任务完成
        Thread.sleep(200);
        assertEquals(2, counter.get());
    }

    @Test
    void testDestroyWithTimeout() throws InterruptedException {
        // 创建线程池
        ExecutorService executor = ThreadPoolExecutorRepository.computeThreadPoolExecutor(
            "test-pool",
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10)
        );

        // 提交长时间运行的任务
        CountDownLatch startLatch = new CountDownLatch(1);
        executor.execute(() -> {
            try {
                startLatch.countDown();
                Thread.sleep(10000); // 10秒
            } catch (InterruptedException e) {
                log.info("任务被中断");
                Thread.currentThread().interrupt();
            }
        });

        // 等待任务开始
        startLatch.await();

        // 使用短超时时间销毁（应该强制关闭）
        long startTime = System.currentTimeMillis();
        boolean destroyed = ThreadPoolExecutorRepository.destroy("test-pool", 1);
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(destroyed);
        assertTrue(duration < 7000); // 应该在 1秒 + 5秒二次等待 内完成

        // 验证已被移除
        assertFalse(ThreadPoolExecutorRepository.contains("test-pool"));
    }

    @Test
    void testGetPoolExecutorMap() {
        // 确保开始时没有线程池
        ThreadPoolExecutorRepository.destroy(5);

        // 创建线程池
        ThreadPoolExecutorRepository.computeThreadPoolExecutor(
            "pool-get-map-test",
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10)
        );

        var poolMap = ThreadPoolExecutorRepository.getPoolExecutorMap();
        assertEquals(1, poolMap.size(), "应该只有一个线程池，实际: " + poolMap.keySet());
        assertTrue(poolMap.containsKey("pool-get-map-test"));

        // 验证返回的是不可变副本
        assertThrows(UnsupportedOperationException.class, () -> poolMap.put("new-pool", null));
    }

    @Test
    void testDefaultThreadPoolExecutor() {
        ExecutorService executor = ThreadPoolExecutorRepository.defaultThreadPoolExecutor();
        assertNotNull(executor);

        // 验证默认线程池已注册（名称为 defaultExecutor）
        assertTrue(ThreadPoolExecutorRepository.contains("defaultExecutor"));
    }
}
