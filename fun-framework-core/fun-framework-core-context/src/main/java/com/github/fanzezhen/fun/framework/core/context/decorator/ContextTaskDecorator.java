package com.github.fanzezhen.fun.framework.core.context.decorator;

import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadPoolTaskDecorator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
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
public class ContextTaskDecorator implements ThreadPoolTaskDecorator {
    @Override
    public @NonNull Runnable decorate(@NonNull Runnable runnable) {
        Thread thread = Thread.currentThread();
        JSONObject contextMap = ContextHolder.getContextMap();
        return () -> {
            try {
                ContextHolder.setContextMap(contextMap);
                runnable.run();
            } finally {
                if (Thread.currentThread() != thread) {
                    ContextHolder.clean();
                }
            }
        };
    }
}
