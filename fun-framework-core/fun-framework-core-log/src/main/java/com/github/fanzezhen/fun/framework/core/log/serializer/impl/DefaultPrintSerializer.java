package com.github.fanzezhen.fun.framework.core.log.serializer.impl;

import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.fun.framework.core.log.serializer.IPrintSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用 JSON 的默认打印序列化器，并回退到 toString.
 * <p>
 * 尝试使用 FastJSON 序列化对象，如果 JSON 失败则回退到 toString。
 * 缓存无法进行 JSON 序列化的类以避免重复尝试。
 */
@Slf4j
@Order
@Component
public class DefaultPrintSerializer implements IPrintSerializer {

    /**
     * 需要使用 toString 而不是 JSON 序列化的类集合.
     */
    private final Set<Class<?>> toStringClassSet = ConcurrentHashMap.newKeySet();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupport(final Object o) {
        return true;
    }

    /**
     * 将对象序列化为 JSON 字符串，并在失败时回退到 toString.
     * <p>
     * 首先尝试 JSON 序列化；如果失败，则使用 toString 并缓存该类，
     * 以跳过对同一类型未来实例的 JSON 尝试。
     *
     * @param o 要序列化的对象
     * @return 序列化后的字符串表示
     */
    @Override
    public String serialize(final Object o) {
        if (o == null) {
            return null;
        }
        if (toStringClassSet.contains(o.getClass())) {
            return o.toString();
        }
        // Try JSON first, fall back to toString if it fails
        try {
            return JSON.toJSONString(o);
        } catch (Exception e) {
            log.warn("此对象不能用fastJson序列化 ：class {} 对象 {} ", o.getClass(), o);
            toStringClassSet.add(o.getClass());
            return o.toString();
        }
    }

}
