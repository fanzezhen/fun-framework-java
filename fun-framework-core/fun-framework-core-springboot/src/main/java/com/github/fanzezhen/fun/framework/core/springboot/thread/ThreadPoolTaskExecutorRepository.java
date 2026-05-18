package com.github.fanzezhen.fun.framework.core.springboot.thread;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.ttl.TtlRunnable;
import com.github.fanzezhen.fun.framework.core.thread.constant.ThreadPoolConstant;
import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadDecorator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Spring 线程池任务执行器仓库
 * <p>
 * 提供 {@link ThreadPoolTaskExecutor} 的创建、管理和配置功能，主要特性包括：
 * <ul>
 *   <li>统一管理所有 Spring 线程池实例</li>
 *   <li>支持链式装饰器模式，实现上下文传递</li>
 *   <li>提供 Builder 模式简化线程池创建</li>
 *   <li>支持优雅关闭和资源回收</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 创建默认线程池
 * ThreadPoolTaskExecutor executor = ThreadPoolTaskExecutorRepository.defaultThreadPoolTaskExecutor();
 *
 * // 创建自定义线程池
 * ThreadPoolTaskExecutor customExecutor = ThreadPoolTaskExecutorRepository.newThreadPoolTaskExecutor(
 *     "custom-pool", 10, 20
 * );
 *
 * // 使用 Builder 模式
 * ThreadPoolTaskExecutor builderExecutor = ThreadPoolTaskExecutorRepository.computeThreadPoolTaskExecutor(
 *     new ThreadPoolTaskExecutorRepository.ThreadPoolTaskExecutorBuilder()
 *         .name("builder-pool")
 *         .coreSize(5)
 *         .maxSize(10)
 *         .queueCapacity(100)
 * );
 * }</pre>
 * </p>
 */
@Slf4j
@SuppressWarnings("unused")
public class ThreadPoolTaskExecutorRepository {
    /**
     * 全局任务装饰器，默认使用 TTL 装饰器
     */
    private static TaskDecorator taskDecorator = TtlRunnable::get;

    /**
     * 线程池实例缓存
     */
    private static final Map<String, ThreadPoolTaskExecutor> POOL_TASK_EXECUTOR_MAP = new ConcurrentHashMap<>(2);

    /**
     * 添加线程装饰器
     *
     * @param decorator 线程装饰器实例
     */
    public static synchronized void addDecorator(ThreadDecorator decorator) {
        addDecorator(decorator::decorate, decorator.getName());
    }

    /**
     * 添加任务装饰器
     * <p>
     * 装饰器采用链式组合模式，新装饰器会包装已有装饰器，
     * 执行顺序为：新装饰器 -> 旧装饰器 -> 原始任务
     * </p>
     *
     * @param decorator 任务装饰器实例
     * @param name      装饰器名称
     */
    public static synchronized void addDecorator(TaskDecorator decorator, String name) {
        log.info("线程池装饰器【{}】开始添加", name);
        // 保存旧装饰器到不可变局部变量，避免闭包捕获可变静态字段引发递归
        TaskDecorator oldDecorator = taskDecorator;
        taskDecorator = runnable -> {
            runnable = decorator.decorate(oldDecorator.decorate(runnable));
            return runnable;
        };
        log.info("线程池装饰器【{}】已添加，开始启动异步任务清理旧装饰器", name);
        new Thread(() -> destroy(Integer.MAX_VALUE)).start();
    }

