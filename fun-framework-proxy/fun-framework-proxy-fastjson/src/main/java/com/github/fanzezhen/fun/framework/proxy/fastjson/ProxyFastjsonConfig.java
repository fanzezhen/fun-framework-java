package com.github.fanzezhen.fun.framework.proxy.fastjson;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * json字段解析器配置
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
@Configuration
@ConditionalOnBean({ProxyHelper.class})
public class ProxyFastjsonConfig {
    @Resource
    private ProxyHelper proxyHelper;

    @PostConstruct
    public void afterPropertiesSet() {
        ProxyFieldDeserializer.initStatic(true, proxyHelper);
        ProxyFieldSerializer.initStatic(true, proxyHelper);
    }
}
