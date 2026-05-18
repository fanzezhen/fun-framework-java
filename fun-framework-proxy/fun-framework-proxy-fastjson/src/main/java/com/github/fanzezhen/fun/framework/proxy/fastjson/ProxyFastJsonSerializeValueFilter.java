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
 * <p>
 * 在 FastJson 序列化过程中，对标记了 {@link ProxyField} 注解的字段进行代理处理
 *
 * @since 2.19.6
 */
@Component
@ConditionalOnBean(ProxyHelper.class)
public class ProxyFastJsonSerializeValueFilter implements ValueFilter {
    @Resource
    private ProxyHelper proxyHelper;

    /**
     * 应用值过滤器
     *
     * @param object 所属对象
     * @param name   字段名称
     * @param value  字段值
     * @return 处理后的值
     */
    @Override
    public Object apply(final Object object, final String name, final Object value) {
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
