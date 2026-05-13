package com.github.fanzezhen.fun.framework.core.springboot.thread;

import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadPoolTaskDecorator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ThreadPoolTaskExecutorRepository 单元测试
 */
@Slf4j
class ThreadPoolTaskExecutorRepositoryTest {

    @BeforeEach
    void setUp() {
        // 每个测试前清理所有线程池，确保测试独立性
        ThreadPoolTaskExecutorRepository.destroy(1);
    }

    @AfterEach
    void tearDown() {
        // 每个测试后清理所有线程池
        ThreadPoolTaskExecutorRepository.destroy(5);
    }

    @Test
    void testCreateAndGetThreadPool() {
        // 创建线程池
        ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
            "test-pool",
            2,
            5
        );

        assertNotNull(executor);

        // 验证可以获取
        ThreadPoolTaskExecutor retrieved = ThreadPoolTaskExecutorRepository.get("test-pool");
        assertNotNull(retrieved);
        assertSame(executor, retrieved);
    }

    @Test
    void testContains() {
        // 创建线程池
        ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("test-pool", 2, 5);

        assertTrue(ThreadPoolTaskExecutorRepository.contains("test-pool"));
        assertFalse(ThreadPoolTaskExecutorRepository.contains("non-existent-pool"));
    }

    @Test
    void testGetPoolNames() {
        // 创建多个线程池
        ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("pool-1", 2, 5);
        ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("pool-2", 2, 5);

        Set<String> poolNames = ThreadPoolTaskExecutorRepository.getPoolNames();
        assertEquals(2, poolNames.size());
        assertTrue(poolNames.contains("pool-1"));
        assertTrue(poolNames.contains("pool-2"));
    }

    @Test
    void testDestroySingleThreadPool() throws InterruptedException {
        // 创建线程池
        ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
            "test-pool",
            2,
            5
        );

        // 提交任务
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch taskLatch = new CountDownLatch(1);
        executor.execute(() -> {
            startLatch.countDown();
            // 使用 CountDownLatch 替代 Thread.sleep 等待销毁信号
            try {
                taskLatch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 等待任务开始
        assertTrue(startLatch.await(5, TimeUnit.SECONDS));

        // 销毁线程池
        boolean destroyed = ThreadPoolTaskExecutorRepository.destroy("test-pool", 5);
        assertTrue(destroyed);

        // 验证已被移除
        assertFalse(ThreadPoolTaskExecutorRepository.contains("test-pool"));
        assertNull(ThreadPoolTaskExecutorRepository.get("test-pool"));
    }

    @Test
    void testDestroyNonExistentThreadPool() {
        // 销毁不存在的线程池应该返回 false
        boolean destroyed = ThreadPoolTaskExecutorRepository.destroy("non-existent", 5);
        assertFalse(destroyed);
    }

    @Test
    void testDestroyAllThreadPools() throws InterruptedException {
        // 创建多个线程池
        ThreadPoolTaskExecutor executor1 = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("pool-1", 2, 5);
        ThreadPoolTaskExecutor executor2 = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("pool-2", 2, 5);

        // 提交快速完成的任务
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);

        executor1.execute(() -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        executor2.execute(() -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        // 等待任务完成
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(2, counter.get());

        // 销毁所有线程池
        ThreadPoolTaskExecutorRepository.destroy(5);

        // 验证所有线程池已被移除
        assertTrue(ThreadPoolTaskExecutorRepository.getPoolNames().isEmpty());
    }

    @Test
    void testDestroyWithTimeout() throws InterruptedException {
        // 创建线程池
        ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
            "test-pool",
            2,
            5
        );

        // 提交长时间运行的任务
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch taskLatch = new CountDownLatch(1);
        executor.execute(() -> {
            startLatch.countDown();
            // 使用 CountDownLatch 模拟长时间任务，而不是 Thread.sleep
            try {
                taskLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.info("任务被中断");
                Thread.currentThread().interrupt();
            }
        });

        // 等待任务开始
        assertTrue(startLatch.await(5, TimeUnit.SECONDS));

        // 使用短超时时间销毁（应该强制关闭）
        long startTime = System.currentTimeMillis();
        boolean destroyed = ThreadPoolTaskExecutorRepository.destroy("test-pool", 1);
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(destroyed);
        assertTrue(duration < 7000); // 应该在 1秒 + 5秒二次等待 内完成

        // 验证已被移除
        assertFalse(ThreadPoolTaskExecutorRepository.contains("test-pool"));
    }

    @Test
    void testGetPoolTaskExecutorMap() {
        // 创建线程池
        ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor("pool-1", 2, 5);

        var poolMap = ThreadPoolTaskExecutorRepository.getPoolTaskExecutorMap();
        assertEquals(1, poolMap.size());
        assertTrue(poolMap.containsKey("pool-1"));
    }

    @Test
    void testDefaultThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.defaultThreadPoolTaskExecutor();
        assertNotNull(executor);

        // 验证默认线程池已注册（名称为 defaultExecutor）
        assertTrue(ThreadPoolTaskExecutorRepository.contains("defaultExecutor"));
    }

    @Test
    void testThreadPoolTaskExecutorWithQueueCapacity() {
        // 创建带队列容量的线程池
        ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.computeThreadPoolTaskExecutor(
            "test-pool-with-queue",
            2,
            5,
            100
        );

        assertNotNull(executor);
        assertEquals(100, executor.getThreadPoolExecutor().getQueue().remainingCapacity());
    }

    @Test
    void testThreadNamePrefix() throws InterruptedException {
        // 创建线程池
        ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
            "my-test-pool",
            2,
            5
        );

        // 提交任务并检查线程名称
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger threadNameMatches = new AtomicInteger(0);

        executor.execute(() -> {
            String threadName = Thread.currentThread().getName();
            if (threadName.startsWith("my-test-pool-")) {
                threadNameMatches.incrementAndGet();
            }
            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
        assertEquals(1, threadNameMatches.get());
    }

    @Test
    void testComputeThreadPoolTaskExecutor() {
        // 第一次调用应该创建新的线程池
        ThreadPoolTaskExecutor executor1 = ThreadPoolTaskExecutorRepository.computeThreadPoolTaskExecutor(
            "compute-pool",
            2,
            5,
            10
        );

        // 第二次调用应该返回同一个实例
        ThreadPoolTaskExecutor executor2 = ThreadPoolTaskExecutorRepository.computeThreadPoolTaskExecutor(
            "compute-pool",
            3,  // 不同的参数
            6,
            20
        );

        assertSame(executor1, executor2);
        // 参数应该保持第一次创建时的值
        assertEquals(2, executor1.getCorePoolSize());
    }

    @Test
    void testThreadPoolStatistics() throws InterruptedException {
        // 创建线程池（核心线程数2，确保任务可以立即执行）
        ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
            "stats-pool",
            2,
            5
        );

        // 提交2个任务（等于核心线程数，确保立即执行）
        CountDownLatch startLatch = new CountDownLatch(2);
        CountDownLatch finishLatch = new CountDownLatch(1);
        for (int i = 0; i < 2; i++) {
            executor.execute(() -> {
                startLatch.countDown();
                try {
                    // 等待释放信号，保持任务活跃以便检查统计信息
                    finishLatch.await(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 等待所有任务开始执行
        assertTrue(startLatch.await(5, TimeUnit.SECONDS), "任务应在 5 秒内开始");

        // 检查统计信息
        assertTrue(executor.getActiveCount() > 0, "应有活跃线程");
        assertTrue(executor.getThreadPoolExecutor().getCompletedTaskCount() >= 0, "完成任务数应>=0");

        // 释放所有任务
        finishLatch.countDown();
    }

    @Test
    void testAddMultipleDecoratorsShouldNotCauseStackOverflow() throws InterruptedException {
        // 测试修复后的装饰器链不会引发 StackOverflowError
        AtomicInteger decoratorCallCount = new AtomicInteger(0);

        // 添加多个装饰器，模拟实际场景
        for (int i = 0; i < 5; i++) {
            final int decoratorId = i;
            ThreadPoolTaskExecutorRepository.addDecorator(new ThreadPoolTaskDecorator() {
                @Override
                public String getName() {
                    return "test-decorator-" + decoratorId;
                }

                @Override
                public Runnable decorate(Runnable runnable) {
                    decoratorCallCount.incrementAndGet();
                    return () -> {
                        log.info("装饰器 {} 执行前", decoratorId);
                        runnable.run();
                        log.info("装饰器 {} 执行后", decoratorId);
                    };
                }
            });
        }

        // 等待所有异步销毁任务完成（addDecorator 会异步销毁线程池）
        // 使用轮询检查，避免 Thread.sleep 和 TimeUnit.sleep
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
        while (!ThreadPoolTaskExecutorRepository.getPoolNames().isEmpty()
                && System.currentTimeMillis() < deadline) {
            // 使用 LockSupport.parkNanos 替代 sleep，符合 SonarQube 规范
            java.util.concurrent.locks.LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
        }

        // 额外等待确保线程池完全销毁（线程池 shutdown 是异步的）
        java.util.concurrent.locks.LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500));

        // 创建新线程池验证装饰器链正常工作
        ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
            "decorated-pool",
            2,
            5
        );

        CountDownLatch taskLatch = new CountDownLatch(1);
        AtomicInteger taskExecuted = new AtomicInteger(0);

        // 执行任务，验证不会栈溢出
        executor.execute(() -> {
            taskExecuted.incrementAndGet();
            taskLatch.countDown();
        });

        // 等待任务完成
        assertTrue(taskLatch.await(10, TimeUnit.SECONDS), "任务应在 10 秒内完成");
        assertEquals(1, taskExecuted.get(), "任务应成功执行一次");

        // 验证装饰器确实被调用（至少应用了最后一个装饰器）
        assertTrue(decoratorCallCount.get() > 0, "装饰器应被调用");
    }
}
