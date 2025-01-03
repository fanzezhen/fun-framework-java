package com.github.fanzezhen.fun.framework.mp.base;

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

}
