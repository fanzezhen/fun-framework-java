package com.github.fanzezhen.fun.framework.core.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author zezhen.fan
 */
@Slf4j
@SuppressWarnings("unused")
public class PoolExecutors {
    private static final Map<String, ThreadPoolExecutor> POOL_EXECUTOR_MAP = new ConcurrentHashMap<>(2);
    private static final Map<String, ThreadPoolTaskExecutor> POOL_TASK_EXECUTOR_MAP = new ConcurrentHashMap<>(2);
    private static final int DEFAULT_CORE_SIZE = 1;
    private static final int DEFAULT_MAX_SIZE = 10;
    private static final int DEFAULT_QUEUE_CAPACITY = 0;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 60;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    private PoolExecutors() {
    }

    public static ThreadPoolExecutor defaultThreadPoolExecutor() {
        return computeThreadPoolExecutor("defaultThreadPoolExecutor",
            DEFAULT_CORE_SIZE,
            DEFAULT_MAX_SIZE,
            DEFAULT_KEEP_ALIVE_TIME,
            DEFAULT_TIME_UNIT,
            new LinkedBlockingQueue<>(0),
            new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ThreadPoolExecutor computeThreadPoolExecutor(String name,
                                                               int coreSize,
                                                               int maxSize,
                                                               long keepAliveTime,
                                                               TimeUnit unit,
                                                               BlockingQueue<Runnable> workQueue) {
        return computeThreadPoolExecutor(name, coreSize, maxSize, keepAliveTime, unit, workQueue, new CustomizableThreadFactory(name));
    }

    public static ThreadPoolExecutor computeThreadPoolExecutor(String name,
                                                               int coreSize,
                                                               int maxSize,
                                                               long keepAliveTime,
                                                               TimeUnit unit,
                                                               BlockingQueue<Runnable> workQueue,
                                                               RejectedExecutionHandler rejectedExecutionHandler) {
        return computeThreadPoolExecutor(name, coreSize, maxSize, keepAliveTime, unit, workQueue, new CustomizableThreadFactory(name), rejectedExecutionHandler);
    }

    public static ThreadPoolExecutor computeThreadPoolExecutor(String name,
                                                               int coreSize,
                                                               int maxSize,
                                                               long keepAliveTime,
                                                               TimeUnit unit,
                                                               BlockingQueue<Runnable> workQueue,
                                                               ThreadFactory threadFactory) {
        return POOL_EXECUTOR_MAP.computeIfAbsent(name, k -> new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, unit, workQueue, threadFactory));
    }

    public static ThreadPoolExecutor computeThreadPoolExecutor(String name,
                                                               int coreSize,
                                                               int maxSize,
                                                               long keepAliveTime,
                                                               TimeUnit unit,
                                                               BlockingQueue<Runnable> workQueue,
                                                               ThreadFactory threadFactory,
                                                               RejectedExecutionHandler rejectedExecutionHandler) {
        return POOL_EXECUTOR_MAP.computeIfAbsent(name,
            k -> new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, unit, workQueue, threadFactory, rejectedExecutionHandler));
    }

    public static ThreadPoolExecutor computeThreadPoolExecutor(String name,
                                                               int coreSize,
                                                               int maxSize,
                                                               int keepAliveSeconds,
                                                               int queueCapacity,
                                                               ThreadFactory threadFactory,
                                                               RejectedExecutionHandler rejectedExecutionHandler) {
        return computeThreadPoolExecutor(name, coreSize, maxSize, keepAliveSeconds, new LinkedBlockingQueue<>(queueCapacity), threadFactory, rejectedExecutionHandler);
    }

    public static ThreadPoolExecutor computeThreadPoolExecutor(String name,
                                                               int coreSize,
                                                               int maxSize,
                                                               int keepAliveSeconds,
                                                               BlockingQueue<Runnable> workQueue,
                                                               ThreadFactory threadFactory,
                                                               RejectedExecutionHandler rejectedExecutionHandler) {
        return POOL_EXECUTOR_MAP.computeIfAbsent(name, k -> {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory != null ? threadFactory : Executors.defaultThreadFactory()
            );
            if (rejectedExecutionHandler != null) {
                threadPoolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
            }
            return threadPoolExecutor;
        });
    }

    public static ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
        return computeThreadPoolTaskExecutor("defaultThreadPoolTaskExecutor",
            DEFAULT_CORE_SIZE,
            DEFAULT_MAX_SIZE,
            DEFAULT_QUEUE_CAPACITY,
            null,
            ThreadPoolConfig.getTaskDecorator(),
            null,
            new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ThreadPoolTaskExecutor computeThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity) {
        return computeThreadPoolTaskExecutor(name, coreSize, maxSize, queueCapacity, null, ThreadPoolConfig.getTaskDecorator(), null, null);
    }

    public static ThreadPoolTaskExecutor computeThreadPoolTaskExecutor(String name,
                                                                       Integer coreSize,
                                                                       Integer maxSize,
                                                                       Integer queueCapacity,
                                                                       Integer keepAliveSeconds,
                                                                       TaskDecorator taskDecorator,
                                                                       ThreadFactory threadFactory,
                                                                       RejectedExecutionHandler rejectedExecutionHandler) {
        return POOL_TASK_EXECUTOR_MAP.computeIfAbsent(name, k ->
            newThreadPoolTaskExecutor(name, coreSize, maxSize, queueCapacity, keepAliveSeconds, taskDecorator, threadFactory, rejectedExecutionHandler));
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String threadGroupName,
                                                                   Integer coreSize,
                                                                   Integer maxSize,
                                                                   Integer queueCapacity,
                                                                   Integer keepAliveSeconds,
                                                                   TaskDecorator taskDecorator,
                                                                   ThreadFactory threadFactory,
                                                                   RejectedExecutionHandler rejectedExecutionHandler) {
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        if (threadGroupName != null) {
            threadPoolExecutor.setThreadGroupName(threadGroupName);
        }
        if (coreSize != null) {
            threadPoolExecutor.setCorePoolSize(coreSize);
        }
        if (maxSize != null) {
            threadPoolExecutor.setMaxPoolSize(maxSize);
        }
        if (queueCapacity != null) {
            threadPoolExecutor.setQueueCapacity(queueCapacity);
        }
        if (keepAliveSeconds != null) {
            threadPoolExecutor.setKeepAliveSeconds(keepAliveSeconds);
        }
        if (taskDecorator != null) {
            threadPoolExecutor.setTaskDecorator(taskDecorator);
        }
        if (threadFactory != null) {
            threadPoolExecutor.setThreadFactory(threadFactory);
        }
        if (rejectedExecutionHandler != null) {
            threadPoolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        }
        threadPoolExecutor.initialize();
        return threadPoolExecutor;
    }

    public static Map<String, ThreadPoolTaskExecutor> getPoolTaskExecutorMap() {
        return POOL_TASK_EXECUTOR_MAP;
    }
}
