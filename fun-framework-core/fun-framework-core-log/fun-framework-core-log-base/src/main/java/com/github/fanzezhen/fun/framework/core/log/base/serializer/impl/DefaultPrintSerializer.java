package com.github.fanzezhen.fun.framework.core.log.base.serializer.impl;

import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.fun.framework.core.log.base.serializer.IPrintSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认不做处理
 */
@Slf4j
@Order
@Component
public class DefaultPrintSerializer implements IPrintSerializer {

    private final Set<Class<?>> toStringClassSet = ConcurrentHashMap.newKeySet();

    @Override
    public boolean isSupport(Object o) {
        return true;
    }

    /**
     * 优先json转，如果失败则直接toString
     */
    @Override
    public String serialize(Object o) {
        if (o == null) {
            return null;
        }
        if (toStringClassSet.contains(o.getClass())) {
            return o.toString();
        }
        //如果不能用json，就直接toString
        try {
            return JSON.toJSONString(o);
        } catch (Exception e) {
            log.warn("此对象不能用fastJson序列化 ：class {} 对象 {} ", o.getClass(), o);
            toStringClassSet.add(o.getClass());
            return o.toString();
        }
    }

}
