package com.github.fanzezhen.fun.framework.proxy.fastjson;

import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * FastJson 代理配置类
 * <p>
 * 自动配置 FastJson2 的代理字段读写器，实现序列化/反序列化时的自动URL转换
 *
 * @since 3.4.3.5
 */
@Configuration
@ConditionalOnBean({ProxyHelper.class})
public class ProxyFastjsonConfig {
    @Resource
    private ProxyHelper proxyHelper;

    /**
     * 初始化 FastJson 代理组件
     */
    @PostConstruct
    public void afterPropertiesSet() {
        ProxyFieldReader.initStatic(true, proxyHelper);
        ProxyFieldWriter.initStatic(true, proxyHelper);
    }
}
