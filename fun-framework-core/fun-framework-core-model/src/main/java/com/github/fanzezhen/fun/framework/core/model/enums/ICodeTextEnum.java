package com.github.fanzezhen.fun.framework.core.model.enums;

/**
 * 枚举
 *
 * @author fanzezhen
 */
public interface ICodeTextEnum<E extends ICodeEnum<E>> extends ICodeEnum<E> {

    /**
     * 在中文语境下配合一个中文说明
     */
    String getText();

    /**
     * 在中文语境下配合一个中文说明
     */
    @Override
    default String text() {
        return getText();
    }
}
