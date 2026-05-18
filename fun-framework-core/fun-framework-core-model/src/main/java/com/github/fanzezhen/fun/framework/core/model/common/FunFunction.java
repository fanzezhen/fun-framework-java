package com.github.fanzezhen.fun.framework.core.model.common;

import lombok.SneakyThrows;

import java.util.function.Function;

/**
 * 函数式接口
 * <p>
 * 扩展 Java 标准 Function 接口，支持抛出异常的函数调用。
 * </p>
 *
 * @param <T> 输入参数类型
 * @param <R> 返回结果类型
 */
@FunctionalInterface
public interface FunFunction<T, R> extends Function<T, R> {
    /**
     * 应用函数
     * <p>
     * 实现 Function 接口的 apply 方法，将检查异常转换为非检查异常
     * </p>
     *
     * @param param 输入参数
     * @return 函数执行结果
     */
    @Override
    @SneakyThrows
    default R apply(T param){
        return call(param);
    }

    /**
     * 执行函数调用
     * <p>
     * 可抛出任意异常的函数执行方法，由 apply 方法捕获并重新抛出
     * </p>
     *
     * @param param 输入参数
     * @return 函数执行结果
     * @throws Throwable 函数执行过程中可能抛出的任意异常
     */
    R call(T param) throws Throwable;

}
