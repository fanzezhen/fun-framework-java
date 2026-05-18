package com.github.fanzezhen.fun.framework.security.sa.token;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.github.fanzezhen.fun.framework.core.springboot.thread.ThreadPoolTaskDecorator;
import jakarta.annotation.Nonnull;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Sa-Token 系统上下文多线程修饰器。
 * <p>
 * 在多线程环境下传递 Sa-Token 的上下文信息（如 Token 值），
 * 确保子线程能够正确获取当前用户的登录状态。
 *
 * @since 3.1.7
 */
@Order(Integer.MAX_VALUE - 1)
@Component
public class FunSaTokenTaskDecorator implements ThreadPoolTaskDecorator {

    /**
     * 装饰 Runnable 任务，传递 Sa-Token 上下文。
     *
     * @param runnable 原始任务
     * @return 装饰后的任务
     */
    @Override
    public @Nonnull Runnable decorate(@Nonnull final Runnable runnable) {
        Thread thread = Thread.currentThread();
        String tokenValue = StpUtil.getTokenValue();
        return () -> {
            if (Thread.currentThread() != thread) {
                try {
                    // 传递 Sa-Token 上下文
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
