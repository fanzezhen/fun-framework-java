package com.github.fanzezhen.fun.framework.core.log.decorator;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.springboot.thread.ThreadPoolTaskDecorator;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import jakarta.annotation.Nonnull;

import java.util.Map;
import java.util.UUID;

/**
 * 跨线程传播 MDC 日志上下文的任务装饰器.
 * <p>
 * 确保在不同线程中执行任务时（例如异步操作、线程池）正确传播跟踪 ID 和其他 MDC 值。
 * 如果 MDC 中不存在跟踪 ID，则生成新的跟踪 ID。
 *
 * @since 3.1.7
 */
@Order(Integer.MAX_VALUE - 1)
@Component
public class FunLogSysContextTaskDecorator implements ThreadPoolTaskDecorator {
    /**
     * 痕迹的key.
     */
    @Value("${fun.log.key.trace-id:traceId}")
    private String traceIdKey;

    /**
     * 装饰 runnable 以将 MDC 上下文传播到执行线程.
     *
     * @param runnable 要装饰的 runnable
     * @return 具有 MDC 上下文传播的装饰后的 runnable
     */
    @Override
    public @Nonnull Runnable decorate(@Nonnull final Runnable runnable) {
        Thread thread = Thread.currentThread();
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            if (Thread.currentThread() != thread) {
                try {
                    MDC.setContextMap(map);
                    String traceId = MDC.get(traceIdKey);
                    if (CharSequenceUtil.isBlank(traceId)) {
                        traceId = UUID.randomUUID().toString();
                        MDC.put(traceIdKey, traceId);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            } else {
                runnable.run();
            }
        };
    }
}
