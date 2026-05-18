package com.github.fanzezhen.fun.framework.core.model.enums;

/**
 * 编码文本枚举接口
 * <p>
 * 继承 ICodeEnum，额外提供文本说明字段，适用于需要中文描述的枚举场景。
 * </p>
 *
 * @param <E> 枚举类型
 */
public interface ICodeTextEnum<E extends ICodeEnum<E>> extends ICodeEnum<E> {

    /**
     * 获取枚举项文本说明
     * <p>
     * 返回枚举项的中文描述或说明文本，用于前端展示
     * </p>
     *
     * @return 枚举文本说明
     */
    String getText();

    /**
     * 获取枚举项文本
     * <p>
     * 实现 EnumItem 接口的方法，返回枚举的中文说明
     * </p>
     *
     * @return 枚举文本
     */
    @Override
    default String text() {
        return getText();
    }
}
