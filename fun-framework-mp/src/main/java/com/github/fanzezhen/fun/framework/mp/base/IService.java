package com.github.fanzezhen.fun.framework.mp.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * @author fanzezhen
 * @createTime 2024/6/6 16:01
 */
public interface IService<T, B> extends com.baomidou.mybatisplus.extension.service.IService<T> {
    default B get(String pk) {
        return toBO(getById(pk));
    }

    B toBO(T entity);

    List<B> toBO(Collection<T> entities);

    @SuppressWarnings("unchecked")
    static <T> Class<T> getBoClass(Type... types) {
        if (types != null) {
            for (Type interfaceType : types) {
                if (interfaceType instanceof ParameterizedType parameterizedType && parameterizedType.getRawType() == IService.class) {
                    return (Class<T>) parameterizedType.getActualTypeArguments()[1];
                } else if (interfaceType instanceof Class<?> interfaceTypecClass){
                    Class<T> c = getBoClass(interfaceTypecClass.getGenericInterfaces());
                    if (c != null) {
                        return c;
                    }
                }
            }
        }
        return null;
    }
}
