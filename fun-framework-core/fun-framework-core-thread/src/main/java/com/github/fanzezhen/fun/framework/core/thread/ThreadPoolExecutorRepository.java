package com.github.fanzezhen.fun.framework.core.thread;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.ttl.TtlRunnable;
import com.github.fanzezhen.fun.framework.core.thread.constant.ThreadPoolConstant;
import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadDecorator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工具类，提供线程池的创建、管理和配置功能.
 * <p>
 * 支持创建线程池：
 * <ul>
 * <li>{@link ThreadPoolExecutor} - JDK原生线程池</li>
 * </ul>
 * <p>
 * 采用Builder模式和工厂方法模式，简化线程池的创建和配置。
 * <p>
 * <b>注意：</b>本类作为线程池工厂/仓库，返回的 ExecutorService 由调用者管理其生命周期，
 * 因此不使用 try-with-resources 自动关闭。
 */
@Slf4j
@SuppressWarnings({"unused", "resource", "UnusedReturnValue"})
public final class ThreadPoolExecutorRepository {
    /**
     * 线程装饰器，用于任务执行前后的上下文传递.
     */
    private static ThreadDecorator threadDecorator = TtlRunnable::get;

    /**
     * 线程池缓存映射.
     */
    private static final Map<String, ExecutorService> POOL_EXECUTOR_MAP = new ConcurrentHashMap<>(2);

    /**
     * 添加线程装饰器.
     *
     * @param decorator 装饰器实例
     */
    public static synchronized void addDecorator(final ThreadDecorator decorator) {
        log.info("线程池装饰器【{}】开始添加", decorator.getName());
        // 保存旧装饰器到不可变局部变量，避免闭包捕获可变静态字段引发递归
        ThreadDecorator oldDecorator = threadDecorator;
        threadDecorator = runnable -> decorator.decorate(oldDecorator.decorate(runnable));
        log.info("线程池装饰器【{}】已添加，开始启动异步任务清理旧装饰器", decorator.getName());
        new Thread(() -> destroy(Integer.MAX_VALUE)).start();
    }

    /**
     * 获取默认线程池.
     *
     * @return 默认线程池实例
     */
    public static ExecutorService defaultThreadPoolExecutor() {
        return computeThreadPoolExecutor(
                ThreadPoolConstant.DEFAULT_EXECUTOR_NAME,
                ThreadPoolConstant.DEFAULT_CORE_SIZE,
                ThreadPoolConstant.DEFAULT_MAX_SIZE,
                ThreadPoolConstant.DEFAULT_KEEP_ALIVE_TIME,
                ThreadPoolConstant.DEFAULT_TIME_UNIT,
                new SynchronousQueue<>(),
                ThreadPoolConstant.DEFAULT_REJECTED_EXECUTION_HANDLER);
    }

    /**
     * 获取或创建线程池（6参数）.
     *
     * @param name 线程池名称
     * @param coreSize 核心线程数
     * @param maxSize 最大线程数
     * @param keepAliveTime 线程存活时间
     * @param unit 时间单位
     * @param workQueue 工作队列
     * @return 线程池实例
     */
    public static ExecutorService computeThreadPoolExecutor(
            final String name, final int coreSize,
            final int maxSize,
            final long keepAliveTime,
            final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        return computeThreadPoolExecutor(
                ThreadPoolConfig.builder(name)
                        .coreSize(coreSize)
                        .maxSize(maxSize)
                        .keepAliveTime(keepAliveTime)
                        .unit(unit)
                        .workQueue(workQueue)
                        .build());
    }

    /**
     * 获取或创建线程池（6参数，带拒绝策略）.
     *
     * @param name 线程池名称
     * @param coreSize 核心线程数
     * @param maxSize 最大线程数
     * @param keepAliveTime 线程存活时间
     * @param unit 时间单位
     * @param workQueue 工作队列
     * @param handler 拒绝策略处理器
     * @return 线程池实例
     */
    public static ExecutorService computeThreadPoolExecutor(
            final String name, final int coreSize,
            final int maxSize,
            final long keepAliveTime,
            final TimeUnit unit, final BlockingQueue<Runnable> workQueue,
            final RejectedExecutionHandler handler) {
        return computeThreadPoolExecutor(
                ThreadPoolConfig.builder(name)
                        .coreSize(coreSize)
                        .maxSize(maxSize)
                        .keepAliveTime(keepAliveTime)
                        .unit(unit)
                        .workQueue(workQueue)
                        .rejectedExecutionHandler(handler)
                        .build());
    }

