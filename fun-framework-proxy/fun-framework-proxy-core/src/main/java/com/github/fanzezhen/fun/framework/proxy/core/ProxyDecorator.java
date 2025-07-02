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
 * 静态资源代理修饰器
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
@SuppressWarnings({"unused", "unchecked"})
@Component
@ConditionalOnProperty(value = "fun.proxy.enabled", havingValue = "true")
public class ProxyDecorator {
    @Resource
    private ProxyProperties proxyProperties;

    public String decorate(String s) {
        return replaceStr(s, null);
    }

    public <R> R decorate(@NotNull Supplier<R> supplier, AtomicBoolean changed) {
        R r = supplier.get();
        return process(r, changed);
    }

    private <R> R process(R object, AtomicBoolean atomicBoolean) {
        if (object instanceof List) {
            return (R) processList((List<?>) object, atomicBoolean);
        } else if (object instanceof Map) {
            return (R) processMap((Map<?, ?>) object, atomicBoolean);
        } else if (object instanceof String) {
            return (R) replaceStr((String) object, atomicBoolean);
        } else if (!object.getClass().isPrimitive()) {
            return processObject(object, atomicBoolean);
        }
        return object;
    }

    @SneakyThrows
    private <R> R processObject(@NotNull R object, AtomicBoolean atomicBoolean) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        AtomicBoolean fieldValueChanged = new AtomicBoolean(false);
        for (Field field : fields) {
            fieldValueChanged.set(false);
            Object value = ReflectUtil.getFieldValue(object, field);
            if (value instanceof String strValue) {
                String newValue = replaceStr((String) value, fieldValueChanged);
                if (fieldValueChanged.get()) {
                    ReflectUtil.setFieldValue(object, field, newValue);
                }
            } else if (value instanceof Map) {
                Map<?, ?> newValue = processMap((Map<?, ?>) value, fieldValueChanged);
                ReflectUtil.setFieldValue(object, field, newValue);
            } else if (value instanceof List) {
                List<?> newValue = processList((List<?>) value, fieldValueChanged);
                ReflectUtil.setFieldValue(object, field, newValue);
            } else if (value != null && BeanUtil.isBean(value.getClass())) {
                // 对非基本类型递归处理
                Object newValue = process(value, fieldValueChanged);
                if (fieldValueChanged.get()) {
                    ReflectUtil.setFieldValue(object, field, newValue);
                }
            }
            if (fieldValueChanged.get()) {
                atomicBoolean.set(true);
            }
        }
        return object;
    }

    private <T> List<T> processList(List<T> list, AtomicBoolean atomicBoolean) {
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

    private <K, V> Map<K, V> processMap(@NotNull Map<K, V> map, AtomicBoolean atomicBoolean) {
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

    public <R> R decorate(@NotNull Supplier<R> supplier, AtomicBoolean changed, AtomicBoolean isAnnotation) {
        R r = supplier.get();
        return process(r, changed, isAnnotation);
    }

    private <R> R process(R object, AtomicBoolean atomicBoolean, AtomicBoolean isAnnotation) {
        if (object instanceof List) {
            return (R) processList((List<?>) object, atomicBoolean, isAnnotation);
        } else if (object instanceof Map) {
            return (R) processMap((Map<?, ?>) object, atomicBoolean, isAnnotation);
        } else if (object instanceof String) {
            return (R) replaceStr((String) object, atomicBoolean);
        } else if (!object.getClass().isPrimitive()) {
            return processObject(object, atomicBoolean, isAnnotation);
        }
        return object;
    }

    @SneakyThrows
    private <R> R processObject(@NotNull R object, AtomicBoolean atomicBoolean, AtomicBoolean isAnnotation) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        AtomicBoolean fieldValueChanged = new AtomicBoolean(false);
        for (Field field : fields) {
            fieldValueChanged.set(false);
            if (!isAnnotation.get() && field.isAnnotationPresent(ProxyField.class)) {
                isAnnotation.set(true);
            }
            Object value = ReflectUtil.getFieldValue(object, field);
            if (value instanceof String strValue) {
                if (isAnnotation.get()) {
                    String newValue = replaceStr(strValue, fieldValueChanged);
                    if (fieldValueChanged.get()) {
                        ReflectUtil.setFieldValue(object, field, newValue);
                    }
                }
            } else if (value instanceof Map mapValue) {
                Map<?, ?> newValue = processMap((Map<?, ?>) value, fieldValueChanged, isAnnotation);
                ReflectUtil.setFieldValue(object, field, newValue);
            } else if (value instanceof List) {
                List<?> newValue = processList((List<?>) value, fieldValueChanged, isAnnotation);
                ReflectUtil.setFieldValue(object, field, newValue);
            } else if (value != null && BeanUtil.isBean(value.getClass())) {
                // 对非基本类型递归处理
                Object newValue = process(value, fieldValueChanged, isAnnotation);
                if (fieldValueChanged.get()) {
                    ReflectUtil.setFieldValue(object, field, newValue);
                }
            }
            if (fieldValueChanged.get()) {
                atomicBoolean.set(true);
            }
        }
        return object;
    }

    private <T> List<T> processList(List<T> list, AtomicBoolean atomicBoolean, AtomicBoolean isAnnotation) {
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

    private <K, V> Map<K, V> processMap(@NotNull Map<K, V> map, AtomicBoolean atomicBoolean, AtomicBoolean isAnnotation) {
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
     * 替换链接
     *
     * @param value   需要替换的链接
     * @param changed 是否发生变更
     *
     * @return 替换后的链接
     */
    private String replaceStr(String value, AtomicBoolean changed) {
        for (ProxyProperties.Address address : proxyProperties.getAddressList()) {
            for (Pattern pattern : address.getPatterns()) {
                boolean currentChanged = false;
                Matcher matcher = pattern.matcher(value);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String replacement = proxyProperties.getApi() + matcher.group(2) + "?url=" + matcher.group();
                    matcher.appendReplacement(sb, replacement);
                    currentChanged = true;
                }
                matcher.appendTail(sb);
                if (currentChanged) {
                    value = sb.toString();
                    if (changed != null) {
                        changed.set(true);
                    }
                }
            }
        }
        return value;
    }
}
