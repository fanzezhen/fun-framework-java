package com.github.fanzezhen.fun.framework.proxy.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 静态资源代理修饰器
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
@Component
@ConditionalOnBean(ProxyDecorator.class)
public class ProxyHelper {
    @Resource
    private ProxyDecorator proxyDecorator;

    public String decorateStr(String s) {
        return proxyDecorator.decorate(s);
    }

    public <R> R decorate(R r) {
        return proxyDecorator.decorate(() -> r, new AtomicBoolean(false));
    }

    public <R> R decorate(R r, AtomicBoolean changed) {
        return proxyDecorator.decorate(() -> r, changed);
    }

    public <R> R decorateByAnnotation(R r) {
        return proxyDecorator.decorate(() -> r, new AtomicBoolean(false), new AtomicBoolean(false));
    }

    public <R> R decorateByAnnotation(R r, AtomicBoolean changed) {
        return proxyDecorator.decorate(() -> r, changed, new AtomicBoolean(false));
    }
}
