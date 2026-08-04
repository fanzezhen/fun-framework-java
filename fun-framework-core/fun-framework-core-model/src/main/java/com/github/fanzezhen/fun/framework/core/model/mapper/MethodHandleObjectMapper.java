package com.github.fanzezhen.fun.framework.core.model.mapper;

import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 {@link java.lang.invoke.MethodHandle} 的对象映射引擎（默认引擎）。
 * <p>
 * 纯 JDK 实现，零第三方依赖。通过 JavaBean 内省匹配同名属性，走 public getter/setter，
 * 首次映射某对类型时构建 {@link MappingPlan} 并缓存，后续复用。
 * </p>
 *
 * <p><b>相对 Orika 的优势：</b>不访问 JDK 内部字段，JDK21 强封装下无需 {@code --add-opens}。</p>
 *
 * <p><b>线程安全：</b>计划缓存使用 {@link ConcurrentHashMap}，{@code computeIfAbsent} 保证
 * 同一类型对只构建一次。</p>
 *
 * @since 4.1.0
 */
public class MethodHandleObjectMapper implements FunObjectMapper {

    /**
     * (源类, 目标类) → 映射计划缓存。
     */
    private final Map<CacheKey, MappingPlan> planCache = new ConcurrentHashMap<>();

    /**
     * 字段写入 hook 列表（不可变）。
     */
    private final List<FieldValueHook> hooks;

    /**
     * 构造无 hook 的引擎（非 Spring 环境默认）。
     */
    public MethodHandleObjectMapper() {
        this(Collections.emptyList());
    }

    /**
     * 构造带字段写入 hook 的引擎。
     *
     * @param hooks 字段写入 hook 列表（如 @ProxyField 脱敏）
     */
    public MethodHandleObjectMapper(final List<FieldValueHook> hooks) {
        this.hooks = hooks == null ? Collections.emptyList() : List.copyOf(hooks);
    }

    @Override
    public <S, D> D map(final S source, final Class<D> destinationClass) {
        return planFor(source.getClass(), destinationClass).map(source);
    }

    @Override
    public <S, D> List<D> mapAsList(final Iterable<S> source, final Class<D> destinationClass) {
        List<D> result = new ArrayList<>();
        for (S element : source) {
            result.add(element == null ? null : map(element, destinationClass));
        }
        return result;
    }

    @Override
    public <F, R> PageDTO<R> mapPage(final PageDTO<F> fromPage, final Class<F> sourceClass,
                                     final Class<R> targetClass) {
        return fromPage.convert(item -> item == null ? null : map(item, targetClass));
    }

    private MappingPlan planFor(final Class<?> sourceClass, final Class<?> destinationClass) {
        return planCache.computeIfAbsent(new CacheKey(sourceClass, destinationClass),
                key -> MappingPlan.build(key.source, key.destination, hooks));
    }

    /**
     * 计划缓存键：源类型 + 目标类型。
     */
    private record CacheKey(Class<?> source, Class<?> destination) {
    }
}
