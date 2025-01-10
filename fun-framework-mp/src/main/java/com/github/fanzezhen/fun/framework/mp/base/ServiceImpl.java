package com.github.fanzezhen.fun.framework.mp.base;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * @author fanzezhen
 * @createTime 2024/6/6 16:02
 */
public class ServiceImpl<M extends BaseMapper<T>, T, B> extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<M, T> implements IService<T, B> {
    protected Class<B> boClass;

    @Override
    public B toBO(T entity) {
        return BeanUtil.copyProperties(entity, boClass);
    }

    @Override
    public List<B> toBO(Collection<T> entities) {
        return BeanUtil.copyToList(entities, boClass);
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        for (Type interfaceType : this.getClass().getGenericInterfaces()) {
            if (interfaceType instanceof ParameterizedType parameterizedType && parameterizedType.getRawType() == IService.class) {
                boClass = (Class<B>) parameterizedType.getActualTypeArguments()[1];
                break;
            }
        }
    }
}
