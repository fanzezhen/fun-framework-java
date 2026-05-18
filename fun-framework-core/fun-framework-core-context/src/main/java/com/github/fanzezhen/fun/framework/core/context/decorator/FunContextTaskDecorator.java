package com.github.fanzezhen.fun.framework.core.context.decorator;

import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.springboot.thread.ThreadPoolTaskDecorator;
import jakarta.annotation.Nonnull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 上下文任务装饰器.
 * <p>
 * 用于在异步任务执行时自动传递上下文信息，确保子线程可以访问父线程的上下文数据。
 * 任务执行完成后自动清理子线程的上下文，防止内存泄漏。
 *
 * @since 3.1.7
 */
@Order
@Component
@ConditionalOnClass(ContextHolder.class)
public class FunContextTaskDecorator implements ThreadPoolTaskDecorator {
    /**
     * 装饰Runnable任务，在子线程中传递父线程的上下文.
     * <p>
     * 如果是同一线程执行，则直接运行；如果是不同线程，则先设置上下文，执行完成后清理。
     *
     * @param runnable 原始任务
     * @return 装饰后的任务
     */
    @Override
    @Nonnull
    public Runnable decorate(@Nonnull final Runnable runnable) {
        Thread thread = Thread.currentThread();
        JSONObject contextMap = ContextHolder.getCopyOfContextMap();
        return () -> {
            if (Thread.currentThread() != thread) {
                try {
                    ContextHolder.setContextMap(contextMap);
                    runnable.run();
                } finally {
                    ContextHolder.clean();
                }
            } else {
                runnable.run();
            }
        };
    }
}
