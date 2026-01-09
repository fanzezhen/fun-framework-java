package com.github.fanzezhen.fun.framework.core.model;

import lombok.SneakyThrows;

import java.util.function.Function;

@FunctionalInterface
public interface FunFunction<T, R> extends Function<T, R> {
    @Override
    @SneakyThrows
    default R apply(T param){
        return call(param);
    }

    R call(T param) throws Throwable;

}
