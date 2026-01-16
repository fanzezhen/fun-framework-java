package com.github.fanzezhen.fun.framework.core.context.decorator;

import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadPoolTaskDecorator;
import jakarta.annotation.Nonnull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 上下文装饰器
 *
 * @author fanzezhen
 * @since 3.1.7
 */
@Order
@Component
@ConditionalOnClass(ContextHolder.class)
public class FunContextTaskDecorator implements ThreadPoolTaskDecorator {
    @Override
    public @Nonnull Runnable decorate(@Nonnull Runnable runnable) {
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
