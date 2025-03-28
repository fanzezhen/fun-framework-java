package com.github.fanzezhen.fun.framework.core.log.support;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.context.properties.ContextConstant;
import com.github.fanzezhen.fun.framework.core.thread.decorator.SysContextTaskDecorator;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

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
public class FunLogSysContextTaskDecorator extends SysContextTaskDecorator {
    @Override
    public @NonNull Runnable decorate(@NonNull Runnable runnable) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            try {
                MDC.setContextMap(map);
                String traceId = MDC.get(ContextConstant.DEFAULT_HEADER_TRACE_ID);
                if (CharSequenceUtil.isBlank(traceId)) {
                    traceId = UUID.randomUUID().toString();
                    MDC.put(ContextConstant.DEFAULT_HEADER_TRACE_ID, traceId);
                }
                super.decorate(runnable).run();
            } finally {
                MDC.clear();
            }
        };
    }
}
