package com.github.fanzezhen.fun.framework.core.model.mapper;

/**
 * 字段值写入 hook 扩展点。
 * <p>
 * 在 MethodHandle 引擎将转换后的值写入目标字段前回调，用于脱敏、URL 代理等装饰处理。
 * 等价于 Orika 的 {@code CustomFilter.filterDestination} 机制。
 * </p>
 *
 * <p>典型实现由 {@code fun-framework-proxy-method-handle} 模块提供，针对
 * {@code @ProxyField} 注解字段进行装饰。</p>
 *
 * @since 4.1.0
 */
public interface FieldValueHook {

    /**
     * 判断目标字段是否需要 hook 处理。
     * <p>
     * 结果应可缓存（对同一 (目标类, 字段名) 稳定），实现方无需自行缓存，
     * 引擎会按字段绑定缓存该判定。
     * </p>
     *
     * @param destinationClass 目标类型
     * @param fieldName        目标字段名
     * @param fieldType        目标字段类型
     *
     * @return true 表示写入该字段时调用 {@link #apply}
     */
    boolean supports(Class<?> destinationClass, String fieldName, Class<?> fieldType);

    /**
     * 对即将写入目标字段的值进行装饰处理。
     *
     * @param value            转换后待写入的值
     * @param destinationClass 目标类型
     * @param fieldName        目标字段名
     *
     * @return 装饰后的值
     */
    Object apply(Object value, Class<?> destinationClass, String fieldName);
}
