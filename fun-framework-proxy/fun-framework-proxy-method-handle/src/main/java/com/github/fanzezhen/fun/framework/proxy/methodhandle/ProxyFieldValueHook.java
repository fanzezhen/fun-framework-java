package com.github.fanzezhen.fun.framework.proxy.methodhandle;

import com.github.fanzezhen.fun.framework.core.model.mapper.FieldValueHook;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyField;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MethodHandle 映射引擎的 @ProxyField 字段级脱敏 hook。
 * <p>
 * 等价于 proxy-orika 的 {@code ProxyOrikaFilter}：在对象映射写入目标字段前，
 * 对带有 {@link ProxyField} 注解的 String 字段调用 {@link ProxyHelper#decorateStr}
 * 进行代理装饰（URL 代理、脱敏等）。
 * </p>
 *
 * <p>通过 {@link ConditionalOnBean} 依赖 {@link ProxyHelper} 自动启用；
 * 由框架自动配置收集为 {@code FieldValueHook} 注入 MethodHandle 引擎。</p>
 *
 * @since 4.1.0
 */
@Slf4j
@Component
@ConditionalOnBean(ProxyHelper.class)
public class ProxyFieldValueHook implements FieldValueHook {

    /**
     * 目标类 → 带 @ProxyField 注解的 String 字段名集合缓存。
     * <p>
     * 使用 {@link ConcurrentHashMap} 的 {@code computeIfAbsent} 原子性保证同一类型
     * 只反射扫描一次，避免高并发下重复反射。
     * </p>
     */
    private final Map<Class<?>, Set<String>> cache = new ConcurrentHashMap<>();

    /**
     * 代理助手，用于对字符串进行装饰处理。
     */
    @Resource
    private ProxyHelper proxyHelper;

    @Override
    public boolean supports(final Class<?> destinationClass, final String fieldName, final Class<?> fieldType) {
        if (fieldType != String.class) {
            return false;
        }
        return cache.computeIfAbsent(destinationClass, ProxyFieldValueHook::scanProxyFields).contains(fieldName);
    }

    @Override
    public Object apply(final Object value, final Class<?> destinationClass, final String fieldName) {
        if (value == null) {
            return null;
        }
        return proxyHelper.decorateStr(value.toString());
    }

    /**
     * 扫描类（含父类）中所有带 @ProxyField 注解的字段名。
     *
     * @param clazz 目标类
     *
     * @return 代理字段名集合
     */
    private static Set<String> scanProxyFields(final Class<?> clazz) {
        Set<String> names = new HashSet<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(ProxyField.class)) {
                    names.add(field.getName());
                }
            }
            current = current.getSuperclass();
        }
        return names;
    }
}
