package com.github.fanzezhen.fun.framework.core.thread;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.func.Consumer3;
import cn.hutool.core.lang.func.Supplier3;
import cn.hutool.core.util.ArrayUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.thread.enums.FunCoreThreadExceptionEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 并发任务编排工具，简化批量异步任务的提交和结果收集.
 * <p>
 * 典型使用场景：
 * <ul>
 * <li>批量调用RPC接口（如批量查询用户信息）</li>
 * <li>并行处理独立任务（如同时导出多个报表）</li>
 * <li>聚合查询（如同时查询订单、库存、物流状态）</li>
 * </ul>
 * <p>
 * 线程池复用策略：默认使用全局共享的
 * {@link ThreadPoolExecutorRepository#defaultThreadPoolExecutor()}，
 * 避免频繁创建销毁线程池。如有隔离需求（如长时间任务），
 * 可通过 {@link #create(ExecutorService)} 指定独立线程池。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 立即执行模式（默认）
 * List<User> users = ExecutorHolder.<User>create()
 *     .addTask(userService::getById, 1L, 2L, 3L)
 *     .get();
 *
 * // 延迟执行模式（所有任务添加完后统一提交，
 * // 适用于需要提前计算任务总数的场景）
 * List<Result> results = ExecutorHolder.<Result>create()
 *     .waitToStart()  // 标记为延迟执行
 *     .addTask(task1)
 *     .addTask(task2)
 *     .get();  // 此时才真正提交所有任务
 * }</pre>
 *
 * @param <R> 任务返回值类型
 *
 * @since 3
 */
@SuppressWarnings({"unchecked", "unused", "UnusedReturnValue"})
public class ExecutorHolder<R> {
    /**
     * 三参数任务的参数数量常量.
     */
    private static final int THREE_PARAMS = 3;

    /**
     * 默认执行器持有者实例.
     */
    private static final ExecutorHolder<?> DEFAULT_EXECUTOR_HOLDER = create();

    /**
     * 线程池执行器.
     */
    private final Executor executor;

    /**
     * 任务列表.
     */
    private final List<Task<R>> tasks;

    /**
     * 任务与异步执行结果的映射.
     */
    private final Map<Task<R>, CompletableFuture<R>> futureMap;

    /**
     * 任务执行结果映射.
     */
    private final Map<Task<R>, R> result;

    /**
     * 延迟执行标记：true时addTask不立即提交，而是等到get()时统一提交.
     * 适用于需要提前知道任务总数或批量优化提交的场景。
     */
    private boolean waitToStart;

    /**
     * 异常传播开关：true时任何子任务失败都会抛出ServiceException.
     * false时忽略异常继续收集成功结果。
     * 默认false，适用于"尽力而为"的场景（如批量查询允许部分失败）。
     */
    private boolean throwAllowed;

    /**
     * 构造函数.
     *
     * @param executorParam 执行器
     * @param size 初始容量
     */
    public ExecutorHolder(final Executor executorParam, final int size) {
        this.executor = executorParam;
        this.tasks = new ArrayList<>(size);
        this.futureMap = Collections.synchronizedMap(
                new LinkedHashMap<>(size, 1f));
        this.result = Collections.synchronizedMap(
                new LinkedHashMap<>(size, 1f));
    }

    /**
     * 构造函数.
     *
     * @param executorParam 执行器
     */
    public ExecutorHolder(final Executor executorParam) {
        this.executor = executorParam;
        this.tasks = new ArrayList<>();
        this.futureMap = Collections.synchronizedMap(new LinkedHashMap<>());
        this.result = Collections.synchronizedMap(new LinkedHashMap<>());
    }

    /**
     * 构造函数，使用默认执行器.
     *
     * @param taskSize 初始任务容量
     */
    public ExecutorHolder(final int taskSize) {
        this.executor = ThreadPoolExecutorRepository.defaultThreadPoolExecutor();
        this.tasks = new ArrayList<>(taskSize);
        this.futureMap = Collections.synchronizedMap(
                new LinkedHashMap<>(taskSize, 1f));
        this.result = Collections.synchronizedMap(
                new LinkedHashMap<>(taskSize, 1f));
    }

    /**
     * 默认构造函数，使用默认执行器.
     */
    public ExecutorHolder() {
        this(ThreadPoolExecutorRepository.defaultThreadPoolExecutor());
    }

    /**
     * 静态工具方法，异步执行任务.
     *
     * @param runnableArr 可执行任务数组
     */
    public static void asyncExec(final Runnable... runnableArr) {
        DEFAULT_EXECUTOR_HOLDER.addTask(runnableArr);
    }

    /**
     * 创建 ExecutorHolder 实例.
     *
     * @param <R> 返回值类型
     * @return ExecutorHolder 实例
     */
    public static <R> ExecutorHolder<R> create() {
        return new ExecutorHolder<>();
    }

    /**
     * 创建 ExecutorHolder 实例.
     *
     * @param executorParam 执行器服务
     * @param <R> 返回值类型
     * @return ExecutorHolder 实例
     */
    public static <R> ExecutorHolder<R> create(final ExecutorService executorParam) {
        return new ExecutorHolder<>(executorParam);
    }

    /**
     * 切换为延迟执行模式.
     * 任务将在调用get()时统一提交而非addTask时立即执行。
     * 适用场景：需要提前计算任务总数用于进度展示，
     * 或批量优化任务提交以减少线程池调度开销。
     *
     * @return 当前实例（支持链式调用）
     */
    public ExecutorHolder<R> waitToStart() {
        waitToStart = true;
        return this;
    }

    /**
     * 启用异常传播.
     * 任何子任务失败时get()将抛出ServiceException而非默默忽略。
     * 适用场景：所有子任务必须全部成功才能继续（如分布式事务的并行预检查），
     * 默认false适用于"尽力而为"场景（如批量导出允许部分失败）。
     *
     * @return 当前实例（支持链式调用）
     */
    public ExecutorHolder<R> throwAllowed() {
        throwAllowed = true;
        return this;
    }

    /**
     * 获取批量执行结果（带超时）.
     *
     * @param time     超时时间
     * @param timeUnit 时间单位
     * @return 给定时间内批量执行中正确的结果列表
     */
    public List<R> get(final long time, final TimeUnit timeUnit) {
        return getBatchResult(System.nanoTime() + timeUnit.toNanos(time));
    }

    /**
     * 获取批量执行结果（无超时限制）.
     *
     * @return 批量执行中正确的结果列表
     */
    public List<R> get() {
        return getBatchResult(-1);
    }

    /**
     * 获取批量执行结果的内部方法.
     *
     * @param endNanos 结束时间（纳秒），-1表示无超时限制
     * @return 结果列表
     */
    private List<R> getBatchResult(final long endNanos) {
        if (result.size() >= tasks.size()) {
            return ListUtil.toList(result.values());
        }
        tasks.subList(futureMap.size(), tasks.size())
                .forEach(task -> futureMap.put(task, toFuture(task)));
        futureMap.forEach((task, future) -> {
            ServiceException exception = null;
            try {
                if (!result.containsKey(task)) {
                    result.put(task, endNanos > 0 ?
                            future.get(endNanos - System.nanoTime(), TimeUnit.NANOSECONDS) :
                            future.get());
                }
            } catch (ExecutionException | TimeoutException e) {
                exception = new ServiceException(FunCoreThreadExceptionEnum.ASYNC_ERROR_THREAD_TERMINATE_ABNORMALLY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                exception = new ServiceException(FunCoreThreadExceptionEnum.ASYNC_ERROR_THREAD_TERMINATE_ABNORMALLY);
            }
            if (exception != null && throwAllowed) {
                throw exception;
            }
        });
        return ListUtil.toList(result.values());
    }

    /**
     * 添加 Runnable 任务.
     *
     * @param runnableArr 可执行任务数组
     * @return 当前实例（支持链式调用）
     */
    public ExecutorHolder<R> addTask(final Runnable... runnableArr) {
        if (runnableArr != null) {
            for (Runnable runnable : runnableArr) {
                if (runnable != null) {
                    Task<R> task = new Task<>(() -> {
                        runnable.run();
                        return null;
                    }, null);
                    addTask(task);
                }
            }
        }
        return this;
    }

    /**
     * 添加 Consumer 任务（单参数）.
     *
     * @param consumer 消费者函数
     * @param args 参数数组
     * @param <T> 参数类型
     * @return 当前实例（支持链式调用）
     */
    @SafeVarargs
    public final <T> ExecutorHolder<R> addTask(final Consumer<T> consumer, final T... args) {
        if (args != null) {
            for (T arg : args) {
                if (consumer != null) {
                    Task<R> task = new Task<>(() -> {
                        consumer.accept(arg);
                        return null;
                    }, null);
                    addTask(task);
                }
            }
        }
        return this;
    }

    /**
     * 添加 BiConsumer 任务（双参数）.
     *
     * @param consumer 双参数消费者函数
     * @param arg1 第一个参数
     * @param arg2 第二个参数
     * @param <T1> 第一个参数类型
     * @param <T2> 第二个参数类型
     * @return 当前实例（支持链式调用）
     */
    public <T1, T2> ExecutorHolder<R> addTask(final BiConsumer<T1, T2> consumer,
                                               final T1 arg1,
                                               final T2 arg2) {
        if (consumer != null) {
            Task<R> task = new Task<>(() -> {
                consumer.accept(arg1, arg2);
                return null;
            }, null);
            addTask(task);
        }
        return this;
    }

    /**
     * 批量添加 BiConsumer 任务.
     *
     * @param consumer 双参数消费者函数
     * @param towArgsArr 参数数组（每个元素为包含2个参数的数组）
     * @param <T1> 第一个参数类型
     * @param <T2> 第二个参数类型
     * @return 当前实例（支持链式调用）
     */
    public <T1, T2> ExecutorHolder<R> addTask(final BiConsumer<T1, T2> consumer, final Object[]... towArgsArr) {
        if (towArgsArr != null) {
            for (Object[] args : towArgsArr) {
                T1 arg1 = null;
                T2 arg2 = null;
                if (ArrayUtil.isNotEmpty(args)) {
                    switch (Math.min(args.length, 2)) {
                        case 2:
                            arg2 = (T2) args[1];
                            // fall through
                        case 1:
                            arg1 = (T1) args[0];
                            // fall through
                        default:
                            break;
                    }
                }
                addTask(consumer, arg1, arg2);
            }
        }
        return this;
    }

    /**
     * 添加三参数 Consumer 任务.
     *
     * @param consumer 三参数消费者函数
     * @param arg1 第一个参数
     * @param arg2 第二个参数
     * @param arg3 第三个参数
     * @param <T1> 第一个参数类型
     * @param <T2> 第二个参数类型
     * @param <T3> 第三个参数类型
     * @return 当前实例（支持链式调用）
     */
    public <T1, T2, T3> ExecutorHolder<R> addTask(final Consumer3<T1, T2, T3> consumer,
                                                   final T1 arg1,
                                                   final T2 arg2,
                                                   final T3 arg3) {
        if (consumer != null) {
            Task<R> task = new Task<>(() -> {
                consumer.accept(arg1, arg2, arg3);
                return null;
            }, null);
            addTask(task);
        }
        return this;
    }

    /**
     * 批量添加三参数 Consumer 任务.
     *
     * @param consumer 三参数消费者函数
     * @param argsArr 参数数组（每个元素为包含3个参数的数组）
     * @param <T1> 第一个参数类型
     * @param <T2> 第二个参数类型
     * @param <T3> 第三个参数类型
     * @return 当前实例（支持链式调用）
     */
    public <T1, T2, T3> ExecutorHolder<R> addTask(final Consumer3<T1, T2, T3> consumer, final Object[]... argsArr) {
        if (argsArr != null) {
            for (Object[] args : argsArr) {
                T1 arg1 = null;
                T2 arg2 = null;
                T3 arg3 = null;
                if (ArrayUtil.isNotEmpty(args)) {
                    switch (Math.min(args.length, THREE_PARAMS)) {
                        case THREE_PARAMS:
                            arg3 = (T3) args[2];
                            // fall through
                        case 2:
                            arg2 = (T2) args[1];
                            // fall through
                        case 1:
                            arg1 = (T1) args[0];
                            // fall through
                        default:
                            break;
                    }
                }
                addTask(consumer, arg1, arg2, arg3);
            }
        }
        return this;
    }

    /**
     * 添加 Supplier 任务.
     *
     * @param suppliers 任务供应者数组
     * @return 当前实例（支持链式调用）
     */
    @SafeVarargs
    public final ExecutorHolder<R> addTask(final Supplier<R>... suppliers) {
        return addTask((Function<Throwable, R>) null, suppliers);
    }

    /**
     * 添加任务并指定异常处理器.
     *
     * @param errorHandler 异常处理器
     * @param suppliers 任务供应者数组
     * @return 当前实例（支持链式调用）
     */
    @SafeVarargs
    public final ExecutorHolder<R> addTask(final Function<Throwable, R> errorHandler, final Supplier<R>... suppliers) {
        if (suppliers == null) {
            return this;
        }
        for (Supplier<R> supplier : suppliers) {
            if (supplier != null) {
                tasks.add(new Task<>(supplier, errorHandler));
            }
        }
        return this;
    }

    /**
     * 添加 Function 任务.
     *
     * @param function 函数
     * @param args 参数数组
     * @param <T> 参数类型
     * @return 当前实例（支持链式调用）
     */
    @SafeVarargs
    public final <T> ExecutorHolder<R> addTask(final Function<T, R> function, final T... args) {
        if (args == null) {
            return this;
        }
        for (T arg : args) {
            Task<R> task = new Task<>(() -> function.apply(arg), null);
            addTask(task);
        }
        return this;
    }

    /**
     * 添加 BiFunction 任务（双参数）.
     *
     * @param function 双参数函数
     * @param arg1 第一个参数
     * @param arg2 第二个参数
     * @param <T1> 第一个参数类型
     * @param <T2> 第二个参数类型
     * @return 当前实例（支持链式调用）
     */
    public <T1, T2> ExecutorHolder<R> addTask(final BiFunction<T1, T2, R> function,
                                               final T1 arg1,
                                               final T2 arg2) {
        Task<R> task = new Task<>(() -> function.apply(arg1, arg2), null);
        addTask(task);
        return this;
    }

    /**
     * 添加三参数 Supplier 任务.
     *
     * @param function 三参数供应者函数
     * @param arg1 第一个参数
     * @param arg2 第二个参数
     * @param arg3 第三个参数
     * @param <T1> 第一个参数类型
     * @param <T2> 第二个参数类型
     * @param <T3> 第三个参数类型
     * @return 当前实例（支持链式调用）
     */
    public <T1, T2, T3> ExecutorHolder<R> addTask(final Supplier3<R, T1, T2, T3> function,
                                                   final T1 arg1,
                                                   final T2 arg2,
                                                   final T3 arg3) {
        Task<R> task = new Task<>(() -> function.get(arg1, arg2, arg3), null);
        addTask(task);
        return this;
    }

    /**
     * 批量添加 BiFunction 任务.
     *
     * @param function 双参数函数
     * @param towArgsArr 参数数组（每个元素为包含2个参数的数组）
     * @param <T1> 第一个参数类型
     * @param <T2> 第二个参数类型
     * @return 当前实例（支持链式调用）
     */
    public <T1, T2> ExecutorHolder<R> addTasks(final BiFunction<T1, T2, R> function, final Object[]... towArgsArr) {
        if (towArgsArr == null) {
            return this;
        }
        for (Object[] args : towArgsArr) {
            T1 arg1 = null;
            T2 arg2 = null;
            if (ArrayUtil.isNotEmpty(args)) {
                switch (Math.min(args.length, 2)) {
                    case 2:
                        arg2 = (T2) args[1];
                        // fall through
                    case 1:
                        arg1 = (T1) args[0];
                        break;
                    default:
                        break;
                }
            }
            addTask(function, arg1, arg2);
        }
        return this;
    }

    /**
     * 批量添加三参数 Supplier 任务.
     *
     * @param function 三参数供应者函数
     * @param threeArgsArr 参数数组（每个元素为包含3个参数的数组）
     * @param <T1> 第一个参数类型
     * @param <T2> 第二个参数类型
     * @param <T3> 第三个参数类型
     * @return 当前实例（支持链式调用）
     */
    public <T1, T2, T3> ExecutorHolder<R> addTasks(final Supplier3<R, T1, T2, T3> function, final Object[]... threeArgsArr) {
        if (threeArgsArr == null) {
            return this;
        }
        for (Object[] args : threeArgsArr) {
            T1 arg1 = null;
            T2 arg2 = null;
            T3 arg3 = null;
            if (ArrayUtil.isNotEmpty(args)) {
                switch (Math.min(args.length, THREE_PARAMS)) {
                    case THREE_PARAMS:
                        arg3 = (T3) args[2];
                        // fall through
                    case 2:
                        arg2 = (T2) args[1];
                        // fall through
                    case 1:
                        arg1 = (T1) args[0];
                        // fall through
                    default:
                        break;
                }
            }
            addTask(function, arg1, arg2, arg3);
        }
        return this;
    }

    /**
     * 添加带异常处理的条件任务.
     *
     * @param function 执行函数
     * @param errorHandler 异常处理器
     * @param arg1 参数
     * @param <T> 参数类型
     * @return 当前实例（支持链式调用）
     */
    public <T> ExecutorHolder<R> addConditionTask(
            final Function<T, R> function, final Function<Throwable, R> errorHandler,
            final T arg1) {
        Task<R> task = new Task<>(() -> function.apply(arg1), errorHandler);
        addTask(task);
        return this;
    }

    /**
     * 添加带异常处理器的 Runnable 任务.
     *
     * @param errorHandler 异常处理器
     * @param runnableArr 可执行任务数组
     * @return 当前实例（支持链式调用）
     */
    public ExecutorHolder<R> addTaskWithErrorHandler(final Consumer<Throwable> errorHandler, final Runnable... runnableArr) {
        if (runnableArr != null) {
            for (Runnable runnable : runnableArr) {
                if (runnable != null) {
                    Task<R> task = new Task<>(() -> {
                        runnable.run();
                        return null;
                    }, throwable -> {
                        errorHandler.accept(throwable);
                        return null;
                    });
                    addTask(task);
                }
            }
        }
        return this;
    }

    /**
     * 忽略所有可能的异常.
     *
     * @return 当前实例（支持链式调用）
     */
    public ExecutorHolder<R> ignoreAllError() {
        this.throwAllowed = false;
        return this;
    }

    /**
     * 将任务转换为 CompletableFuture.
     *
     * @param task 任务
     * @return CompletableFuture 实例
     */
    private CompletableFuture<R> toFuture(final Task<R> task) {
        return task.errorHandler == null ?
                CompletableFuture.supplyAsync(task, executor) :
                CompletableFuture.supplyAsync(task, executor)
                        .exceptionally(task.errorHandler);
    }

    /**
     * 添加任务的内部方法.
     *
     * @param task 任务
     * @return 当前实例（支持链式调用）
     */
    private ExecutorHolder<R> addTask(final Task<R> task) {
        if (task != null) {
            tasks.add(task);
            if (!waitToStart) {
                futureMap.put(task, toFuture(task));
            }
        }
        return this;
    }

    /**
     * 任务封装类，包装 Supplier 和异常处理器.
     *
     * @param <R> 返回值类型
     */
    static class Task<R> implements Supplier<R> {

        /**
         * 任务供应者.
         */
        private final Supplier<R> supplier;

        /**
         * 异常处理器.
         */
        private final Function<Throwable, R> errorHandler;

        /**
         * 构造函数.
         *
         * @param supplierParam 任务供应者
         * @param errorHandlerParam 异常处理器
         */
        Task(final Supplier<R> supplierParam, final Function<Throwable, R> errorHandlerParam) {
            this.supplier = supplierParam;
            this.errorHandler = errorHandlerParam;
        }

        @Override
        public R get() {
            return supplier.get();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            return super.equals(obj);
        }
    }
}
