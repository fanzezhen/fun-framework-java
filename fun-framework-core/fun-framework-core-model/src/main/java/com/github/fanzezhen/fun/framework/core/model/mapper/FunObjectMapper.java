package com.github.fanzezhen.fun.framework.core.model.mapper;

import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;

import java.util.List;

/**
 * 对象映射引擎抽象。
 * <p>
 * 定义与具体实现（Orika、MethodHandle 等）无关的映射语义，供 {@code MapperFacadeUtil} 委托。
 * 框架内置两个实现：
 * <ul>
 *   <li>{@link MethodHandleObjectMapper}：纯 JDK 实现，走 public getter/setter，默认引擎</li>
 *   <li>{@code OrikaObjectMapper}：包装 Orika MapperFacade（存在于 classpath 时）</li>
 * </ul>
 * 子项目可注册自定义 {@code FunObjectMapper} bean 覆盖框架默认实现。
 * </p>
 *
 * <p><b>空值约定：</b>实现方假定入参非 null，null 边界由调用方（门面）统一处理。</p>
 *
 * @since 4.1.0
 */
public interface FunObjectMapper {

    /**
     * 将单个对象映射为指定类型的对象。
     *
     * @param source           源对象（非 null）
     * @param destinationClass 目标类型
     * @param <S>              源对象类型
     * @param <D>              目标对象类型
     *
     * @return 转换后的对象
     */
    <S, D> D map(S source, Class<D> destinationClass);

    /**
     * 将可迭代集合映射为指定类型的 List。
     *
     * @param source           源对象集合（非 null）
     * @param destinationClass 目标类型
     * @param <S>              源对象类型
     * @param <D>              目标对象类型
     *
     * @return 转换后的 List
     */
    <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass);

    /**
     * 转换分页结果对象的行记录类型，保持分页元数据不变。
     *
     * @param fromPage    源分页对象（非 null）
     * @param sourceClass 源数据类型（用于类型推断）
     * @param targetClass 目标数据类型
     * @param <F>         源数据类型
     * @param <R>         目标数据类型
     *
     * @return 转换后的分页对象
     */
    <F, R> PageDTO<R> mapPage(PageDTO<F> fromPage, Class<F> sourceClass, Class<R> targetClass);
}
