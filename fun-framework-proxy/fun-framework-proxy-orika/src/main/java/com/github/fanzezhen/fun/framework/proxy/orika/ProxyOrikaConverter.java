package com.github.fanzezhen.fun.framework.proxy.orika;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;

/**
 * Orika转换器
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
@Configuration
@ConditionalOnBean({ProxyHelper.class})
public class ProxyOrikaConverter extends CustomConverter<String, String> {
    @Value("${fun.proxy.enabled:false}")
    private boolean enabled;
    @Resource
    protected ProxyHelper proxyHelper;

    /**
     * Perform the conversion of <code>source</code> into a new instance of
     * <code>destinationType</code>.
     *
     * @param source          the source object to be converted
     * @param destinationType the destination type to produce
     * @param mappingContext  since converters now have access to <code>MapperFacade</code>,
     *                        they have to pass mapping context
     *
     * @return a new instance of <code>destinationType</code>
     */
    @Override
    public String convert(String source, Type<? extends String> destinationType, MappingContext mappingContext) {
        return enabled ? proxyHelper.decorateStr(source) : source;
    }
}
