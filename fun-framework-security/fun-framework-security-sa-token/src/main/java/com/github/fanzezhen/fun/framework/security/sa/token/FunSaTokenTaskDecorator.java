package com.github.fanzezhen.fun.framework.security.sa.token;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadPoolTaskDecorator;
import jakarta.annotation.Nonnull;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * SaToken系统上下文多线程修饰器
 *
 * @author fanzezhen
 * @since 3.1.7
 */
@Order(Integer.MAX_VALUE - 1)
@Component
public class FunSaTokenTaskDecorator implements ThreadPoolTaskDecorator {

    @Override
    public @Nonnull Runnable decorate(@Nonnull Runnable runnable) {
        Thread thread = Thread.currentThread();
        String tokenValue = StpUtil.getTokenValue();
        return () -> {
            if (Thread.currentThread() != thread) {
                try {
                    // 传递sa-token
                    SaTokenContextMockUtil.setMockContext(() -> {
                        StpUtil.setTokenValueToStorage(tokenValue);
                        // 执行任务
                        runnable.run();
                    });
                } finally {
                    if (Thread.currentThread() != thread) {
                        MDC.clear();
                    }
                }
            } else {
                // 执行任务
                runnable.run();
            }
        };
    }
}
