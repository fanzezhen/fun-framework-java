package com.github.fanzezhen.fun.framework.core.thread;

import lombok.Getter;
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
    public static final String DEFAULT_THREAD_POOL_TASK_EXECUTOR = "defaultThreadPoolTaskExecutor";

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

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize) {
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .threadGroupName(name)
                .coreSize(coreSize)
                .maxSize(maxSize)
                .taskDecorator(ThreadPoolConfig.getTaskDecorator()));
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
        return computeThreadPoolExecutor(name,
            new ThreadPoolExecutorBuilder(coreSize, maxSize, keepAliveTime)
                .unit(unit)
                .workQueue(workQueue)
                .threadFactory(new CustomizableThreadFactory(name))
                .rejectedExecutionHandler(rejectedExecutionHandler));
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

    public static ThreadPoolExecutor computeThreadPoolExecutor(String name, ThreadPoolExecutorBuilder builder) {
        return POOL_EXECUTOR_MAP.computeIfAbsent(name, k -> builder.build());
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
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .threadGroupName(DEFAULT_THREAD_POOL_TASK_EXECUTOR)
                .coreSize(DEFAULT_CORE_SIZE)
                .maxSize(DEFAULT_MAX_SIZE)
                .queueCapacity(DEFAULT_QUEUE_CAPACITY)
                .taskDecorator(ThreadPoolConfig.getTaskDecorator())
                .rejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()));
    }

    public static ThreadPoolTaskExecutor computeThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity) {
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .threadGroupName(name)
                .coreSize(coreSize)
                .maxSize(maxSize)
                .queueCapacity(queueCapacity)
                .taskDecorator(ThreadPoolConfig.getTaskDecorator()));
    }

    public static ThreadPoolTaskExecutor computeThreadPoolTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return POOL_TASK_EXECUTOR_MAP.computeIfAbsent(builder.getThreadGroupName(), k -> newThreadPoolTaskExecutor(builder));
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return builder.build();
    }

    public static Map<String, ThreadPoolTaskExecutor> getPoolTaskExecutorMap() {
        return POOL_TASK_EXECUTOR_MAP;
    }

    public static class ThreadPoolTaskExecutorBuilder {
        @Getter
        private String threadGroupName;
        private Integer coreSize;
        private Integer maxSize;
        private Integer queueCapacity;
        private Integer keepAliveSeconds;
        private TaskDecorator taskDecorator;
        private ThreadFactory threadFactory;
        private RejectedExecutionHandler rejectedExecutionHandler;

        public ThreadPoolTaskExecutorBuilder threadGroupName(String threadGroupName) {
            this.threadGroupName = threadGroupName;
            return this;
        }

        public ThreadPoolTaskExecutorBuilder coreSize(Integer coreSize) {
            this.coreSize = coreSize;
            return this;
        }

        public ThreadPoolTaskExecutorBuilder maxSize(Integer maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public ThreadPoolTaskExecutorBuilder queueCapacity(Integer queueCapacity) {
            this.queueCapacity = queueCapacity;
            return this;
        }

        public ThreadPoolTaskExecutorBuilder keepAliveSeconds(Integer keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
            return this;
        }

        public ThreadPoolTaskExecutorBuilder taskDecorator(TaskDecorator taskDecorator) {
            this.taskDecorator = taskDecorator;
            return this;
        }

        public ThreadPoolTaskExecutorBuilder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public ThreadPoolTaskExecutorBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this;
        }

        public ThreadPoolTaskExecutor build() {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            if (threadGroupName != null) {
                threadPoolTaskExecutor.setThreadGroupName(threadGroupName);
            }
            if (coreSize != null) {
                threadPoolTaskExecutor.setCorePoolSize(coreSize);
            }
            if (maxSize != null) {
                threadPoolTaskExecutor.setMaxPoolSize(maxSize);
            }
            if (queueCapacity != null) {
                threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
            }
            if (keepAliveSeconds != null) {
                threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
            }
            if (taskDecorator != null) {
                threadPoolTaskExecutor.setTaskDecorator(taskDecorator);
            }
            if (threadFactory != null) {
                threadPoolTaskExecutor.setThreadFactory(threadFactory);
            }
            if (rejectedExecutionHandler != null) {
                threadPoolTaskExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
            }
            threadPoolTaskExecutor.initialize();
            return threadPoolTaskExecutor;
        }
    }

    public static class ThreadPoolExecutorBuilder {
        @Getter
        private String name;
        private final int coreSize;
        private final int maxSize;
        private final long keepAliveTime;
        private TimeUnit unit;
        private BlockingQueue<Runnable> workQueue;
        private ThreadFactory threadFactory;
        private RejectedExecutionHandler rejectedExecutionHandler;

        public ThreadPoolExecutorBuilder(int coreSize, int maxSize, long keepAliveTime) {
            this.coreSize = coreSize;
            this.maxSize = maxSize;
            this.keepAliveTime = keepAliveTime;
        }

        public ThreadPoolExecutorBuilder unit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }

        public ThreadPoolExecutorBuilder workQueue(BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
            return this;
        }

        public ThreadPoolExecutorBuilder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public ThreadPoolExecutorBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this;
        }

        public ThreadPoolExecutor build() {
            return new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, unit, workQueue, threadFactory, rejectedExecutionHandler);
        }
    }
}
