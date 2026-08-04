package com.github.fanzezhen.fun.framework.core.model.util;

import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import com.github.fanzezhen.fun.framework.core.model.mapper.FunObjectMapper;
import com.github.fanzezhen.fun.framework.core.model.mapper.MethodHandleObjectMapper;

import java.util.List;

/**
 * 对象映射工具类。
 * <p>
 * 提供对象映射的静态工具方法，委托可插拔的 {@link FunObjectMapper} 引擎执行。
 * 框架内置两个引擎：MethodHandle（默认，纯 JDK）与 Orika（需 classpath 存在 orika-core）。
 * 在 Spring 环境中由自动配置注入所选引擎；非 Spring 环境默认使用 MethodHandle 引擎，
 * 无需任何初始化即可使用。
 * </p>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li>DTO/VO/Entity 之间的对象转换</li>
 *   <li>集合对象批量转换</li>
 *   <li>分页结果对象转换</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 单个对象转换
 * UserBO userBO = MapperFacadeUtil.map(userEntity, UserBO.class);
 *
 * // 集合对象转换
 * List<UserBO> userBOList = MapperFacadeUtil.mapAsList(userEntityList, UserBO.class);
 *
 * // 分页对象转换
 * PageDTO<UserBO> boPage = MapperFacadeUtil.page(entityPage, UserEntity.class, UserBO.class);
 * }</pre>
 *
 * @since 4.0.5
 */
public class MapperFacadeUtil {

    private MapperFacadeUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 静态内部类持有默认映射引擎（延迟初始化）。
     * <p>
     * 利用 JVM 类加载机制实现延迟加载和线程安全：
     * 首次访问 INSTANCE 时才加载此类，JVM 保证类加载的线程安全性，
     * static final 字段保证初始化完成后的可见性。
     * </p>
     */
    private static class DefaultMapperHolder {
        static final FunObjectMapper INSTANCE = new MethodHandleObjectMapper();
    }

    /**
     * 映射引擎实例。
     * <p>
     * - 在 Spring 环境中，由自动配置在容器启动时注入所选引擎
     * - 在非 Spring 环境中，首次访问时延迟初始化为 MethodHandle 引擎
     * </p>
     */
    private static FunObjectMapper objectMapper = DefaultMapperHolder.INSTANCE;

    /**
     * 设置映射引擎实例。
     * <p>
     * 此方法由框架在 Spring 容器初始化时调用，应用代码无需手动调用。
     * </p>
     *
     * @param objectMapper 映射引擎实例
     */
    public static void setObjectMapper(final FunObjectMapper objectMapper) {
        if (objectMapper != null) {
            MapperFacadeUtil.objectMapper = objectMapper;
        }
    }

    /**
     * 将单个对象映射为指定类型的对象。
     *
     * @param sourceObject     源对象
     * @param destinationClass 目标类型
     * @param <S>              源对象类型
     * @param <D>              目标对象类型
     *
     * @return 转换后的对象，如果 sourceObject 为 null 则返回 null
     */
    public static <S, D> D map(final S sourceObject, final Class<D> destinationClass) {
        if (sourceObject == null) {
            return null;
        }
        return objectMapper.map(sourceObject, destinationClass);
    }

    /**
     * 将可迭代集合映射为指定类型的 List。
     *
     * @param source           源对象集合
     * @param destinationClass 目标类型
     * @param <S>              源对象类型
     * @param <D>              目标对象类型
     *
     * @return 转换后的 List，如果 source 为 null 则返回 null
     */
    @SuppressWarnings("java:S1168") // 返回 null 以区分"源数据不存在"与"源数据为空集合"的语义
    public static <S, D> List<D> mapAsList(final Iterable<S> source, final Class<D> destinationClass) {
        if (source == null) {
            return null;
        }
        return objectMapper.mapAsList(source, destinationClass);
    }

    /**
     * 转换分页结果对象。
     * <p>
     * 将 {@link PageDTO} 中的数据列表从源类型转换为目标类型，保持分页元数据不变。
     * </p>
     *
     * @param fromPage    源分页对象
     * @param sourceClass 源数据类型（用于类型推断）
     * @param targetClass 目标数据类型
     * @param <F>         源数据类型
     * @param <R>         目标数据类型
     *
     * @return 转换后的分页对象，如果 fromPage 为 null 则返回 null
     */
    @SuppressWarnings("java:S1168") // 返回 null 以区分"分页对象不存在"与"空分页对象"的语义
    public static <F, R> PageDTO<R> page(final PageDTO<F> fromPage, final Class<F> sourceClass,
                                         final Class<R> targetClass) {
        if (fromPage == null) {
            return null;
        }
        return objectMapper.mapPage(fromPage, sourceClass, targetClass);
    }
}
