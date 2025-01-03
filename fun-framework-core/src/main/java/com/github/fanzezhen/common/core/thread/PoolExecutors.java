package com.github.fanzezhen.fun.framework.core.thread;

import com.github.fanzezhen.fun.framework.core.property.CommonThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;
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

    private PoolExecutors() {
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(String name) {
        return newThreadPoolExecutor(name,
            ThreadInstanceHelper.getThreadPoolProperties().getCoreSize(),
            ThreadInstanceHelper.getThreadPoolProperties().getMaxSize(),
            ThreadInstanceHelper.getThreadPoolProperties().getKeepAliveSeconds(),
            ThreadInstanceHelper.getThreadPoolProperties().getQueueCapacity(),
            null,
            null
        );
    }

    public static ThreadPoolExecutor newThreadPoolCallerRunsPolicyExecutor(String name) {
        return newThreadPoolExecutor(name,
            ThreadInstanceHelper.getThreadPoolProperties().getCoreSize(),
            ThreadInstanceHelper.getThreadPoolProperties().getMaxSize(),
            ThreadInstanceHelper.getThreadPoolProperties().getKeepAliveSeconds(),
            ThreadInstanceHelper.getThreadPoolProperties().getQueueCapacity(),
            null,
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(String name, RejectedExecutionHandler rejectedExecutionHandler) {
        return newThreadPoolExecutor(name,
            ThreadInstanceHelper.getThreadPoolProperties().getCoreSize(),
            ThreadInstanceHelper.getThreadPoolProperties().getMaxSize(),
            ThreadInstanceHelper.getThreadPoolProperties().getKeepAliveSeconds(),
            ThreadInstanceHelper.getThreadPoolProperties().getQueueCapacity(),
            null,
            rejectedExecutionHandler
        );
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(String name,
                                                           int coreSize,
                                                           int maxSize,
                                                           int keepAliveSeconds,
                                                           int queueCapacity,
                                                           ThreadFactory threadFactory,
                                                           RejectedExecutionHandler rejectedExecutionHandler) {
        return newThreadPoolExecutor(name, coreSize, maxSize, keepAliveSeconds, new LinkedBlockingQueue<>(queueCapacity), threadFactory, rejectedExecutionHandler);
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(String name,
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

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name) {
        return newThreadPoolTaskExecutor(name, ThreadInstanceHelper.getThreadPoolProperties(), ThreadInstanceHelper.getTaskDecorator(), null, null);
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, RejectedExecutionHandler rejectedExecutionHandler) {
        return newThreadPoolTaskExecutor(name, ThreadInstanceHelper.getThreadPoolProperties(), ThreadInstanceHelper.getTaskDecorator(), null, rejectedExecutionHandler
        );
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, ThreadFactory threadFactory) {
        return newThreadPoolTaskExecutor(name, ThreadInstanceHelper.getThreadPoolProperties(), ThreadInstanceHelper.getTaskDecorator(), threadFactory, null
        );
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize) {
        return newThreadPoolTaskExecutor(name, coreSize, ThreadInstanceHelper.getThreadPoolProperties().getMaxSize());
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize) {
        return newThreadPoolTaskExecutor(name, coreSize, maxSize, ThreadInstanceHelper.getThreadPoolProperties().getQueueCapacity());
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity) {
        return newThreadPoolTaskExecutor(name, coreSize, maxSize, queueCapacity, ThreadInstanceHelper.getThreadPoolProperties().getKeepAliveSeconds());
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds) {
        return newThreadPoolTaskExecutor(name, coreSize, maxSize, queueCapacity, keepAliveSeconds, null, null);
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds,
                                                                   ThreadFactory threadFactory,
                                                                   RejectedExecutionHandler rejectedExecutionHandler) {
        CommonThreadPoolProperties commonThreadPoolProperties = new CommonThreadPoolProperties(coreSize, maxSize, keepAliveSeconds, queueCapacity);
        return newThreadPoolTaskExecutor(name, commonThreadPoolProperties, ThreadInstanceHelper.getTaskDecorator(), threadFactory, rejectedExecutionHandler);
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name,
                                                                   CommonThreadPoolProperties threadPoolProperties,
                                                                   TaskDecorator taskDecorator,
                                                                   ThreadFactory threadFactory,
                                                                   RejectedExecutionHandler rejectedExecutionHandler) {
        return POOL_TASK_EXECUTOR_MAP.computeIfAbsent(name, k -> {
            ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
            threadPoolExecutor.setCorePoolSize(threadPoolProperties.getCoreSize());
            threadPoolExecutor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
            threadPoolExecutor.setMaxPoolSize(threadPoolProperties.getMaxSize());
            threadPoolExecutor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
            threadPoolExecutor.setTaskDecorator(taskDecorator);
            threadPoolExecutor.setThreadGroupName(name);
            if (threadFactory != null) {
                threadPoolExecutor.setThreadFactory(threadFactory);
            }
            if (rejectedExecutionHandler != null) {
                threadPoolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
            }
            return threadPoolExecutor;
        });
    }

    public static Map<String, ThreadPoolTaskExecutor> getPoolTaskExecutorMap() {
        return POOL_TASK_EXECUTOR_MAP;
    }
}
