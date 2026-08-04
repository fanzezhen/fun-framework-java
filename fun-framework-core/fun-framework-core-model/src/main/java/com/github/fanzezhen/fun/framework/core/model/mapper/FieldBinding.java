package com.github.fanzezhen.fun.framework.core.model.mapper;

import java.lang.invoke.MethodHandle;

/**
 * 单个字段的映射绑定。
 * <p>
 * 持有源对象 getter 与目标对象 setter 的 {@link MethodHandle}，
 * 以及目标字段类型（用于类型转换）和可选的写入 hook。
 * 由 {@link MappingPlan} 在首次映射某对类型时构建并缓存。
 * </p>
 *
 * @since 4.1.0
 */
final class FieldBinding {

    /**
     * 源对象 getter 句柄，签名 {@code (Object)Object}。
     */
    private final MethodHandle getter;

    /**
     * 目标对象 setter 句柄，签名 {@code (Object,Object)void}。
     */
    private final MethodHandle setter;

    /**
     * 目标字段类型，用于类型转换。
     */
    private final Class<?> targetType;

    /**
     * 目标字段名。
     */
    private final String fieldName;

    /**
     * 命中的写入 hook；无 hook 时为 null。
     */
    private final FieldValueHook hook;

    FieldBinding(final MethodHandle getter, final MethodHandle setter,
                 final Class<?> targetType, final String fieldName, final FieldValueHook hook) {
        this.getter = getter;
        this.setter = setter;
        this.targetType = targetType;
        this.fieldName = fieldName;
        this.hook = hook;
    }

    /**
     * 从源对象读取字段值，转换为目标类型（必要时经 hook 装饰）后写入目标对象。
     *
     * @param source           源对象
     * @param destination      目标对象
     * @param destinationClass 目标类型（供 hook 使用）
     *
     * @throws Throwable MethodHandle 调用异常
     */
    void copy(final Object source, final Object destination, final Class<?> destinationClass) throws Throwable {
        Object raw = getter.invoke(source);
        Object converted = TypeConverter.convert(raw, targetType);
        if (hook != null) {
            converted = hook.apply(converted, destinationClass, fieldName);
        }
        if (converted == null && targetType.isPrimitive()) {
            // primitive 字段不接受 null，保留其默认值（与不覆盖 null 源的语义一致）
            return;
        }
        setter.invoke(destination, converted);
    }
}
