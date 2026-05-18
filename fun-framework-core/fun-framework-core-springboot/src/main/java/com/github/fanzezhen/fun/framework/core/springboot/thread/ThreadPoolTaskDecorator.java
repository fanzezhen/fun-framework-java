package com.github.fanzezhen.fun.framework.core.springboot.thread;

import com.github.fanzezhen.fun.framework.core.thread.decorator.ThreadDecorator;
import org.springframework.core.task.TaskDecorator;

/**
 * 线程池任务装饰器接口
 * <p>
 * 组合了 {@link ThreadDecorator} 和 {@link TaskDecorator} 两个接口，
 * 用于在线程池任务执行前后进行上下文传递、日志记录等装饰操作。
 * </p>
 * <p>
 * 使用方式：
 * <pre>{@code
 * @Component
 * public class MyTaskDecorator implements ThreadPoolTaskDecorator {
 *     @Override
 *     public Runnable decorate(Runnable runnable) {
 *         // 保存当前线程上下文
 *         String context = getCurrentContext();
 *         return () -> {
 *             // 在新线程中恢复上下文
 *             setCurrentContext(context);
 *             try {
 *                 runnable.run();
 *             } finally {
 *                 clearCurrentContext();
 *             }
 *         };
 *     }
 * }
 * }</pre>
 * </p>
 */
@FunctionalInterface
public interface ThreadPoolTaskDecorator extends ThreadDecorator, TaskDecorator {
}
