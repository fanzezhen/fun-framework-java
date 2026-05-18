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
 * Orika字符串代理转换器
 * <p>
 * 在Orika对象映射过程中自动对字符串字段进行代理处理（如脱敏、加密等）。
 * 仅在fun.proxy.enabled=true且存在ProxyHelper时生效。
 *
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
     * 执行字符串到字符串的转换，自动进行URL代理处理
     *
     * @param source          源字符串对象
     * @param destinationType 目标类型
     * @param mappingContext  映射上下文
     * @return 转换后的字符串（已进行URL代理处理）
     */
    @Override
    public String convert(final String source,
                          final Type<? extends String> destinationType,
                          final MappingContext mappingContext) {
        return enabled ? proxyHelper.decorateStr(source) : source;
    }
}
