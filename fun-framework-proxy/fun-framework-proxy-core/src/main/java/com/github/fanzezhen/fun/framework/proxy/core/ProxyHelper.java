package com.github.fanzezhen.fun.framework.proxy.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 静态资源代理修饰器，用于批量替换返回对象中的静态资源URL（如添加CDN前缀）
 * <p>
 * 典型场景：
 * - 将 "/images/logo.png" 替换为 "https://cdn.example.com/images/logo.png"
 * - 将相对路径替换为带时间戳的版本化路径（防止缓存）
 * <p>
 * changed参数用途：用于外部判断对象是否实际发生了修改，避免不必要的数据库更新或缓存失效。
 * 例如：批量更新用户头像URL时，只有真正变化的记录才需要持久化。
 *
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

    /**
     * @param changed 输出参数：修饰完成后，如果对象内容实际发生变化则设为true，否则为false。
     *                用于外部判断是否需要执行后续操作（如保存数据库、清除缓存）。
     */
    public <R> R decorate(R r, AtomicBoolean changed) {
        return proxyDecorator.decorate(() -> r, changed);
    }

    public <R> R decorateByAnnotation(R r) {
        return proxyDecorator.decorate(() -> r, new AtomicBoolean(false), new AtomicBoolean(false));
    }

    /**
     * @param changed 输出参数：修饰完成后，如果对象内容实际发生变化则设为true，否则为false。
     *                用于外部判断是否需要执行后续操作（如保存数据库、清除缓存）。
     */
    public <R> R decorateByAnnotation(R r, AtomicBoolean changed) {
        return proxyDecorator.decorate(() -> r, changed, new AtomicBoolean(false));
    }
}