    /**
     * 获取或创建线程池（完整参数）.
     *
     * @param config 线程池配置参数对象
     * @return 线程池实例
     */
    public static ExecutorService computeThreadPoolExecutor(
            final ThreadPoolConfig config) {
        return POOL_EXECUTOR_MAP.computeIfAbsent(config.name, k -> {
            ThreadPoolExecutorBuilder threadPoolExecutorBuilder = new ThreadPoolExecutorBuilder(
                            config.coreSize,
                            config.maxSize,
                            config.keepAliveTime,
                            config.unit,
                            config.workQueue,
                            config.threadFactory == null ?
                                    getDefaultThreadFactory(config.name) :
                                    config.threadFactory
                    );
            if (config.rejectedExecutionHandler != null) {
                threadPoolExecutorBuilder.rejectedExecutionHandler(
                        config.rejectedExecutionHandler);
            }
            return threadPoolExecutorBuilder.build();
        });
    }

    /**
     * 获取或创建线程池（7参数版本）.
     *
     * @param name 线程池名称
     * @param coreSize 核心线程数
     * @param maxSize 最大线程数
     * @param keepAliveTime 线程存活时间
     * @param unit 时间单位
     * @param workQueue 工作队列
     * @param threadFactory 线程工厂
     * @return 线程池实例
     */
    public static ExecutorService computeThreadPoolExecutor(
            final String name, final int coreSize,
            final int maxSize,
            final long keepAliveTime,
            final TimeUnit unit, final BlockingQueue<Runnable> workQueue,
            final ThreadFactory threadFactory) {
        return computeThreadPoolExecutor(
                ThreadPoolConfig.builder(name)
                        .coreSize(coreSize)
                        .maxSize(maxSize)
                        .keepAliveTime(keepAliveTime)
                        .unit(unit)
                        .workQueue(workQueue)
                        .threadFactory(threadFactory)
                        .build());
    }

    /**
     * 获取或创建线程池（包含拒绝策略）.
     *
     * @param name 线程池名称
     * @param coreSize 核心线程数
     * @param maxSize 最大线程数
     * @param keepAliveTime 线程存活时间
     * @param unit 时间单位
     * @param workQueue 工作队列
     * @param rejectedExecutionHandler 拒绝策略处理器
     * @return 线程池实例
     */
    public static ExecutorService computeThreadPoolExecutorWithHandler(
            final String name, final int coreSize,
            final int maxSize,
            final long keepAliveTime,
            final TimeUnit unit, final BlockingQueue<Runnable> workQueue,
            final RejectedExecutionHandler rejectedExecutionHandler) {
        return computeThreadPoolExecutor(
                ThreadPoolConfig.builder(name)
                        .coreSize(coreSize)
                        .maxSize(maxSize)
                        .keepAliveTime(keepAliveTime)
                        .unit(unit)
                        .workQueue(workQueue)
                        .rejectedExecutionHandler(rejectedExecutionHandler)
                        .build());
    }

    /**
     * 线程池配置参数对象.
     */
    public static final class ThreadPoolConfig {
        /**
         * 线程池名称.
         */
        private String name;

        /**
         * 核心线程数.
         */
        private int coreSize;

        /**
         * 最大线程数.
         */
        private int maxSize;

        /**
         * 线程存活时间.
         */
        private long keepAliveTime;

        /**
         * 时间单位.
         */
        private TimeUnit unit;

        /**
         * 工作队列.
         */
        private BlockingQueue<Runnable> workQueue;

        /**
         * 线程工厂.
         */
        private ThreadFactory threadFactory;

        /**
         * 拒绝策略处理器.
         */
        private RejectedExecutionHandler rejectedExecutionHandler;

