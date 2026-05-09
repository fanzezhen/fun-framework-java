package com.github.fanzezhen.fun.framework.core.thread;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.ttl.TtlRunnable;
import com.github.fanzezhen.fun.framework.core.thread.constant.ThreadPoolConstant;
import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadPoolTaskDecorator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工具类，提供线程池的创建、管理和配置功能
 * <p>
 * 支持创建两种类型的线程池：
 * 1. {@link ThreadPoolExecutor} - JDK原生线程池
 * <p>
 * 采用Builder模式和工厂方法模式，简化线程池的创建和配置
 */
@Slf4j
@SuppressWarnings("unused")
public class ThreadPoolExecutorRepository {
    private static ThreadPoolTaskDecorator threadPoolTaskDecorator = TtlRunnable::get;
    private static final Map<String, ExecutorService> POOL_EXECUTOR_MAP = new ConcurrentHashMap<>(2);
    
    public static synchronized void addDecorator(ThreadPoolTaskDecorator decorator) {
        log.info("线程池装饰器【{}】开始添加", decorator.getName());
        // 保存旧装饰器到不可变局部变量，避免闭包捕获可变静态字段引发递归
        ThreadPoolTaskDecorator oldDecorator = threadPoolTaskDecorator;
        threadPoolTaskDecorator = runnable -> {
            runnable = decorator.decorate(oldDecorator.decorate(runnable));
            return runnable;
        };
        log.info("线程池装饰器【{}】已添加，开始启动异步任务清理旧装饰器", decorator.getName());
        new Thread(() -> destroy(Integer.MAX_VALUE)).start();
    }

    public static ExecutorService defaultThreadPoolExecutor() {
        return computeThreadPoolExecutor(ThreadPoolConstant.DEFAULT_EXECUTOR_NAME,
            ThreadPoolConstant.DEFAULT_CORE_SIZE,
            ThreadPoolConstant.DEFAULT_MAX_SIZE,
            ThreadPoolConstant.DEFAULT_KEEP_ALIVE_TIME,
            ThreadPoolConstant.DEFAULT_TIME_UNIT,
            new SynchronousQueue<>(),
            ThreadPoolConstant.DEFAULT_REJECTED_EXECUTION_HANDLER);
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
        String threadNamePrefix = CharSequenceUtil.endWithAny(name, StrPool.DASHED, StrPool.UNDERLINE) ? name : name + StrPool.DASHED;
        AtomicInteger threadNumber = new AtomicInteger(1);
        return r -> {
            Thread thread = new Thread(r);
            thread.setName(threadNamePrefix + threadNumber.getAndIncrement());
            thread.setDaemon(false); // 非守护线程
            return thread;
        };
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
                rejectedExecutionHandler != null ? 
                    rejectedExecutionHandler : ThreadPoolConstant.DEFAULT_REJECTED_EXECUTION_HANDLER
            );
            return (ExecutorService) Proxy.newProxyInstance(
                threadPoolExecutor.getClass().getClassLoader(),
                new Class<?>[]{ExecutorService.class},
                (proxy, method, args) -> {
                    if (args != null) {
                        for (int i = 0; i < args.length; i++) {
                            if (args[i] instanceof Runnable runnable) {
                                args[i] = threadPoolTaskDecorator.decorate(runnable);
                            }
                        }
                    }
                    return method.invoke(threadPoolExecutor, args);
                }
            );
        }
    }

    /**
     * 销毁所有已注册的线程池
     * <p>
     * 优雅关闭流程：
     * 1. 停止接收新任务（shutdown）
     * 2. 等待正在执行的任务完成（awaitTermination）
     * 3. 超时后强制关闭（shutdownNow）
     *
     * @param timeoutSeconds 等待超时时间（秒），超时后强制关闭
     */
    public static synchronized void destroy(int timeoutSeconds) {
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
                if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                    log.warn("线程池[{}]等待超时，强制关闭...", poolName);
                    executor.shutdownNow();

                    // 再次等待一小段时间
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
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
     * 销毁指定名称的线程池
     *
     * @param name 线程池名称
     * @param timeoutSeconds 等待超时时间（秒）
     * @return 是否销毁成功
     */
    public static synchronized boolean destroy(String name, int timeoutSeconds) {
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
            if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                log.warn("线程池[{}]等待超时，强制关闭...", name);
                executor.shutdownNow();

                // 再次等待
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
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
     * 获取指定名称的线程池
     *
     * @param name 线程池名称
     * @return 线程池实例，不存在则返回 null
     */
    public static ExecutorService get(String name) {
        return POOL_EXECUTOR_MAP.get(name);
    }

    /**
     * 检查指定名称的线程池是否存在
     *
     * @param name 线程池名称
     * @return 是否存在
     */
    public static boolean contains(String name) {
        return POOL_EXECUTOR_MAP.containsKey(name);
    }

    /**
     * 获取所有已注册的线程池名称
     *
     * @return 线程池名称集合
     */
    public static Set<String> getPoolNames() {
        return Set.copyOf(POOL_EXECUTOR_MAP.keySet());
    }

    /**
     * 获取所有已注册的线程池
     *
     * @return 线程池映射的不可变副本
     */
    public static Map<String, ExecutorService> getPoolExecutorMap() {
        return Map.copyOf(POOL_EXECUTOR_MAP);
    }

    private ThreadPoolExecutorRepository() {
    }
}
