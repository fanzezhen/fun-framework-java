package com.github.fanzezhen.fun.framework.proxy.fastjson;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.filter.ValueFilter;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyField;
import com.github.fanzezhen.fun.framework.proxy.core.ProxyHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.lang.reflect.Field;

/**
 * 静态资源代理序列化过滤器
 *
 * @author fanzezhen
 * @since 2.19.6
 */
@Component
@ConditionalOnBean(ProxyHelper.class)
public class ProxyFastJsonSerializeValueFilter implements ValueFilter {
    @Resource
    private ProxyHelper proxyHelper;

    @Override
    public Object apply(Object object, String name, Object value) {
        if (object == null) {
            return null;
        }
        for (Field field : ReflectUtil.getFields(object.getClass())) {
            if (field.getName().equals(name) && field.isAnnotationPresent(ProxyField.class)) {
                    return proxyHelper.decorateByAnnotation(value);
                }

        }
        return value;
    }
}
