package com.github.fanzezhen.fun.framework.log.support;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.thread.decorator.SysContextTaskDecorator;
import com.github.fanzezhen.fun.framework.log.config.LogSpringConfig;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.UUID;

/**
 * 日志版系统上下文多线程适配器
 *
 * @author fanzezhen
 * @createTime 2024/2/1 20:22
 * @since 3.1.7
 */
@Order(Integer.MAX_VALUE - 1)
public class LogSysContextTaskDecorator extends SysContextTaskDecorator {
    @Override
    public @NonNull Runnable decorate(@NonNull Runnable runnable) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            try {
                MDC.setContextMap(map);
                String traceId = MDC.get(LogSpringConfig.TRACE_ID);
                if (CharSequenceUtil.isBlank(traceId)) {
                    traceId = UUID.randomUUID().toString();
                    MDC.put(LogSpringConfig.TRACE_ID, traceId);
                }
                super.decorate(runnable).run();
            } finally {
                MDC.clear();
            }
        };
    }
}