        /**
         * 私有构造函数，使用 Builder 创建.
         */
        private ThreadPoolConfig() {
        }

        /**
         * 创建 Builder.
         *
         * @param nameParam 线程池名称
         * @return Builder 实例
         */
        public static Builder builder(final String nameParam) {
            return new Builder(nameParam);
        }

        /**
         * 配置对象构建器.
         */
        public static final class Builder {
            /**
             * 配置对象.
             */
            private final ThreadPoolConfig config = new ThreadPoolConfig();

            /**
             * 构造函数.
             *
             * @param name 线程池名称
             */
            private Builder(final String name) {
                config.name = name;
            }

            /**
             * 设置核心线程数.
             *
             * @param coreSizeParam 核心线程数
             * @return Builder 实例
             */
            public Builder coreSize(final int coreSizeParam) {
                config.coreSize = coreSizeParam;
                return this;
            }

            /**
             * 设置最大线程数.
             *
             * @param maxSizeParam 最大线程数
             * @return Builder 实例
             */
            public Builder maxSize(final int maxSizeParam) {
                config.maxSize = maxSizeParam;
                return this;
            }

            /**
             * 设置线程存活时间.
             *
             * @param keepAliveTimeParam 线程存活时间
             * @return Builder 实例
             */
            public Builder keepAliveTime(final long keepAliveTimeParam) {
                config.keepAliveTime = keepAliveTimeParam;
                return this;
            }

            /**
             * 设置时间单位.
             *
             * @param unitParam 时间单位
             * @return Builder 实例
             */
            public Builder unit(final TimeUnit unitParam) {
                config.unit = unitParam;
                return this;
            }

            /**
             * 设置工作队列.
             *
             * @param workQueueParam 工作队列
             * @return Builder 实例
             */
            public Builder workQueue(final BlockingQueue<Runnable> workQueueParam) {
                config.workQueue = workQueueParam;
                return this;
            }

            /**
             * 设置线程工厂.
             *
             * @param threadFactoryParam 线程工厂
             * @return Builder 实例
             */
            public Builder threadFactory(final ThreadFactory threadFactoryParam) {
                config.threadFactory = threadFactoryParam;
                return this;
            }

            /**
             * 设置拒绝策略处理器.
             *
             * @param rejectedExecutionHandlerParam 拒绝策略处理器
             * @return Builder 实例
             */
            public Builder rejectedExecutionHandler(
                    final RejectedExecutionHandler
                            rejectedExecutionHandlerParam) {
                config.rejectedExecutionHandler = rejectedExecutionHandlerParam;
                return this;
            }

            /**
             * 构建配置对象.
             *
             * @return 线程池配置对象
             */
            public ThreadPoolConfig build() {
                return config;
            }
        }
    }

    /**
     * 获取默认线程工厂.
     *
     * @param name 线程名称前缀
     * @return 线程工厂
     */
    private static ThreadFactory getDefaultThreadFactory(final String name) {
        String threadNamePrefix = CharSequenceUtil.endWithAny(name, StrPool.DASHED, StrPool.UNDERLINE) ?
                name : name + StrPool.DASHED;
        AtomicInteger threadNumber = new AtomicInteger(1);
        return r -> {
            Thread thread = new Thread(r);
            thread.setName(threadNamePrefix + threadNumber.getAndIncrement());
            thread.setDaemon(false); // 非守护线程
            return thread;
        };
    }

    /**
     * 线程池构建器，使用 Builder 模式简化线程池配置.
     */
    public static class ThreadPoolExecutorBuilder {
        /**
         * 线程池名称.
         */
        @Getter
        private String name;

        /**
         * 核心线程数.
         */
        private final int coreSize;

        /**
         * 最大线程数.
         */
        private final int maxSize;

        /**
         * 线程存活时间.
         */
        private final long keepAliveTime;

        /**
         * 时间单位.
         */
        private TimeUnit unit;

        /**
         * 工作队列.
         */
        private BlockingQueue<Runnable> workQueue;

        /**
         * 线程工厂.
         */
        private ThreadFactory threadFactory;

