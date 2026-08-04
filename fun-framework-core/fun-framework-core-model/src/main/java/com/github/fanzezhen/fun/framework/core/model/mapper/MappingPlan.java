package com.github.fanzezhen.fun.framework.core.model.mapper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一对类型 (源类 → 目标类) 的映射计划。
 * <p>
 * 通过 JavaBean 内省匹配同名可读/可写属性，构建 {@link FieldBinding} 列表。
 * 仅使用 public getter/setter 的 {@link MethodHandle}，不访问 JDK 内部字段，
 * 因此无需 {@code --add-opens}（这是相对 Orika 反射的核心优势）。
 * </p>
 * <p>
 * 句柄解析统一走 {@link #lookupFor}：优先用 {@link MethodHandles#privateLookupIn} 取得目标类所在
 * 包的访问权（覆盖包私有 DTO、包私有嵌套类等常见场景），而非 {@code setAccessible(true)} 破坏访问检查；
 * 目标包未 open（如 JDK 内部类型）时降级为公共 Lookup，仅能解析 public 成员。
 * </p>
 *
 * @since 4.1.0
 */
final class MappingPlan {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /**
     * 目标类型（用于构造实例与传递给 hook）。
     */
    private final Class<?> destinationClass;

    /**
     * 目标类无参构造句柄。
     */
    private final MethodHandle constructor;

    /**
     * 字段绑定列表。
     */
    private final List<FieldBinding> bindings;

    private MappingPlan(final Class<?> destinationClass, final MethodHandle constructor,
                        final List<FieldBinding> bindings) {
        this.destinationClass = destinationClass;
        this.constructor = constructor;
        this.bindings = bindings;
    }

    /**
     * 构建 (sourceClass → destinationClass) 的映射计划。
     *
     * @param sourceClass      源类型
     * @param destinationClass 目标类型
     * @param hooks            字段写入 hook 列表（可空）
     *
     * @return 映射计划
     */
    static MappingPlan build(final Class<?> sourceClass, final Class<?> destinationClass,
                             final List<FieldValueHook> hooks) {
        try {
            Map<String, Method> readers = readableProperties(sourceClass);
            Constructor<?> ctorRef = destinationClass.getDeclaredConstructor();
            MethodHandle ctor = lookupFor(destinationClass).unreflectConstructor(ctorRef);
            List<FieldBinding> bindings = new ArrayList<>();
            for (PropertyDescriptor pd : beanInfo(destinationClass).getPropertyDescriptors()) {
                Method writer = pd.getWriteMethod();
                Method reader = readers.get(pd.getName());
                if (writer == null || reader == null) {
                    continue;
                }
                Class<?> targetType = pd.getPropertyType();
                FieldValueHook hook = resolveHook(hooks, destinationClass, pd.getName(), targetType);
                bindings.add(new FieldBinding(
                        unreflect(reader),
                        unreflect(writer),
                        targetType,
                        pd.getName(),
                        hook));
            }
            return new MappingPlan(destinationClass, ctor, bindings);
        } catch (ReflectiveOperationException | IntrospectionException e) {
            throw new IllegalStateException(
                    "构建映射计划失败: " + sourceClass.getName() + " -> " + destinationClass.getName(), e);
        }
    }

    /**
     * 执行映射，创建目标实例并逐字段拷贝。
     *
     * @param source 源对象
     * @param <D>    目标类型
     *
     * @return 目标实例
     */
    @SuppressWarnings("unchecked")
    <D> D map(final Object source) {
        try {
            Object destination = constructor.invoke();
            for (FieldBinding binding : bindings) {
                binding.copy(source, destination, destinationClass);
            }
            return (D) destination;
        } catch (Throwable e) {
            throw new IllegalStateException("对象映射失败: " + destinationClass.getName(), e);
        }
    }

    /**
     * 将反射方法转为句柄，Lookup 取自方法的声明类（属性可能继承自父类，故不能统一用目标类）。
     *
     * @param method getter 或 setter
     *
     * @return 方法句柄
     *
     * @throws IllegalAccessException 声明类所在包未开放且方法非 public
     */
    private static MethodHandle unreflect(final Method method) throws IllegalAccessException {
        return lookupFor(method.getDeclaringClass()).unreflect(method);
    }

    /**
     * 获取可访问指定类成员的 Lookup。
     * <p>
     * 优先 {@link MethodHandles#privateLookupIn}，使包私有 DTO / 包私有嵌套类无需
     * {@code setAccessible} 即可解析；目标类所在包未向本模块开放时降级为公共 Lookup。
     * </p>
     *
     * @param clazz 目标类
     *
     * @return 对应 Lookup
     */
    private static MethodHandles.Lookup lookupFor(final Class<?> clazz) {
        try {
            return MethodHandles.privateLookupIn(clazz, LOOKUP);
        } catch (IllegalAccessException e) {
            return LOOKUP;
        }
    }

    private static Map<String, Method> readableProperties(final Class<?> clazz) throws IntrospectionException {
        Map<String, Method> readers = new HashMap<>();
        for (PropertyDescriptor pd : beanInfo(clazz).getPropertyDescriptors()) {
            if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                readers.put(pd.getName(), pd.getReadMethod());
            }
        }
        return readers;
    }

    private static BeanInfo beanInfo(final Class<?> clazz) throws IntrospectionException {
        return Introspector.getBeanInfo(clazz, Object.class);
    }

    private static FieldValueHook resolveHook(final List<FieldValueHook> hooks, final Class<?> destinationClass,
                                              final String fieldName, final Class<?> fieldType) {
        if (hooks == null || hooks.isEmpty()) {
            return null;
        }
        for (FieldValueHook hook : hooks) {
            if (hook.supports(destinationClass, fieldName, fieldType)) {
                return hook;
            }
        }
        return null;
    }
}
