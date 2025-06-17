package com.github.fanzezhen.fun.framework.proxy.mybatis;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;

/**
 * mybatis字段解析器配置
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
@Configuration
@ConditionalOnBean({ProxyHelper.class})
public class ProxyJdbcConfig implements InitializingBean {

    @Resource
    private ProxyHelper proxyHelper;

    @Override
    public void afterPropertiesSet() throws Exception {
        ProxyFieldStringTypeHandler.initStatic(true, proxyHelper);
    }
}
