package com.github.fanzezhen.fun.framework.log.support;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.thread.SysContextTaskDecorator;
import com.github.fanzezhen.fun.framework.core.thread.ThreadInstanceHelper;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

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
    public Runnable decorate(Runnable runnable) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            try {
                MDC.setContextMap(map);
                String traceId = MDC.get(ThreadInstanceHelper.TRACE_ID);
                if (CharSequenceUtil.isBlank(traceId)) {
                    traceId = UUID.randomUUID().toString();
                    MDC.put(ThreadInstanceHelper.TRACE_ID, traceId);
                }
                super.decorate(runnable).run();
            } finally {
                MDC.clear();
            }
        };
    }
}
