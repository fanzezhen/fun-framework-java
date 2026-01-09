package com.github.fanzezhen.fun.framework.core.thread;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.func.Consumer3;
import cn.hutool.core.lang.func.Supplier3;
import cn.hutool.core.util.ArrayUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.thread.enums.FunCoreThreadExceptionEnum;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * 线程池工具
 *
 * @author fanzezhen
 * @since 3
 */
@SuppressWarnings({"unchecked", "unused"})
public class ExecutorHolder<R> {
    private static final ExecutorHolder<?> defaultExecutorHolder = create();
    private final Executor executor;
    private final List<Task<R>> tasks;
    private final Map<Task<R>, CompletableFuture<R>> futureMap;
    private final Map<Task<R>, R> result;
    private boolean waitToStart;
    private boolean throwAllowed;

    public ExecutorHolder(Executor executor, int size) {
        this.executor = executor;
        this.tasks = new ArrayList<>(size);
        this.futureMap = Collections.synchronizedMap(new LinkedHashMap<>(size, 1f));
        this.result = Collections.synchronizedMap(new LinkedHashMap<>(size, 1f));
    }

    public ExecutorHolder(Executor executor) {
        this.executor = executor;
        this.tasks = new ArrayList<>();
        this.futureMap = Collections.synchronizedMap(new LinkedHashMap<>());
        this.result = Collections.synchronizedMap(new LinkedHashMap<>());
    }

    public ExecutorHolder(int taskSize) {
        this.executor = PoolExecutors.defaultThreadPoolTaskExecutor();
        this.tasks = new ArrayList<>(taskSize);
        this.futureMap = Collections.synchronizedMap(new LinkedHashMap<>(taskSize, 1f));
        this.result = Collections.synchronizedMap(new LinkedHashMap<>(taskSize, 1f));
    }

    public ExecutorHolder() {
        this(PoolExecutors.defaultThreadPoolTaskExecutor());
    }

    public static void asyncExec(Runnable... runnableArr) {
        defaultExecutorHolder.addTask(runnableArr);
    }

    public static <R> ExecutorHolder<R> create() {
        return new ExecutorHolder<>();
    }

    public static <R> ExecutorHolder<R> create(ExecutorService executor) {
        return new ExecutorHolder<>(executor);
    }

    public ExecutorHolder<R> waitToStart() {
        waitToStart = true;
        return this;
    }

    public ExecutorHolder<R> throwAllowed() {
        throwAllowed = true;
        return this;
    }

    /**
     * @param time     超时时间
     * @param timeUnit 时间单位
     * @return 返回给定时间内批量执行中正确的结果
     */
    public List<R> get(long time, TimeUnit timeUnit) {
        return getBatchResult(System.nanoTime() + timeUnit.toNanos(time));
    }

    /**
     * @return 返回批量执行中正确的结果
     */
    public List<R> get() {
        return getBatchResult(-1);
    }

