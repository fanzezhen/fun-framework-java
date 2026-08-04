package com.github.fanzezhen.fun.framework.core.model.mapper;

import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

import java.util.List;

/**
 * 基于 Orika {@link MapperFacade} 的对象映射引擎适配器。
 * <p>
 * 包装 Orika 的映射能力，保持与迁移前完全一致的行为。仅当 classpath 存在 orika-core
 * 且通过 {@code fun.mapper.engine=orika} 选用时装配。
 * </p>
 *
 * <p><b>注意：</b>Orika 反射访问 JDK 内部类，JDK21 强封装下需配置 {@code --add-opens}；
 * 若无此约束，建议使用默认的 {@link MethodHandleObjectMapper}。</p>
 *
 * @since 4.1.0
 */
public class OrikaObjectMapper implements FunObjectMapper {

    /**
     * Orika 映射门面。
     */
    private final MapperFacade mapperFacade;

    /**
     * 使用默认 {@link DefaultMapperFactory} 构造。
     */
    public OrikaObjectMapper() {
        this(new DefaultMapperFactory.Builder().build().getMapperFacade());
    }

    /**
     * 使用指定 MapperFacade 构造（如 proxy-orika 提供的带过滤器实例）。
     *
     * @param mapperFacade Orika 映射门面
     */
    public OrikaObjectMapper(final MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public <S, D> D map(final S source, final Class<D> destinationClass) {
        return mapperFacade.map(source, destinationClass);
    }

    @Override
    public <S, D> List<D> mapAsList(final Iterable<S> source, final Class<D> destinationClass) {
        return mapperFacade.mapAsList(source, destinationClass);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <F, R> PageDTO<R> mapPage(final PageDTO<F> fromPage, final Class<F> sourceClass,
                                     final Class<R> targetClass) {
        Type<PageDTO> from = TypeFactory.valueOf(PageDTO.class, sourceClass);
        Type<PageDTO> to = TypeFactory.valueOf(PageDTO.class, targetClass);
        return mapperFacade.map(fromPage, from, to);
    }
}
