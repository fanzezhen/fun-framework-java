package com.github.fanzezhen.fun.framework.proxy.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 静态资源代理修饰器，用于批量替换返回对象中的静态资源URL。
 * <p>
 * 典型场景：
 * - 将 "/images/logo.png" 替换为
 * "https://cdn.example.com/images/logo.png"
 * - 将相对路径替换为带时间戳的版本化路径（防止缓存）
 * <p>
 * changed参数用途：用于外部判断对象是否实际发生了修改，
 * 避免不必要的数据库更新或缓存失效。
 * 例如：批量更新用户头像URL时，只有真正变化的记录才需要持久化。
 *
 * @since 3.4.3.5
 */
@Component
@ConditionalOnBean(ProxyDecorator.class)
public class ProxyHelper {
    /**
     * 代理装饰器实例。
     */
    @Resource
    private ProxyDecorator proxyDecorator;

    /**
     * 装饰字符串，将字符串中的URL地址进行代理转换。
     *
     * @param s 待装饰的字符串
     * @return 装饰后的字符串
     */
    public String decorateStr(final String s) {
        return proxyDecorator.decorate(s);
    }

    /**
     * 装饰对象，递归处理对象中的所有字符串字段。
     *
     * @param r   待装饰的对象
     * @param <R> 对象类型
     * @return 装饰后的对象
     */
    public <R> R decorate(final R r) {
        return proxyDecorator.decorate(
                () -> r, new AtomicBoolean(false));
    }

    /**
     * 装饰对象，递归处理对象中的所有字符串字段。
     *
     * @param r       待装饰的对象
     * @param changed 输出参数：修饰完成后，
     *                如果对象内容实际发生变化则设为true，
     *                否则为false。用于外部判断是否需要执行后续操作
     *                （如保存数据库、清除缓存）。
     * @param <R>     对象类型
     * @return 装饰后的对象
     */
    public <R> R decorate(final R r,
                          final AtomicBoolean changed) {
        return proxyDecorator.decorate(() -> r, changed);
    }

    /**
     * 装饰对象，仅处理带有 {@link ProxyField} 注解的字段。
     *
     * @param r   待装饰的对象
     * @param <R> 对象类型
     * @return 装饰后的对象
     */
    public <R> R decorateByAnnotation(final R r) {
        return proxyDecorator.decorate(() -> r,
                new AtomicBoolean(false),
                new AtomicBoolean(false));
    }

    /**
     * 装饰对象，仅处理带有 {@link ProxyField} 注解的字段。
     *
     * @param r       待装饰的对象
     * @param changed 输出参数：修饰完成后，
     *                如果对象内容实际发生变化则设为true，
     *                否则为false。用于外部判断是否需要执行后续操作
     *                （如保存数据库、清除缓存）。
     * @param <R>     对象类型
     * @return 装饰后的对象
     */
    public <R> R decorateByAnnotation(final R r,
                                      final AtomicBoolean changed) {
        return proxyDecorator.decorate(() -> r, changed,
                new AtomicBoolean(false));
    }
}
