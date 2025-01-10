package com.github.fanzezhen.fun.framework.core.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.fanzezhen.fun.framework.core.model.ClassInfoBean;
import com.github.fanzezhen.fun.framework.core.strategy.AnnotationFieldServiceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zezhen.fan
 */
@Slf4j
@SuppressWarnings({"unchecked", "unused"})
public class BeanCopyUtil {
    private BeanCopyUtil() {
    }

    public static <T> T copy(Object origin, Class<T> targetClass) {
        if (origin == null || targetClass == null) {
            return null;
        }
        return copy(origin, ReflectUtil.newInstance(targetClass));
    }

    public static <T> T copy(Object origin, T target) {
        if (origin == null || target == null) {
            return null;
        }
        Class<?> originClass = origin.getClass();
        Class<?> targetClass = target.getClass();
        ClassInfoBean originClassInfo = SingletonUtil.getClassInfo(originClass);
        ClassInfoBean targetClassInfo = SingletonUtil.getClassInfo(targetClass);
        Map<Object, Object[]> getterAnnotationMap = originClassInfo.getGetterAnnotationsMap();
        Map<Object, Object[]> setterAnnotationMap = targetClassInfo.getSetterAnnotationsMap();
        if (getterAnnotationMap.isEmpty() && setterAnnotationMap.isEmpty()) {
                BeanUtils.copyProperties(origin, target);
            return target;
        }
        Map<String, Object[]> getterMap = originClassInfo.getFieldNameToGetterMap();
        Map<String, Object[]> setterMap = targetClassInfo.getFieldNameToSetterMap();
        getterMap.forEach((fieldName, getters) ->
                copyField(origin, target, getterAnnotationMap, setterAnnotationMap, setterMap, fieldName, getters));
        return target;
    }

    private static <T> void copyField(Object origin,
                                      T target,
                                      Map<Object, Object[]> getterAnnotationMap,
                                      Map<Object, Object[]> setterAnnotationMap,
                                      Map<String, Object[]> setterMap,
                                      String fieldName,
                                      Object[] getters) {
        Object[] setters = setterMap.get(fieldName);
        if (setters == null) {
            return;
        }
        LinkedHashMap<Object, Object> mapping = mapping(getters, setters);
        mapping.forEach((setter, getter) -> {
            try {
                Object value = null;
                if (getter instanceof Field originField) {
                    value = ReflectUtil.getFieldValue(origin, originField);
                } else if (getter instanceof Method orignMethod) {
                    value = orignMethod.invoke(origin);
                }
                if (value == null) {
                    return;
                }
                if (setter instanceof Method targetItemMethod) {
                    targetItemMethod.invoke(target, value);
                } else if (setter instanceof Field targetItemField) {
                    ReflectUtil.setFieldValue(target, targetItemField, value);
                }
                Map<Class<? extends Annotation>, Annotation> annotationMap = Stream.of(
                                getterAnnotationMap.getOrDefault(getter, ArrayUtils.EMPTY_OBJECT_ARRAY),
                                setterAnnotationMap.getOrDefault(setter, ArrayUtils.EMPTY_OBJECT_ARRAY))
                        .flatMap(objects -> {
                            Stream.Builder<Annotation> builder = Stream.builder();
                            for (Object object : objects) {
                                builder.add((Annotation) object);
                            }
                            return builder.build();
                        })
                        .collect(Collectors.toMap(Annotation::annotationType, Function.identity()));
                Object finalValue = value;
                annotationMap.forEach((annotationClass, annotation) ->
                        AnnotationFieldServiceStrategy.setAnnotationFieldValue(target, finalValue, annotation, fieldName, setterMap)
                );
            } catch (Exception exception) {
                log.warn("copy forEach exception {} ", fieldName, exception);
            }
        });
    }

    private static LinkedHashMap<Object, Object> mapping(Object[] getters, Object[] setters) {
        return Arrays.stream(setters)
                .collect(LinkedHashMap::new, (map, setter) -> {
                    Class<?> parameterType;
                    if (setter instanceof Field setterField) {
                        parameterType = setterField.getType();
                    } else if (setter instanceof Method setterMethod) {
                        parameterType = setterMethod.getParameterTypes()[0];
                    } else {
                        map.putIfAbsent(setter, null);
                        return;
                    }
                    map.put(setter, getMappingGetter(getters, parameterType));
                }, LinkedHashMap::putAll);
    }

