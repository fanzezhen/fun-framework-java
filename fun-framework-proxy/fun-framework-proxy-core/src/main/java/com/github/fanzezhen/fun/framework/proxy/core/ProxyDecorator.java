package com.github.fanzezhen.fun.framework.proxy.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 静态资源代理修饰器。
 *
 * @since 3.4.3.5
 */
@SuppressWarnings({"unused", "unchecked"})
@Component
@ConditionalOnProperty(value = "fun.proxy.enabled", havingValue = "true")
public class ProxyDecorator {
    /**
     * 代理配置属性。
     */
    @Resource
    private ProxyProperties proxyProperties;

    /**
     * 装饰字符串，将字符串中的URL地址进行代理转换。
     *
     * @param s 待装饰的字符串
     * @return 装饰后的字符串
     */
    public String decorate(final String s) {
        return replaceStr(s, null);
    }

    /**
     * 装饰对象，递归处理对象中的所有字符串字段。
     *
     * @param supplier 对象供应器
     * @param changed  输出参数，如果对象内容实际发生变化
     *                 则设为true
     * @param <R>      对象类型
     * @return 装饰后的对象
     */
    public <R> R decorate(@NotNull final Supplier<R> supplier,
                          final AtomicBoolean changed) {
        R r = supplier.get();
        return process(r, changed);
    }

    private <R> R process(final R object,
                          final AtomicBoolean atomicBoolean) {
        if (object instanceof List) {
            return (R) processList(
                    (List<?>) object, atomicBoolean);
        } else if (object instanceof Map) {
            return (R) processMap(
                    (Map<?, ?>) object, atomicBoolean);
        } else if (object instanceof String) {
            return (R) replaceStr(
                    (String) object, atomicBoolean);
        } else if (!object.getClass().isPrimitive()) {
            return processObject(object, atomicBoolean);
        }
        return object;
    }