    /**
     * 创建新的线程池任务执行器（不缓存）
     *
     * @param name     线程池名称
     * @param coreSize 核心线程数
     * @param maxSize  最大线程数
     * @return 新创建的线程池实例
     */
    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize) {
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .name(name)
                .coreSize(coreSize)
                .maxSize(maxSize)
                .taskDecorator(taskDecorator));
    }

    /**
     * 获取默认线程工厂
     *
     * @param name 线程名称前缀
     * @return 线程工厂实例
     */
    private static ThreadFactory getDefaultThreadFactory(String name) {
        DefaultManagedAwareThreadFactory threadFactory = new DefaultManagedAwareThreadFactory();
        threadFactory.setThreadGroupName(name);
        threadFactory.setThreadNamePrefix(name);
        return threadFactory;
    }

    /**
     * 获取或创建默认线程池
     * <p>
     * 使用默认配置：
     * <ul>
     *   <li>核心线程数：{@link ThreadPoolConstant#DEFAULT_CORE_SIZE}</li>
     *   <li>最大线程数：{@link ThreadPoolConstant#DEFAULT_MAX_SIZE}</li>
     *   <li>队列容量：{@link ThreadPoolConstant#DEFAULT_QUEUE_CAPACITY}</li>
     *   <li>拒绝策略：{@link ThreadPoolConstant#DEFAULT_REJECTED_EXECUTION_HANDLER}</li>
     * </ul>
     * </p>
     *
     * @return 默认线程池实例
     */
    public static ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .name(ThreadPoolConstant.DEFAULT_EXECUTOR_NAME)
                .coreSize(ThreadPoolConstant.DEFAULT_CORE_SIZE)
                .maxSize(ThreadPoolConstant.DEFAULT_MAX_SIZE)
                .queueCapacity(ThreadPoolConstant.DEFAULT_QUEUE_CAPACITY)
                .taskDecorator(taskDecorator)
                .rejectedExecutionHandler(ThreadPoolConstant.DEFAULT_REJECTED_EXECUTION_HANDLER));
    }

    /**
     * 获取或创建指定配置的线程池（带缓存）
     *
     * @param name          线程池名称
     * @param coreSize      核心线程数
     * @param maxSize       最大线程数
     * @param queueCapacity 队列容量
     * @return 线程池实例（如果已存在则返回缓存实例）
     */
    public static ThreadPoolTaskExecutor computeThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity) {
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .name(name)
                .coreSize(coreSize)
                .maxSize(maxSize)
                .queueCapacity(queueCapacity)
                .taskDecorator(taskDecorator));
    }

    /**
     * 获取或创建线程池（带缓存）
     * <p>
     * 使用 Builder 模式创建线程池，如果指定名称的线程池已存在则返回缓存实例
     * </p>
     *
     * @param builder 线程池构建器
     * @return 线程池实例
     */
    public static ThreadPoolTaskExecutor computeThreadPoolTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return POOL_TASK_EXECUTOR_MAP.computeIfAbsent(builder.getName(), k -> newThreadPoolTaskExecutor(builder));
    }

    /**
     * 使用 Builder 创建新的线程池（不缓存）
     *
     * @param builder 线程池构建器
     * @return 新创建的线程池实例
     */
    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return builder.build();
    }

    /**
     * 获取所有线程池的缓存 Map
     *
     * @return 线程池缓存 Map（不可变）
     */
    public static Map<String, ThreadPoolTaskExecutor> getPoolTaskExecutorMap() {
        return POOL_TASK_EXECUTOR_MAP;
    }

    /**
     * 线程池任务执行器构建器
     * <p>
     * 使用 Builder 模式简化线程池的创建和配置，支持链式调用
     * </p>
     */
    public static class ThreadPoolTaskExecutorBuilder {
        /**
         * 线程池名称
         */
        @Getter
        private String name;

        /**
         * 线程组名称
         */
        private String threadGroupName;

        /**
         * 核心线程数
         */
        private Integer coreSize;

        /**
         * 最大线程数
         */
        private Integer maxSize;

        /**
         * 队列容量
         */
        private Integer queueCapacity;

        /**
         * 空闲线程存活时间（秒）
         */
        private Integer keepAliveSeconds;

        /**
         * 任务装饰器
         */
        private TaskDecorator taskDecorator;

        /**
         * 线程工厂
         */
        private ThreadFactory threadFactory;

        /**
         * 拒绝策略处理器
         */
        private RejectedExecutionHandler rejectedExecutionHandler;

        /**
         * 设置线程池名称
         *
         * @param name 线程池名称
         * @return 构建器实例
         */
        public ThreadPoolTaskExecutorBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置线程组名称
         *
         * @param threadGroupName 线程组名称
         * @return 构建器实例
         */
        public ThreadPoolTaskExecutorBuilder threadGroupName(String threadGroupName) {
            this.threadGroupName = threadGroupName;
            return this;
        }

        /**
         * 设置核心线程数
         *
         * @param coreSize 核心线程数
         * @return 构建器实例
         */
        public ThreadPoolTaskExecutorBuilder coreSize(Integer coreSize) {
            this.coreSize = coreSize;
            return this;
        }

        /**
         * 设置最大线程数
         *
         * @param maxSize 最大线程数
         * @return 构建器实例
         */
        public ThreadPoolTaskExecutorBuilder maxSize(Integer maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        /**
         * 设置队列容量
         *
         * @param queueCapacity 队列容量
         * @return 构建器实例
         */
        public ThreadPoolTaskExecutorBuilder queueCapacity(Integer queueCapacity) {
            this.queueCapacity = queueCapacity;
            return this;
        }

        /**
         * 设置空闲线程存活时间
         *
         * @param keepAliveSeconds 存活时间（秒）
         * @return 构建器实例
         */
        public ThreadPoolTaskExecutorBuilder keepAliveSeconds(Integer keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
            return this;
        }

        /**
         * 设置任务装饰器
         *
         * @param taskDecorator 任务装饰器
         * @return 构建器实例
         */
        public ThreadPoolTaskExecutorBuilder taskDecorator(TaskDecorator taskDecorator) {
            this.taskDecorator = taskDecorator;
            return this;
        }

        /**
         * 设置线程工厂
         *
         * @param threadFactory 线程工厂
         * @return 构建器实例
         */
        public ThreadPoolTaskExecutorBuilder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        /**
         * 设置拒绝策略处理器
         *
         * @param rejectedExecutionHandler 拒绝策略处理器
         * @return 构建器实例
         */
        public ThreadPoolTaskExecutorBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this;
        }

        /**
         * 构建线程池任务执行器
         *
         * @return 配置好的线程池实例
         */
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
        Set<Map.Entry<String, ThreadPoolTaskExecutor>> executors;
        synchronized (POOL_TASK_EXECUTOR_MAP) {
            executors = Set.copyOf(POOL_TASK_EXECUTOR_MAP.entrySet());
            POOL_TASK_EXECUTOR_MAP.clear();
        }
        executors.forEach(entry -> {
            try {
                String poolName = entry.getKey();
                ThreadPoolTaskExecutor executor = entry.getValue();
                log.info("销毁线程池[{}]，当前活跃线程数: {}, 队列任务数: {}",
                    poolName, executor.getActiveCount(), executor.getThreadPoolExecutor().getQueue().size());

                // 停止接收新任务
                executor.shutdown();

                // 等待指定时间，若仍未关闭则强制关闭
                if (!executor.getThreadPoolExecutor().awaitTermination(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS)) {
                    log.warn("线程池[{}]等待超时，强制关闭...", poolName);
                    executor.getThreadPoolExecutor().shutdownNow();

                    // 再次等待一小段时间
                    if (!executor.getThreadPoolExecutor().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                        log.error("线程池[{}]强制关闭后仍有任务未完成", poolName);
                    }
                }

                // 调用 Spring 的 destroy 方法释放资源
                executor.destroy();
                log.info("线程池[{}]销毁完成", poolName);
            } catch (InterruptedException e) {
                log.error("销毁线程池[{}]时被中断", entry.getKey(), e);
                // 强制关闭
                entry.getValue().getThreadPoolExecutor().shutdownNow();
                // 恢复中断状态
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("销毁线程池[{}]时发生异常", entry.getKey(), e);
            }
        });
        log.info("所有线程池销毁完成，共销毁 {} 个线程池", executors.size());
    }

    /**
     * 销毁指定名称的线程池
     *
     * @param name           线程池名称
     * @param timeoutSeconds 等待超时时间（秒）
     *
     * @return 是否销毁成功
     */
    public static synchronized boolean destroy(String name, int timeoutSeconds) {
        ThreadPoolTaskExecutor executor = POOL_TASK_EXECUTOR_MAP.remove(name);
        if (executor == null) {
            log.warn("线程池[{}]不存在，无需销毁", name);
            return false;
        }

        try {
            log.info("销毁线程池[{}]，当前活跃线程数: {}, 队列任务数: {}",
                name, executor.getActiveCount(), executor.getThreadPoolExecutor().getQueue().size());

            // 停止接收新任务
            executor.shutdown();

            // 等待指定时间
            if (!executor.getThreadPoolExecutor().awaitTermination(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS)) {
                log.warn("线程池[{}]等待超时，强制关闭...", name);
                executor.getThreadPoolExecutor().shutdownNow();

                // 再次等待
                if (!executor.getThreadPoolExecutor().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    log.error("线程池[{}]强制关闭后仍有任务未完成", name);
                    return false;
                }
            }

            // 释放资源
            executor.destroy();
            log.info("线程池[{}]销毁完成", name);
            return true;
        } catch (InterruptedException e) {
            log.error("销毁线程池[{}]时被中断", name, e);
            executor.getThreadPoolExecutor().shutdownNow();
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
     *
     * @return 线程池实例，不存在则返回 null
     */
    public static ThreadPoolTaskExecutor get(String name) {
        return POOL_TASK_EXECUTOR_MAP.get(name);
    }

    /**
     * 检查指定名称的线程池是否存在
     *
     * @param name 线程池名称
     *
     * @return 是否存在
     */
    public static boolean contains(String name) {
        return POOL_TASK_EXECUTOR_MAP.containsKey(name);
    }

    /**
     * 获取所有已注册的线程池名称
     *
     * @return 线程池名称集合
     */
    public static Set<String> getPoolNames() {
        return Set.copyOf(POOL_TASK_EXECUTOR_MAP.keySet());
    }

    /**
     * 私有构造函数，防止实例化
     */
    private ThreadPoolTaskExecutorRepository() {
    }
}