        /**
         * 拒绝策略处理器.
         */
        private RejectedExecutionHandler rejectedExecutionHandler;

        /**
         * 构造函数（3参数）.
         *
         * @param coreSizeParam 核心线程数
         * @param maxSizeParam 最大线程数
         * @param keepAliveTimeParam 线程存活时间
         */
        public ThreadPoolExecutorBuilder(final int coreSizeParam,
                                         final int maxSizeParam,
                                         final long keepAliveTimeParam) {
            this.coreSize = coreSizeParam;
            this.maxSize = maxSizeParam;
            this.keepAliveTime = keepAliveTimeParam;
        }

        /**
         * 构造函数（完整参数）.
         *
         * @param coreSizeParam 核心线程数
         * @param maxSizeParam 最大线程数
         * @param keepAliveTimeParam 线程存活时间
         * @param unitParam 时间单位
         * @param workQueueParam 工作队列
         * @param threadFactoryParam 线程工厂
         */
        public ThreadPoolExecutorBuilder(final int coreSizeParam,
                                         final int maxSizeParam,
                                         final long keepAliveTimeParam,
                                         final TimeUnit unitParam,
                                         final BlockingQueue<Runnable> workQueueParam,
                                         final ThreadFactory threadFactoryParam) {
            this.coreSize = coreSizeParam;
            this.maxSize = maxSizeParam;
            this.keepAliveTime = keepAliveTimeParam;
            this.unit = unitParam;
            this.workQueue = workQueueParam;
            this.threadFactory = threadFactoryParam;
        }

        /**
         * 设置时间单位.
         *
         * @param unitParam 时间单位
         * @return 当前构建器实例
         */
        public ThreadPoolExecutorBuilder unit(final TimeUnit unitParam) {
            this.unit = unitParam;
            return this;
        }

        /**
         * 设置工作队列.
         *
         * @param workQueueParam 工作队列
         * @return 当前构建器实例
         */
        public ThreadPoolExecutorBuilder workQueue(
                final BlockingQueue<Runnable> workQueueParam) {
            this.workQueue = workQueueParam;
            return this;
        }

        /**
         * 设置线程工厂.
         *
         * @param threadFactoryParam 线程工厂
         * @return 当前构建器实例
         */
        public ThreadPoolExecutorBuilder threadFactory(
                final ThreadFactory threadFactoryParam) {
            this.threadFactory = threadFactoryParam;
            return this;
        }

        /**
         * 设置拒绝策略处理器.
         *
         * @param rejectedExecutionHandlerParam 拒绝策略处理器
         * @return 当前构建器实例
         */
        public ThreadPoolExecutorBuilder rejectedExecutionHandler(final RejectedExecutionHandler rejectedExecutionHandlerParam) {
            this.rejectedExecutionHandler = rejectedExecutionHandlerParam;
            return this;
        }

