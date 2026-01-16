package com.github.fanzezhen.fun.framework.core.log.base.decorator;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadPoolTaskDecorator;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import jakarta.annotation.Nonnull;

import java.util.Map;
import java.util.UUID;

/**
 * 日志版系统上下文多线程适配器
 *
 * @author fanzezhen
 * @since 3.1.7
 */
@Order(Integer.MAX_VALUE - 1)
@Component
public class FunLogSysContextTaskDecorator implements ThreadPoolTaskDecorator {
    /**
     * 痕迹的key
     */
    @Value("${fun.log.key.trace-id:traceId}")
    private String traceIdKey;

    @Override
    public @Nonnull Runnable decorate(@Nonnull Runnable runnable) {
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
