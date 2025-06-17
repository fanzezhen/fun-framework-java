package com.github.fanzezhen.fun.framework.proxy.orika;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.Map;

/**
 * orika字段解析器配置
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
@Configuration
@ConditionalOnBean({ProxyHelper.class})
@ConditionalOnProperty(value = "fun.proxy.orika.enabled", havingValue = "true")
public class ProxyOrikaConfig {
    @Resource
    private MapperFactory mapperFactory;
    @Autowired(required = false)
    private ProxyOrikaFilter proxyOrikaFilter;
    @Autowired(required = false)
    private Map<String, Converter<?, ?>> converterMap;
    @Autowired(required = false)
    private Map<String, CustomMapper<?, ?>> mapperMap;

    @PostConstruct
    public void init() {
        if (proxyOrikaFilter != null) {
            mapperFactory.registerFilter(proxyOrikaFilter);
        }
        if (mapperMap != null) {
            mapperMap.values().forEach(mapperFactory::registerMapper);
        }
        if (converterMap != null) {
            converterMap.forEach(mapperFactory.getConverterFactory()::registerConverter);
        }
    }
}
