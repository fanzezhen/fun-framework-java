package com.github.fanzezhen.fun.framework.mp.base;

import cn.hutool.core.bean.BeanUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * @author fanzezhen
 */
public interface IService<T, B> extends com.baomidou.mybatisplus.extension.service.IService<T> {
    default B get(String pk) {
        return toBO(getById(pk));
    }

    default B toBO(T entity) {
        return BeanUtil.copyProperties(entity, getBoClass());
    }

    default List<B> toBO(Collection<T> entities) {
        return BeanUtil.copyToList(entities, getBoClass());
    }

    default Class<B> getBoClass(Type... types) {
        return IService.getTypeArgumentClass((short) 1, types);
    }

    @SuppressWarnings("unchecked")
    static <B> Class<B> getTypeArgumentClass(short index, Type... types) {
        if (types != null) {
            for (Type interfaceType : types) {
                if (interfaceType instanceof ParameterizedType parameterizedType && parameterizedType.getRawType() == IService.class) {
                    return (Class<B>) parameterizedType.getActualTypeArguments()[index];
                } else if (interfaceType instanceof Class<?> interfaceTypecClass) {
                    Class<B> c = getTypeArgumentClass(index, interfaceTypecClass.getGenericInterfaces());
                    if (c != null) {
                        return c;
                    }
                }
            }
        }
        return null;
    }
}
