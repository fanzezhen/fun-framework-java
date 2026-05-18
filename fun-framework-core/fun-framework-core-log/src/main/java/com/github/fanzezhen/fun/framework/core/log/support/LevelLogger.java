package com.github.fanzezhen.fun.framework.core.log.support;

/**
 * 基于级别的日志记录器接口，用于动态日志级别管理.
 * <p>
 * 提供统一的日志记录接口，可以使用不同的日志级别实现。
 * 包含用于禁用日志记录场景的 EMPTY 实现。
 */
public interface LevelLogger {
    /**
     * 记录带有可选参数的消息.
     *
     * @param message 日志消息模式
     * @param args 消息参数
     */
    void log(String message, Object... args);

    /**
     * 不执行任何操作的空日志记录器实现.
     */
    LevelLogger EMPTY = (message, args) -> {
        // Empty implementation - does not log anything
    };
}
