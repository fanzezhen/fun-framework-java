package com.github.fanzezhen.fun.framework.core.model.enums;

import cn.hutool.core.lang.EnumItem;

/**
 * 编码枚举接口
 * <p>
 * 定义带编码的枚举基本结构，所有枚举类应实现此接口以提供编码值。
 * </p>
 *
 * @param <E> 枚举类型
 */
public interface ICodeEnum<E extends EnumItem<E>> extends EnumItem<E> {

    /**
     * 获取枚举项编码
     * <p>
     * 返回枚举的唯一标识码，用于数据库存储和前后端交互
     * </p>
     *
     * @return 枚举编码
     */
    Integer getCode();

    /**
     * 获取枚举项整数值
     * <p>
     * 实现 EnumItem 接口的方法，返回枚举编码的整数值
     * </p>
     *
     * @return 枚举整数值
     */
    @Override
    default int intVal() {
        return getCode();
    }
}
