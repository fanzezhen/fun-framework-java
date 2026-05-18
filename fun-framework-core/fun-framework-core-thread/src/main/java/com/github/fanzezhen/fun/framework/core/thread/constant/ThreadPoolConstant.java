package com.github.fanzezhen.fun.framework.core.thread.constant;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池常量定义类，提供默认配置参数.
 */
public final class ThreadPoolConstant {
    /**
     * 系统CPU核心数.
     */
    public static final int CPU_CORE_SIZE = Runtime.getRuntime().availableProcessors();
    /**
     * 默认核心线程数.
     */
    public static final int DEFAULT_CORE_SIZE = 1;
    /**
     * 默认最大线程数：取CPU核心数的2倍和10中的较大值.
     */
    public static final int DEFAULT_MAX_SIZE = Math.max(10, CPU_CORE_SIZE * 2);
    /**
     * 默认队列容量：0表示直接创建新线程而不排队.
     * （注：当队列容量为0时，线程池会在达到核心线程数后立即创建新线程直到最大线程数）
     */
    public static final int DEFAULT_QUEUE_CAPACITY = 0;
    /**
     * 默认线程存活时间（秒）.
     */
    public static final int DEFAULT_KEEP_ALIVE_TIME = 60;
    /**
     * 默认时间单位.
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    /**
     * 默认执行器名称.
     */
    public static final String DEFAULT_EXECUTOR_NAME = "defaultExecutor";
    /**
     * 默认拒绝策略处理器.
     */
    public static final RejectedExecutionHandler DEFAULT_REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    /**
     * 私有构造函数，防止实例化.
     */
    private ThreadPoolConstant() {
    }
}
