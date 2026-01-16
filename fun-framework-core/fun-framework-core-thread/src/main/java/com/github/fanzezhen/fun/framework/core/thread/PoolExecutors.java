package com.github.fanzezhen.fun.framework.core.thread;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池工具类，提供线程池的创建、管理和配置功能
 * <p>
 * 支持创建两种类型的线程池：
 * 1. {@link ThreadPoolExecutor} - JDK原生线程池
 * 2. {@link ThreadPoolTaskExecutor} - Spring封装的线程池
 * <p>
 * 采用Builder模式和工厂方法模式，简化线程池的创建和配置
 */
@Slf4j
@SuppressWarnings("unused")
public class PoolExecutors { // 实现DisposableBean，应用关闭时销毁线程池
    /**
     * 系统CPU核心数
     */
    private static final int CPU_CORE_SIZE = Runtime.getRuntime().availableProcessors();
    /**
     * 默认核心线程数
     */
    private static final int DEFAULT_CORE_SIZE = 1;
    /**
     * 默认最大线程数：取CPU核心数的2倍和10中的较大值
     */
    private static final int DEFAULT_MAX_SIZE = Math.max(10, CPU_CORE_SIZE * 2);
    /**
     * 默认队列容量：0表示直接创建新线程而不排队
     * （注：当队列容量为0时，线程池会在达到核心线程数后立即创建新线程直到最大线程数）
     */
    private static final int DEFAULT_QUEUE_CAPACITY = 0;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 60;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    public static final String DEFAULT_EXECUTOR_NAME = "defaultExecutor";
    public static final RejectedExecutionHandler DEFAULT_REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();
    private static final Map<String, ExecutorService> POOL_EXECUTOR_MAP = new ConcurrentHashMap<>(2);
    private static final Map<String, ThreadPoolTaskExecutor> POOL_TASK_EXECUTOR_MAP = new ConcurrentHashMap<>(2);

