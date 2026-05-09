package com.github.fanzezhen.fun.framework.core.thread.decorator;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * 线程池上下文装饰器
 *
 * @since 3.1.7
 */
@FunctionalInterface
public interface ThreadPoolTaskDecorator {
    default String getName() {
        return CharSequenceUtil.EMPTY;
    }
    Runnable decorate(Runnable runnable);
}
