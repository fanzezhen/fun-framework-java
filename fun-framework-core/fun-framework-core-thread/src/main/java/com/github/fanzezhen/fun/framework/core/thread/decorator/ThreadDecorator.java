package com.github.fanzezhen.fun.framework.core.thread.decorator;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * 线程池上下文装饰器.
 * <p>
 * 用于在任务执行前后进行上下文传递和增强处理，如 MDC、用户上下文等。
 * 典型实现包括：TTL、MDC 装饰器等。
 */
@FunctionalInterface
public interface ThreadDecorator {
    /**
     * 获取装饰器名称.
     * <p>
     * 用于日志记录和装饰器识别，默认返回空字符串。
     *
     * @return 装饰器名称，默认为空字符串
     */
    default String getName() {
        return CharSequenceUtil.EMPTY;
    }

    /**
     * 装饰 Runnable 任务，在任务执行前后进行上下文传递.
     *
     * @param runnable 原始任务，不能为 null
     * @return 装饰后的任务
     */
    Runnable decorate(Runnable runnable);
}
