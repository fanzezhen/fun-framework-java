package com.github.fanzezhen.fun.framework.core.thread.decorator;

import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.SysContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;

/**
 * 上下文装饰器
 *
 * @author fanzezhen
 * @createTime 2024/2/1 19:05
 * @since 3.1.7
 */
@Order
public class SysContextTaskDecorator implements ThreadPoolTaskDecorator {
    @Override
    public @NonNull Runnable decorate(@NonNull Runnable runnable) {
        Thread thread = Thread.currentThread();
        JSONObject contextMap = SysContextHolder.getContextMap();
        return () -> {
            try {
                SysContextHolder.setContextMap(contextMap);
                runnable.run();
            } finally {
                if (Thread.currentThread() != thread) {
                    SysContextHolder.clean();
                }
            }
        };
    }
}