        /**
         * 构建线程池.
         * 屏蔽AutoCloseable未关闭的提示（常驻线程池无需即时关闭）
         *
         * @return 线程池实例
         */
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
                    rejectedExecutionHandler != null ?
                            rejectedExecutionHandler :
                            ThreadPoolConstant.DEFAULT_REJECTED_EXECUTION_HANDLER
            );
            return (ExecutorService) Proxy.newProxyInstance(
                    threadPoolExecutor.getClass().getClassLoader(),
                    new Class<?>[]{ExecutorService.class},
                    (proxy, method, args) -> {
                        if (args != null) {
                            for (int i = 0; i < args.length; i++) {
                                if (args[i] instanceof Runnable runnable) {
                                    args[i] = threadDecorator.decorate(runnable);
                                }
                            }
                        }
                        return method.invoke(threadPoolExecutor, args);
                    }
            );
        }
    }

    /**
     * 销毁所有已注册的线程池.
     * <p>
     * 优雅关闭流程：
     * <ol>
     * <li>停止接收新任务（shutdown）</li>
     * <li>等待正在执行的任务完成（awaitTermination）</li>
     * <li>超时后强制关闭（shutdownNow）</li>
     * </ol>
     *
     * @param timeoutSeconds 等待超时时间（秒），超时后强制关闭
     */
    public static synchronized void destroy(final int timeoutSeconds) {
        log.info("开始销毁所有线程池，超时时间: {}秒", timeoutSeconds);
        Set<Map.Entry<String, ExecutorService>> executorServices;
        synchronized (POOL_EXECUTOR_MAP) {
            executorServices = Set.copyOf(POOL_EXECUTOR_MAP.entrySet());
            POOL_EXECUTOR_MAP.clear();
        }
        executorServices.forEach(entry -> {
            try {
                String poolName = entry.getKey();
                ExecutorService executor = entry.getValue();
                log.info("销毁线程池[{}]，等待任务完成...", poolName);

                // 停止接收新任务
                executor.shutdown();

                // 等待指定时间，若仍未关闭则强制关闭
                if (!executor.awaitTermination(timeoutSeconds,
                        TimeUnit.SECONDS)) {
                    log.warn("线程池[{}]等待超时，强制关闭...", poolName);
                    executor.shutdownNow();

                    // 再次等待一小段时间
                    final int extraWaitSeconds = 5;
                    if (!executor.awaitTermination(extraWaitSeconds, TimeUnit.SECONDS)) {
                        log.error("线程池[{}]强制关闭后仍有任务未完成", poolName);
                    }
                }
                log.info("线程池[{}]销毁完成", poolName);
            } catch (InterruptedException e) {
                log.error("销毁线程池[{}]时被中断", entry.getKey(), e);
                entry.getValue().shutdownNow();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("销毁线程池[{}]时发生异常", entry.getKey(), e);
            }
        });
        log.info("所有线程池销毁完成，共销毁 {} 个线程池", executorServices.size());
    }

    /**
     * 销毁指定名称的线程池.
     *
     * @param name 线程池名称
     * @param timeoutSeconds 等待超时时间（秒）
     * @return 是否销毁成功
     */
    public static synchronized boolean destroy(final String name, final int timeoutSeconds) {
        ExecutorService executor = POOL_EXECUTOR_MAP.remove(name);
        if (executor == null) {
            log.warn("线程池[{}]不存在，无需销毁", name);
            return false;
        }

        try {
            log.info("销毁线程池[{}]，等待任务完成...", name);

            // 停止接收新任务
            executor.shutdown();

            // 等待指定时间
            if (!executor.awaitTermination(timeoutSeconds,
                    TimeUnit.SECONDS)) {
                log.warn("线程池[{}]等待超时，强制关闭...", name);
                executor.shutdownNow();

                // 再次等待
                final int extraWaitSeconds = 5;
                if (!executor.awaitTermination(extraWaitSeconds,
                        TimeUnit.SECONDS)) {
                    log.error("线程池[{}]强制关闭后仍有任务未完成", name);
                    return false;
                }
            }
            log.info("线程池[{}]销毁完成", name);
            return true;
        } catch (InterruptedException e) {
            log.error("销毁线程池[{}]时被中断", name, e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            log.error("销毁线程池[{}]时发生异常", name, e);
            return false;
        }
    }

    /**
     * 获取指定名称的线程池.
     *
     * @param name 线程池名称
     * @return 线程池实例，不存在则返回 null
     */
    public static ExecutorService get(final String name) {
        return POOL_EXECUTOR_MAP.get(name);
    }

    /**
     * 检查指定名称的线程池是否存在.
     *
     * @param name 线程池名称
     * @return 是否存在
     */
    public static boolean contains(final String name) {
        return POOL_EXECUTOR_MAP.containsKey(name);
    }

    /**
     * 获取所有已注册的线程池名称.
     *
     * @return 线程池名称集合
     */
    public static Set<String> getPoolNames() {
        return Set.copyOf(POOL_EXECUTOR_MAP.keySet());
    }

    /**
     * 获取所有已注册的线程池.
     *
     * @return 线程池映射的不可变副本
     */
    public static Map<String, ExecutorService> getPoolExecutorMap() {
        return Map.copyOf(POOL_EXECUTOR_MAP);
    }

    /**
     * 私有构造函数，防止实例化.
     */
    private ThreadPoolExecutorRepository() {
    }
}
