package com.github.fanzezhen.fun.framework.proxy.orika;

import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyField;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomFilter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Orika转换器
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
@Slf4j
@Component
@ConditionalOnBean({ProxyHelper.class})
public class ProxyOrikaFilter extends CustomFilter<String, String> {
    private static final WeakConcurrentMap<Type<?>, Set<?>> CACHE = new WeakConcurrentMap<>();
    protected boolean enabled;
    @Resource
    private ProxyHelper proxyHelper;

    @Override
    public boolean filtersSource() {
        return false;
    }

    @Override
    public boolean filtersDestination() {
        return true;
    }

    @Override
    public <S extends String, D extends String> boolean shouldMap(Type<S> sourceType, String sourceName, S source, Type<D> destType, String destName, D dest, MappingContext mappingContext) {
        try {
            if (enabled) {
                Type<?> destinationType = mappingContext.getResolvedDestinationType();
                return CACHE.computeIfAbsent(destinationType,
                                k -> Arrays.stream(ReflectUtil.getFields(destinationType.getRawType()))
                                        .map(field -> field.isAnnotationPresent(ProxyField.class) ? field.getName() : null)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toSet()))
                        .contains(destName);
            }
        } catch (Exception e) {
            log.debug("", e);
        }
        return false;
    }

    @Override
    public <D extends String> D filterDestination(D destinationValue, Type<?> sourceType, String sourceName, Type<D> destType, String destName, MappingContext mappingContext) {
        return (D) proxyHelper.decorateStr(destinationValue);
    }

    @Override
    public <S extends String> S filterSource(S sourceValue, Type<S> sourceType, String sourceName, Type<?> destType, String destName, MappingContext mappingContext) {
        return null;
    }

    @PostConstruct
    public void init() {
        enabled = true;
    }

}