    @SneakyThrows
    private <R> R processObject(@NotNull final R object,
                                 final AtomicBoolean atomicBoolean) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        AtomicBoolean fieldValueChanged = new AtomicBoolean(false);
        for (Field field : fields) {
            fieldValueChanged.set(false);
            Object value =
                    ReflectUtil.getFieldValue(object, field);
            if (value instanceof String strValue) {
                String newValue =
                        replaceStr((String) value, fieldValueChanged);
                if (fieldValueChanged.get()) {
                    ReflectUtil.setFieldValue(
                            object, field, newValue);
                }
            } else if (value instanceof Map) {
                Map<?, ?> newValue = processMap(
                        (Map<?, ?>) value, fieldValueChanged);
                ReflectUtil.setFieldValue(object, field, newValue);
            } else if (value instanceof List) {
                List<?> newValue = processList(
                        (List<?>) value, fieldValueChanged);
                ReflectUtil.setFieldValue(object, field, newValue);
            } else if (value != null
                    && BeanUtil.isBean(value.getClass())) {
                // 对非基本类型递归处理
                Object newValue =
                        process(value, fieldValueChanged);
                if (fieldValueChanged.get()) {
                    ReflectUtil.setFieldValue(
                            object, field, newValue);
                }
            }
            if (fieldValueChanged.get()) {
                atomicBoolean.set(true);
            }
        }
        return object;
    }

    private <T> List<T> processList(final List<T> list,
                                     final AtomicBoolean atomicBoolean) {
        if (CollUtil.isEmpty(list)) {
            return list;
        }
        for (int i = 0; i < list.size(); i++) {
            AtomicBoolean itemChanged = new AtomicBoolean(false);
            T o = list.get(i);
            T replaced = process(o, itemChanged);
            if (itemChanged.get()) {
                list.set(i, replaced);
                atomicBoolean.set(true);
            }
        }
        return list;
    }

    private <K, V> Map<K, V> processMap(@NotNull final Map<K, V> map,
                                         final AtomicBoolean atomicBoolean) {
        AtomicBoolean entryChanged = new AtomicBoolean();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            entryChanged.set(false);
            K key = entry.getKey();
            V value = entry.getValue();
            V replaced = process(value, entryChanged);
            if (entryChanged.get()) {
                map.put(key, replaced);
                atomicBoolean.set(true);
            }
        }
        return map;
    }

    /**
     * 装饰对象，仅处理带有 {@link ProxyField} 注解的字段。
     *
     * @param supplier     对象供应器
     * @param changed      输出参数，如果对象内容实际发生变化
     *                     则设为true
     * @param isAnnotation 标记是否进入了注解字段的处理分支
     * @param <R>          对象类型
     * @return 装饰后的对象
     */
    public <R> R decorate(@NotNull final Supplier<R> supplier,
                          final AtomicBoolean changed,
                          final AtomicBoolean isAnnotation) {
        R r = supplier.get();
        return process(r, changed, isAnnotation);
    }

    private <R> R process(final R object,
                          final AtomicBoolean atomicBoolean,
                          final AtomicBoolean isAnnotation) {
        if (object instanceof List) {
            return (R) processList(
                    (List<?>) object, atomicBoolean, isAnnotation);
        } else if (object instanceof Map) {
            return (R) processMap(
                    (Map<?, ?>) object, atomicBoolean, isAnnotation);
        } else if (object instanceof String) {
            return (R) replaceStr(
                    (String) object, atomicBoolean);
        } else if (!object.getClass().isPrimitive()) {
            return processObject(
                    object, atomicBoolean, isAnnotation);
        }
        return object;
    }

    @SneakyThrows
    private <R> R processObject(@NotNull final R object,
                                 final AtomicBoolean atomicBoolean,
                                 final AtomicBoolean isAnnotation) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        AtomicBoolean fieldValueChanged = new AtomicBoolean(false);
        for (Field field : fields) {
            fieldValueChanged.set(false);
            if (!isAnnotation.get()
                    && field.isAnnotationPresent(ProxyField.class)) {
                isAnnotation.set(true);
            }
            Object value =
                    ReflectUtil.getFieldValue(object, field);
            if (value instanceof String strValue) {
                if (isAnnotation.get()) {
                    String newValue =
                            replaceStr(strValue, fieldValueChanged);
                    if (fieldValueChanged.get()) {
                        ReflectUtil.setFieldValue(
                                object, field, newValue);
                    }
                }
            } else if (value instanceof Map) {
                Map<?, ?> newValue = processMap(
                        (Map<?, ?>) value,
                        fieldValueChanged, isAnnotation);
                ReflectUtil.setFieldValue(
                        object, field, newValue);
            } else if (value instanceof List) {
                List<?> newValue = processList(
                        (List<?>) value,
                        fieldValueChanged, isAnnotation);
                ReflectUtil.setFieldValue(
                        object, field, newValue);
            } else if (value != null
                    && BeanUtil.isBean(value.getClass())) {
                // 对非基本类型递归处理
                Object newValue = process(
                        value, fieldValueChanged, isAnnotation);
                if (fieldValueChanged.get()) {
                    ReflectUtil.setFieldValue(
                            object, field, newValue);
                }
            }
            if (fieldValueChanged.get()) {
                atomicBoolean.set(true);
            }
        }
        return object;
    }

    private <T> List<T> processList(final List<T> list,
                                     final AtomicBoolean atomicBoolean,
                                     final AtomicBoolean isAnnotation) {
        if (CollUtil.isEmpty(list)) {
            return list;
        }
        for (int i = 0; i < list.size(); i++) {
            AtomicBoolean itemChanged = new AtomicBoolean(false);
            T o = list.get(i);
            T replaced = process(o, itemChanged, isAnnotation);
            if (itemChanged.get()) {
                list.set(i, replaced);
                atomicBoolean.set(true);
            }
        }
        return list;
    }

    private <K, V> Map<K, V> processMap(
            @NotNull final Map<K, V> map,
            final AtomicBoolean atomicBoolean,
            final AtomicBoolean isAnnotation) {
        AtomicBoolean entryChanged = new AtomicBoolean();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            entryChanged.set(false);
            K key = entry.getKey();
            V value = entry.getValue();
            V replaced = process(value, entryChanged, isAnnotation);
            if (entryChanged.get()) {
                map.put(key, replaced);
                atomicBoolean.set(true);
            }
        }
        return map;
    }

    /**
     * 替换链接。
     *
     * @param value   需要替换的链接
     * @param changed 是否发生变更
     * @return 替换后的链接
     */
    private String replaceStr(final String value,
                              final AtomicBoolean changed) {
        String result = value;
        for (ProxyProperties.Address address :
                proxyProperties.getAddressList()) {
            for (Pattern pattern : address.getPatterns()) {
                boolean currentChanged = false;
                Matcher matcher = pattern.matcher(result);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String replacement = proxyProperties.getApi()
                            + matcher.group(2) + "?url="
                            + matcher.group();
                    matcher.appendReplacement(sb, replacement);
                    currentChanged = true;
                }
                matcher.appendTail(sb);
                if (currentChanged) {
                    result = sb.toString();
                    if (changed != null) {
                        changed.set(true);
                    }
                }
            }
        }
        return result;
    }
}
