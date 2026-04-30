package com.github.fanzezhen.fun.framework.core.model.util;

import com.github.fanzezhen.fun.framework.core.model.result.PageResult;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

import java.util.List;

/**
 * MapperFacade 工具类
 * <p>
 * 提供对象映射的静态工具方法，封装 Orika MapperFacade 功能。
 * 容器初始化后通过 {@link #setMapperFacade(MapperFacade)} 注入 MapperFacade 实例后才可使用。
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
 * PageResult<UserBO> boPage = MapperFacadeUtil.page(entityPage, UserEntity.class, UserBO.class);
 * }</pre>
 *
 * @author fanzezhen
 * @since 4.0.5
 */
public class MapperFacadeUtil {

    private MapperFacadeUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 静态内部类持有默认 MapperFacade 实例（延迟初始化）
     * <p>
     * 利用 JVM 类加载机制实现延迟加载和线程安全：
     * 1. 只有首次访问 INSTANCE 时 JVM 才加载此类
     * 2. JVM 保证类加载的线程安全性（<clinit> 方法加锁）
     * 3. static final 字段保证初始化完成后的可见性
     * </p>
     */
    private static class DefaultMapperFacadeHolder {
        static final MapperFacade INSTANCE =
            new DefaultMapperFactory.Builder().build().getMapperFacade();
    }

    /**
     * MapperFacade 实例
     * <p>
     * - 在 Spring 环境中，由 MapperFacadeUtilConfig 在容器启动时注入
     * - 在非 Spring 环境中，首次访问时延迟初始化为 DefaultMapperFacadeHolder.INSTANCE
     * </p>
     */
    private static MapperFacade mapperFacade = DefaultMapperFacadeHolder.INSTANCE;

    /**
     * 设置 MapperFacade 实例
     * <p>
     * 此方法由框架在 Spring 容器初始化时调用，应用代码无需手动调用。
     * 在 Spring 环境中，会自动注入框架或用户自定义的 MapperFacade 实例。
     * </p>
     *
     * @param mapperFacade MapperFacade 实例
     */
    public static void setMapperFacade(MapperFacade mapperFacade) {
        MapperFacadeUtil.mapperFacade = mapperFacade;
    }

    /**
     * 将单个对象映射为指定类型的对象
     *
     * @param sourceObject     源对象
     * @param destinationClass 目标类型
     * @param <S>              源对象类型
     * @param <D>              目标对象类型
     * @return 转换后的对象，如果 sourceObject 为 null 则返回 null
     */
    public static <S, D> D map(S sourceObject, Class<D> destinationClass) {
        if (sourceObject == null) {
            return null;
        }
        return mapperFacade.map(sourceObject, destinationClass);
    }

    /**
     * 使用指定的类型信息进行对象映射
     * <p>
     * 适用于泛型类型的映射场景，如 {@code List<String>} 到 {@code List<Integer>} 等。
     * </p>
     *
     * @param sourceObject    源对象
     * @param sourceType      源对象类型信息
     * @param destinationType 目标对象类型信息
     * @param <S>             源对象类型
     * @param <D>             目标对象类型
     * @return 转换后的对象，如果 sourceObject 为 null 则返回 null
     */
    public static <S, D> D map(S sourceObject, Type<S> sourceType, Type<D> destinationType) {
        if (sourceObject == null) {
            return null;
        }
        return mapperFacade.map(sourceObject, sourceType, destinationType);
    }

    /**
     * 将可迭代集合映射为指定类型的 List
     *
     * @param source           源对象集合
     * @param destinationClass 目标类型
     * @param <S>              源对象类型
     * @param <D>              目标对象类型
     * @return 转换后的 List，如果 source 为 null 则返回 null
     */
    @SuppressWarnings("java:S1168") // 返回 null 以区分"源数据不存在"与"源数据为空集合"的语义
    public static <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass) {
        if (source == null) {
            return null;
        }
        return mapperFacade.mapAsList(source, destinationClass);
    }

    /**
     * 转换分页结果对象
     * <p>
     * 将 {@link PageResult} 中的数据列表从源类型转换为目标类型，保持分页元数据不变。
     * </p>
     *
     * @param fromPage     源分页对象
     * @param sourceClass  源数据类型（用于类型推断）
     * @param targetClass  目标数据类型
     * @param <F>          源数据类型
     * @param <R>          目标数据类型
     * @return 转换后的分页对象，如果 fromPage 为 null 则返回 null
     */
    @SuppressWarnings("java:S1168") // 返回 null 以区分"分页对象不存在"与"空分页对象"的语义
    public static <F, R> PageResult<R> page(PageResult<F> fromPage, Class<F> sourceClass, Class<R> targetClass) {
        if (fromPage == null) {
            return null;
        }
        return fromPage.convert(sourceClass, targetClass, mapperFacade);
    }
}
