package com.github.fanzezhen.fun.framework.mp.base;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import jakarta.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

/**
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
    public void init() {
        boClass = getBoClass(this.getClass().getGenericInterfaces());
    }

}
