package com.github.fanzezhen.fun.framework.core.model.exception.enums;

import com.github.fanzezhen.fun.framework.core.model.enums.ICodeTextEnum;

/**
 * 异常码接口
 * <p>
 * 继承 ICodeTextEnum，用于定义异常码的统一接口。
 * </p>
 *
 * @param <E> 枚举类型
 */
public interface IExceptionCode<E extends ICodeTextEnum<E>> extends ICodeTextEnum<E> {
}