    private PoolExecutors() {
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize) {
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .name(name)
                .coreSize(coreSize)
                .maxSize(maxSize)
                .taskDecorator(ThreadPoolConfig.getTaskDecorator()));
    }

    public static ExecutorService defaultThreadPoolExecutor() {
        return computeThreadPoolExecutor(DEFAULT_EXECUTOR_NAME,
            DEFAULT_CORE_SIZE,
            DEFAULT_MAX_SIZE,
            DEFAULT_KEEP_ALIVE_TIME,
            DEFAULT_TIME_UNIT,
            new LinkedBlockingQueue<>(0),
            DEFAULT_REJECTED_EXECUTION_HANDLER);
    }

    public static ExecutorService computeThreadPoolExecutor(String name,
                                                            int coreSize,
                                                            int maxSize,
                                                            long keepAliveTime,
                                                            TimeUnit unit,
                                                            BlockingQueue<Runnable> workQueue) {
        return computeThreadPoolExecutor(name, coreSize, maxSize, keepAliveTime, unit, workQueue, null, null);
    }

    public static ExecutorService computeThreadPoolExecutor(String name,
                                                            int coreSize,
                                                            int maxSize,
                                                            long keepAliveTime,
                                                            TimeUnit unit,
                                                            BlockingQueue<Runnable> workQueue,
                                                            RejectedExecutionHandler handler) {
        return computeThreadPoolExecutor(name, coreSize, maxSize, keepAliveTime, unit, workQueue, null, handler);
    }

    public static ExecutorService computeThreadPoolExecutor(String name,
                                                            int coreSize,
                                                            int maxSize,
                                                            long keepAliveTime,
                                                            TimeUnit unit,
                                                            BlockingQueue<Runnable> workQueue,
                                                            ThreadFactory threadFactory,
                                                            RejectedExecutionHandler rejectedExecutionHandler) {
        return POOL_EXECUTOR_MAP.computeIfAbsent(name, k -> {
            ThreadPoolExecutorBuilder threadPoolExecutorBuilder = new ThreadPoolExecutorBuilder(
                coreSize,
                maxSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory == null ? getDefaultThreadFactory(name) : threadFactory
            );
            if (rejectedExecutionHandler != null) {
                threadPoolExecutorBuilder.rejectedExecutionHandler(rejectedExecutionHandler);
            }
            return threadPoolExecutorBuilder.build();
        });
    }

    private static ThreadFactory getDefaultThreadFactory(String name) {
        DefaultManagedAwareThreadFactory threadFactory = new DefaultManagedAwareThreadFactory();
        threadFactory.setThreadGroupName(name);
        threadFactory.setThreadNamePrefix(name);
        return threadFactory;
    }

    public static ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .name(DEFAULT_EXECUTOR_NAME)
                .coreSize(DEFAULT_CORE_SIZE)
                .maxSize(DEFAULT_MAX_SIZE)
                .queueCapacity(DEFAULT_QUEUE_CAPACITY)
                .taskDecorator(ThreadPoolConfig.getTaskDecorator())
                .rejectedExecutionHandler(DEFAULT_REJECTED_EXECUTION_HANDLER));
    }

    public static ThreadPoolTaskExecutor computeThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity) {
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .name(name)
                .coreSize(coreSize)
                .maxSize(maxSize)
                .queueCapacity(queueCapacity)
                .taskDecorator(ThreadPoolConfig.getTaskDecorator()));
    }

    public static ThreadPoolTaskExecutor computeThreadPoolTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return POOL_TASK_EXECUTOR_MAP.computeIfAbsent(builder.getName(), k -> newThreadPoolTaskExecutor(builder));
    }

    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return builder.build();
    }

    public static Map<String, ThreadPoolTaskExecutor> getPoolTaskExecutorMap() {
        return POOL_TASK_EXECUTOR_MAP;
    }

    public static class ThreadPoolTaskExecutorBuilder {
        @Getter
        private String name;
        private String threadGroupName;
        private Integer coreSize;
        private Integer maxSize;
        private Integer queueCapacity;
        private Integer keepAliveSeconds;
        private TaskDecorator taskDecorator;
        private ThreadFactory threadFactory;
        private RejectedExecutionHandler rejectedExecutionHandler;

        public ThreadPoolTaskExecutorBuilder name(String name) {
            this.name = name;
            return this;
        }

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
            String threadNamePrefix =
                CharSequenceUtil.endWithAny(name, StrPool.DASHED, StrPool.UNDERLINE) ? name : name + StrPool.DASHED;
            threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
            if (threadFactory == null) {
                threadFactory = getDefaultThreadFactory(threadNamePrefix);
            }
            threadPoolTaskExecutor.setThreadFactory(threadFactory);
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


        public ThreadPoolExecutorBuilder(int coreSize,
                                         int maxSize,
                                         long keepAliveTime,
                                         TimeUnit unit,
                                         BlockingQueue<Runnable> workQueue,
                                         ThreadFactory threadFactory) {
            this.coreSize = coreSize;
            this.maxSize = maxSize;
            this.keepAliveTime = keepAliveTime;
            this.unit = unit;
            this.workQueue = workQueue;
            this.threadFactory = threadFactory;
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

        // 屏蔽AutoCloseable未关闭的提示（常驻线程池无需即时关闭）
        @SuppressWarnings("resource")
        public ExecutorService build() {
            if (workQueue == null) {
                workQueue = new LinkedBlockingQueue<>(0);
            }
            if (threadFactory == null) {
                threadFactory = getDefaultThreadFactory(name);
            }
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                rejectedExecutionHandler != null ? rejectedExecutionHandler : DEFAULT_REJECTED_EXECUTION_HANDLER
            );
            return (ExecutorService) Proxy.newProxyInstance(
                threadPoolExecutor.getClass().getClassLoader(),
                new Class<?>[]{ExecutorService.class},
                (proxy, method, args) -> {
                    if (args != null) {
                        for (int i = 0; i < args.length; i++) {
                            if (args[i] instanceof Runnable runnable) {
                                args[i] = ThreadPoolConfig.getTaskDecorator().decorate(runnable);
                            }
                        }
                    }
                    return method.invoke(threadPoolExecutor, args);
                }
            );
        }
    }

    public static void destroy() {
        log.info("开始销毁所有线程池...");
        // 销毁JDK原生线程池
        POOL_EXECUTOR_MAP.forEach((name, executor) -> {
            try {
                log.info("销毁线程池[{}]，等待任务完成...", name);
                executor.shutdown();
                // 等待60秒，若仍未关闭则强制关闭
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.warn("线程池[{}]等待超时，强制关闭...", name);
                    executor.shutdownNow();
                }
                log.info("线程池[{}]销毁完成", name);
            } catch (InterruptedException e) {
                log.error("销毁线程池[{}]时被中断", name, e);
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        });
        // 销毁Spring封装的线程池（修复核心部分）
        POOL_TASK_EXECUTOR_MAP.forEach((name, executor) -> {
            try {
                log.info("销毁Spring线程池[{}]，等待任务完成...", name);
                // 获取ThreadPoolTaskExecutor底层的原生线程池实例
                ThreadPoolExecutor underlyingExecutor = executor.getThreadPoolExecutor();
                // 调用Spring封装的shutdown方法（本质也是调用底层的shutdown）
                executor.shutdown();
                // 等待线程池终止，超时则强制关闭
                if (!underlyingExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.warn("Spring线程池[{}]等待超时，强制关闭...", name);
                    // 调用原生线程池的shutdownNow()
                    underlyingExecutor.shutdownNow();
                }
                log.info("Spring线程池[{}]销毁完成", name);
            } catch (InterruptedException e) {
                log.error("销毁Spring线程池[{}]时被中断", name, e);
                // 中断时强制关闭底层线程池
                executor.getThreadPoolExecutor().shutdownNow();
                Thread.currentThread().interrupt();
            }
        });
        log.info("所有线程池销毁完成");
    }
}
