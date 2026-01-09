package com.github.fanzezhen.fun.framework.core.model.enums;

import cn.hutool.core.lang.EnumItem;

public interface ICodeEnum<E extends EnumItem<E>> extends EnumItem<E> {

    /**
     * 枚举项编号
     */
    Integer getCode();

    /**
     * 枚举项编号
     */
    @Override
    default int intVal() {
        return getCode();
    }
}