    private List<R> getBatchResult(long endNanos) {
        if (result.size() >= tasks.size()) {
            return ListUtil.toList(result.values());
        }
        tasks.subList(futureMap.size(), tasks.size()).forEach(task -> futureMap.put(task, toFuture(task)));
        futureMap.forEach((task, future) -> {
            ServiceException exception = null;
            try {
                if (!result.containsKey(task)) {
                    result.put(task, endNanos > 0 ? future.get(endNanos - System.nanoTime(), TimeUnit.NANOSECONDS) : future.get());
                }
            } catch (ExecutionException | TimeoutException e) {
                exception = new ServiceException(FunCoreThreadExceptionEnum.ASYNC_ERROR_THREAD_TERMINATE_ABNORMALLY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 重新中断当前线程
                exception = new ServiceException(FunCoreThreadExceptionEnum.ASYNC_ERROR_THREAD_TERMINATE_ABNORMALLY);
            }
            if (exception != null && throwAllowed) {
                throw exception;
            }
        });
        return ListUtil.toList(result.values());
    }

    public ExecutorHolder<R> addTask(Runnable... runnableArr) {
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

    public <T> ExecutorHolder<R> addTask(Consumer<T> consumer, T... args) {
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

    public <T1, T2> ExecutorHolder<R> addTask(BiConsumer<T1, T2> consumer, T1 arg1, T2 arg2) {
        if (consumer != null) {
            Task<R> task = new Task<>(() -> {
                consumer.accept(arg1, arg2);
                return null;
            }, null);
            addTask(task);
        }
        return this;
    }

    public <T1, T2> ExecutorHolder<R> addTask(BiConsumer<T1, T2> consumer, Object[]... towArgsArr) {
        if (towArgsArr != null) {
            for (Object[] args : towArgsArr) {
                T1 arg1 = null;
                T2 arg2 = null;
                if (ArrayUtil.isNotEmpty(args)) {
                    switch (Math.min(args.length, 2)) {
                        case 2:
                            arg2 = (T2) args[1];
                        case 1:
                            arg1 = (T1) args[0];
                        default:
                    }
                }
                addTask(consumer, arg1, arg2);
            }
        }
        return this;
    }

    public <T1, T2, T3> ExecutorHolder<R> addTask(Consumer3<T1, T2, T3> consumer, T1 arg1, T2 arg2, T3 arg3) {
        if (consumer != null) {
            Task<R> task = new Task<>(() -> {
                consumer.accept(arg1, arg2, arg3);
                return null;
            }, null);
            addTask(task);
        }
        return this;
    }

    public <T1, T2, T3> ExecutorHolder<R> addTask(Consumer3<T1, T2, T3> consumer, Object[]... argsArr) {
        if (argsArr != null) {
            for (Object[] args : argsArr) {
                T1 arg1 = null;
                T2 arg2 = null;
                T3 arg3 = null;
                if (ArrayUtil.isNotEmpty(args)) {
                    switch (Math.min(args.length, 3)) {
                        case 3:
                            arg3 = (T3) args[2];
                        case 2:
                            arg2 = (T2) args[1];
                        case 1:
                            arg1 = (T1) args[0];
                        default:
                    }
                }
                addTask(consumer, arg1, arg2, arg3);
            }
        }
        return this;
    }

    /**
     * 添加任务
     *
     * @param suppliers 任务
     * @return 返回链式调用指针
     */
    @SafeVarargs
    public final ExecutorHolder<R> addTask(Supplier<R>... suppliers) {
        return addTask((Function<Throwable, R>) null, suppliers);
    }

    /**
     * 添加任务并指定任务发生异常时如何处理返回值
     *
     * @param suppliers    任务
     * @param errorHandler 异常处理器
     * @return 返回链式调用指针
     */
    public ExecutorHolder<R> addTask(Function<Throwable, R> errorHandler, Supplier<R>... suppliers) {
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
     * @param args     条件
     * @param function 根据条件执行的函数
     * @param <T>      条件泛型
     * @return 返回链式调用的指针
     */
    public <T> ExecutorHolder<R> addTask(Function<T, R> function, T... args) {
        if (args == null) {
            return this;
        }
        for (T arg : args) {
            Task<R> task = new Task<>(() -> function.apply(arg), null);
            addTask(task);
        }
        return this;
    }

    public <T1, T2> ExecutorHolder<R> addTask(BiFunction<T1, T2, R> function, T1 arg1, T2 arg2) {
        Task<R> task = new Task<>(() -> function.apply(arg1, arg2), null);
        addTask(task);
        return this;
    }

    public <T1, T2, T3> ExecutorHolder<R> addTask(Supplier3<R, T1, T2, T3> function, T1 arg1, T2 arg2, T3 arg3) {
        Task<R> task = new Task<>(() -> function.get(arg1, arg2, arg3), null);
        addTask(task);
        return this;
    }

    public <T1, T2> ExecutorHolder<R> addTasks(BiFunction<T1, T2, R> function, Object[]... towArgsArr) {
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
                    case 1:
                        arg1 = (T1) args[0];
                        break;
                    default:
                }
            }
            addTask(function, arg1, arg2);
        }
        return this;
    }

    public <T1, T2, T3> ExecutorHolder<R> addTasks(Supplier3<R, T1, T2, T3> function, Object[]... threeArgsArr) {
        if (threeArgsArr == null) {
            return this;
        }
        for (Object[] args : threeArgsArr) {
            T1 arg1 = null;
            T2 arg2 = null;
            T3 arg3 = null;
            if (ArrayUtil.isNotEmpty(args)) {
                switch (Math.min(args.length, 3)) {
                    case 3:
                        arg3 = (T3) args[2];
                    case 2:
                        arg2 = (T2) args[1];
                    case 1:
                        arg1 = (T1) args[0];
                    default:
                }
            }
            addTask(function, arg1, arg2, arg3);
        }
        return this;
    }

    /**
     * 添加一个任务并指定任务发生异常时如何处理返回值
     *
     * @param arg1         条件
     * @param function     执行函数
     * @param errorHandler 发生异常的异常处理器如何处理返回值
     * @return 链式调用指针
     */
    public <T> ExecutorHolder<R> addConditionTask(Function<T, R> function, Function<Throwable, R> errorHandler, T arg1) {
        Task<R> task = new Task<>(() -> function.apply(arg1), errorHandler);
        addTask(task);
        return this;
    }

    public ExecutorHolder<R> addTaskWithErrorHandler(Consumer<Throwable> errorHandler, Runnable... runnableArr) {
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
     * 忽略所有可能的异常
     *
     * @return ThreadPoolTaskExecutorHolder 链式调用的引用对象
     */
    public ExecutorHolder<R> ignoreAllError() {
        this.throwAllowed = false;
        return this;
    }

    private CompletableFuture<R> toFuture(Task<R> task) {
        return task.errorHandler == null ?
            CompletableFuture.supplyAsync(task, executor) :
            CompletableFuture.supplyAsync(task, executor).exceptionally(task.errorHandler);
    }

    private ExecutorHolder<R> addTask(Task<R> task) {
        if (task != null) {
            tasks.add(task);
            if (!waitToStart) {
                futureMap.put(task, toFuture(task));
            }
        }
        return this;
    }

    static class Task<R> implements Supplier<R> {

        Supplier<R> supplier;

        Function<Throwable, R> errorHandler;

        Task(Supplier<R> supplier, Function<Throwable, R> errorHandler) {
            this.supplier = supplier;
            this.errorHandler = errorHandler;
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
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}
