package com.github.fanzezhen.fun.framework.core.springboot.thread;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.ttl.TtlRunnable;
import com.github.fanzezhen.fun.framework.core.thread.constant.ThreadPoolConstant;
import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadPoolTaskDecorator;
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
public class ThreadPoolTaskExecutorRepository {
    private static TaskDecorator taskDecorator = TtlRunnable::get;
    private static final Map<String, ThreadPoolTaskExecutor> POOL_TASK_EXECUTOR_MAP = new ConcurrentHashMap<>(2);

    public static synchronized void addDecorator(ThreadPoolTaskDecorator decorator) {
        log.info("线程池装饰器【{}】开始添加", decorator.getName());
        // 保存旧装饰器到不可变局部变量，避免闭包捕获可变静态字段引发递归
        TaskDecorator oldDecorator = taskDecorator;
        taskDecorator = runnable -> {
            runnable = decorator.decorate(oldDecorator.decorate(runnable));
            return runnable;
        };
        log.info("线程池装饰器【{}】已添加，开始启动异步任务清理旧装饰器", decorator.getName());
        new Thread(() -> destroy(Integer.MAX_VALUE)).start();
    }
    public static ThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize) {
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .name(name)
                .coreSize(coreSize)
                .maxSize(maxSize)
                .taskDecorator(taskDecorator));
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
                .name(ThreadPoolConstant.DEFAULT_EXECUTOR_NAME)
                .coreSize(ThreadPoolConstant.DEFAULT_CORE_SIZE)
                .maxSize(ThreadPoolConstant.DEFAULT_MAX_SIZE)
                .queueCapacity(ThreadPoolConstant.DEFAULT_QUEUE_CAPACITY)
                .taskDecorator(taskDecorator)
                .rejectedExecutionHandler(ThreadPoolConstant.DEFAULT_REJECTED_EXECUTION_HANDLER));
    }

    public static ThreadPoolTaskExecutor computeThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity) {
        return computeThreadPoolTaskExecutor(
            new ThreadPoolTaskExecutorBuilder()
                .name(name)
                .coreSize(coreSize)
                .maxSize(maxSize)
                .queueCapacity(queueCapacity)
                .taskDecorator(taskDecorator));
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
     * @param name 线程池名称
     * @param timeoutSeconds 等待超时时间（秒）
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
     * @return 线程池实例，不存在则返回 null
     */
    public static ThreadPoolTaskExecutor get(String name) {
        return POOL_TASK_EXECUTOR_MAP.get(name);
    }

    /**
     * 检查指定名称的线程池是否存在
     *
     * @param name 线程池名称
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
    private ThreadPoolTaskExecutorRepository() {
    }
}