    private static Object getMappingGetter(Object[] getters, Class<?> requiredClass) {
        Object mappingGetter = getMappingGetter(getters, requiredClass, true);
        if (mappingGetter != null) {
            return mappingGetter;
        }
        return getMappingGetter(getters, requiredClass, false);
    }

    private static Object getMappingGetter(Object[] getters, Class<?> requiredClass, boolean isStrongVerify) {
        for (Object getter : getters) {
            if (getter instanceof Field getterField) {
                if (isMatched(requiredClass, getterField.getType(), isStrongVerify)) {
                    return getterField;
                }
            } else if (getter instanceof Method getterMethod) {
                if (isMatched(requiredClass, getterMethod.getReturnType(), isStrongVerify)) {
                    return getterMethod;
                }
            }
        }
        return null;
    }

    public static boolean isMatched(Class<?> requiredClass, Class<?> providedClass, boolean isStrongVerify) {
        return isStrongVerify ? Objects.equals(requiredClass, providedClass) : requiredClass.isAssignableFrom(providedClass);
    }

    public static void setValue(Object bean, Object value, Object[] entryFieldSets) throws InvocationTargetException, IllegalAccessException {
        if (bean != null && value != null && entryFieldSets != null) {
            for (Object entryFieldSet : entryFieldSets) {
                if (entryFieldSet instanceof Method targetItemMethod) {
                    if (targetItemMethod.getParameterTypes()[0].isAssignableFrom(value.getClass())) {
                        targetItemMethod.invoke(bean, value);
                    }
                } else if (entryFieldSet instanceof Field targetItemField) {
                    ReflectUtil.setFieldValue(bean, targetItemField, value);
                }
            }
        }
    }
    /**
     * 填充对象
     *
     * @param obj  待填充的对象
     * @param from 数据来源对象
     * @param maps 映射字段名，如果存在下划线会自动转驼峰
     */
    public static <T> T fillByFieldName(T obj, Object from, Pair<String, String>... maps) {
        if (from == null || obj == null) {
            return obj;
        }
        for (Pair<String, String> map : maps) {
            String fieldName = CharSequenceUtil.toCamelCase(map.getKey());
            Field field = ReflectUtil.getField(obj.getClass(), fieldName);
            if (field != null) {
                Object originFieldValue = getFieldValue(from, map.getValue());
                if (originFieldValue instanceof Date dateValue&& field.getType().equals(String.class)) {
                    if (fieldName.endsWith("Date")) {
                        originFieldValue = DateUtil.formatDate(dateValue);
                    } else {
                        originFieldValue = DateUtil.formatDateTime(dateValue);
                    }
                }
                ReflectUtil.setFieldValue(obj, fieldName, originFieldValue);
            }
        }
        return obj;
    }

    /**
     * 获取字段值
     */
    public static Object getFieldValue(Object instance, String fieldName) {
        if (fieldName != null) {
            if (instance instanceof Map) {
                Map<String, Object> instanceObj = ((Map<String, Object>) instance);
                return instanceObj.get(fieldName);
            }
            return ReflectUtil.getFieldValue(instance, fieldName);
        }
        return null;
    }

    /**
     * 获取字段值
     */
    public static Object getFieldValue(Object instance, String fieldName, String cameFieldName) {
        Object value = getFieldValue(instance, fieldName);
        return value != null ? value : getFieldValue(instance, cameFieldName);
    }

    /**
     * 获取字段值
     */
    public static Object getFieldValue(Object instance, String fieldName, boolean toCamel) {
        return toCamel ? getFieldValue(instance, fieldName, CharSequenceUtil.toCamelCase(fieldName)) : getFieldValue(instance, fieldName);
    }

    /**
     * 填充对象
     *
     * @param objClass 待填充的对象类型
     * @param from     数据来源对象
     * @param maps     映射字段名，如果存在下划线会自动转驼峰
     */
    public static <T> T fillByFieldName(Class<T> objClass, Object from, Pair<String, String>... maps) {
        if (from == null || objClass == null) {
            return null;
        }
        return fillByFieldName(BeanUtil.copyProperties(from, objClass), from, maps);
    }

}
