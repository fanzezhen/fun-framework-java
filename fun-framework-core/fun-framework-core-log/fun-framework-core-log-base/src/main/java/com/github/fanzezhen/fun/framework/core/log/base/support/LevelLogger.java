package com.github.fanzezhen.fun.framework.core.log.base.support;

public interface LevelLogger {
    void log(String var1, Object... var2);

    LevelLogger EMPTY = (var1, var2) -> {
    };
}
