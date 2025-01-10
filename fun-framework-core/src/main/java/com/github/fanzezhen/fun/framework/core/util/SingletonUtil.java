package com.github.fanzezhen.fun.framework.core.util;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.github.fanzezhen.fun.framework.core.model.ClassInfoBean;
import com.github.fanzezhen.fun.framework.core.strategy.AnnotationFieldServiceStrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zezhen.fan
 */
public class SingletonUtil {
    private SingletonUtil() {
    }

    public static final Log log = LogFactory.get(SingletonUtil.class);
    public static final String[] getterPrefixes = new String[]{"get", "is"};
    public static final String[] setterPrefixes = new String[]{"set"};

    /**
     * Byte 1 ：map = Getter's special annotations
     * Byte 2 ：map = Setter's special annotations
     * Byte 3 ：map = Getters or Field
     * Byte 4 ：map = Setters or Field
     * Byte 1 ：Boolean = if getters have special annotations
     * Byte 2 ：Boolean = if setters have special annotations
     *
     * @return <Class<?>, Map<Byte, Map<String, Object>>>
     */
    static TimedCache<Class<?>, ClassInfoBean> getHourTimedCacheForClassInfo() {
        return ClassInfoHourCacheSingletonHolder.HOUR_TIMED_CACHE_FOR_CLASS_INFO;
    }

    public static Field[] getClassFields(Class<?> clazz) {
        return getClassInfo(clazz).getFields();
    }

    public static Map<String, Object[]> getClassSetterMap(Class<?> clazz) {
        return getClassInfo(clazz).getFieldNameToSetterMap();
    }

    public static ClassInfoBean getClassInfo(Class<?> clazz) {
        return getHourTimedCacheForClassInfo().get(clazz, true, () -> {
            ClassInfoBean classInfo = new ClassInfoBean();
            Field[] fields = getFields(clazz);
            loadGetterAttribute(classInfo, clazz, fields);
            loadSetterAttribute(classInfo, clazz, fields);
            return classInfo.setFields(fields);
        });
    }

    private static Field[] getFields(Class<?> clazz) {
        return ReflectUtil.getFields(clazz, field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isNative(field.getModifiers()));
    }

    private static void loadSetterAttribute(ClassInfoBean classInfo, Class<?> clazz, Field[] fields) {
        Map<Object, Object[]> setterAnnotationMap = new HashMap<>(2, 1);
        Map<String, Object[]> setterMap = new HashMap<>(16);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            setterMap.put(field.getName(), new Object[]{field});
        }
        for (Method method : ReflectUtil.getPublicMethods(clazz)) {
            if (Modifier.isStatic(method.getModifiers()) || Modifier.isNative(method.getModifiers())) {
                continue;
            }
            loadSetter(setterMap, setterAnnotationMap, method);
        }
        classInfo.setSetterAnnotationsMap(setterAnnotationMap);
        classInfo.setFieldNameToSetterMap(setterMap);
        Map<String, Field> fieldNameToSetFieldMap = new HashMap<>(setterMap.size(), 1);
        Map<String, Method[]> fieldNameToSetMethodMap = new HashMap<>(setterMap.size(), 1);
        setterMap.forEach(((fieldName, objects) -> {
            for (int i = 0, objectsLength = objects.length; i < objectsLength; i++) {
                Object o = objects[i];
                if (o instanceof Field field) {
                    fieldNameToSetFieldMap.put(fieldName, field);
                    Method[] methods = new Method[objects.length - 1];
                    System.arraycopy(objects, 0, methods, 0, i);
                    System.arraycopy(objects, i + 1, methods, i, methods.length - i);
                    fieldNameToSetMethodMap.put(fieldName, methods);
                    return;
                }
            }
            Method[] methods = new Method[objects.length];
            System.arraycopy(objects, 0, methods, 0, objects.length);
            fieldNameToSetMethodMap.put(fieldName, methods);
            classInfo.setFieldNameToSetFieldMap(fieldNameToSetFieldMap);
            classInfo.setFieldNameToSetMethodMap(fieldNameToSetMethodMap);
        }));
    }

    private static void loadGetterAttribute(ClassInfoBean classInfo, Class<?> clazz, Field[] fields) {
        Map<Object, Object[]> getterAnnotationMap = new HashMap<>(2, 1);
        Map<String, Object[]> getterMap = new HashMap<>(16);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String fieldName = field.getName();
            getterMap.put(fieldName, new Object[]{field});
        }
        for (Method method : ReflectUtil.getPublicMethods(clazz)) {
            if (Modifier.isStatic(method.getModifiers()) || Modifier.isNative(method.getModifiers())) {
                continue;
            }
            loadGetter(getterMap, getterAnnotationMap, method);
        }
        classInfo.setGetterAnnotationsMap(getterAnnotationMap);
        classInfo.setFieldNameToGetterMap(getterMap);
        Map<String, Field> fieldNameToGetFieldMap = new HashMap<>(getterMap.size(), 1);
        Map<String, Method> fieldNameToGetMethodMap = new HashMap<>(getterMap.size(), 1);
        getterMap.forEach(((fieldName, objects) -> {
            for (Object o : objects) {
                if (o instanceof Field field) {
                    fieldNameToGetFieldMap.put(field.getName(), field);
                } else if (o instanceof Method method) {
                    fieldNameToGetMethodMap.put(fieldName, method);
                }
            }
        }));
        classInfo.setFieldNameToGetFieldMap(fieldNameToGetFieldMap);
        classInfo.setFieldNameToGetMethodMap(fieldNameToGetMethodMap);
    }

    private static void loadGetter(Map<String, Object[]> getterMap, Map<Object, Object[]> getterAnnotationMap, Method method) {
        Class<?> methodReturnType = method.getReturnType();
        if (method.getParameterCount() > 0 || methodReturnType.equals(void.class)) {
            return;
        }
        merge(getterMap, getterAnnotationMap, method, getterPrefixes, methodReturnType);
    }

    private static void merge(Map<String, Object[]> terMap, Map<Object, Object[]> annotationMap, Method method, String[] prefixes, Class<?> requiredType) {
        String methodName = method.getName();
        for (String prefix : prefixes) {
            if (methodName.startsWith(prefix)) {
                String key = methodName.substring(prefix.length());
                if (!key.isEmpty()) {
                    terMap.compute(Character.toLowerCase(key.charAt(0)) + key.substring(1), (k, v) ->
                            getMergeItemValue(annotationMap, method, requiredType, v));
                }
                break;
            }
        }
        Map<Object, Object[]> supplementAnnotationMap = getAnnotationMap(terMap);
        supplementAnnotationMap.forEach(((getter, annotations) -> {
            if (annotations.length == 0) {
                return;
            }
            annotationMap.computeIfAbsent(getter, k -> annotations);
        }));
        Iterator<Map.Entry<Object, Object[]>> iterator = annotationMap.entrySet().iterator();
        Map.Entry<Object, Object[]> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (null == entry.getValue() || entry.getValue().length == 0) {
                iterator.remove();
            }
        }
    }

    private static Object[] getMergeItemValue(Map<Object, Object[]> annotationMap, Method method, Class<?> requiredType, Object[] v) {
        if (v == null) {
            return new Object[]{method};
        }
        short i = 0;
        for (; i < v.length; i++) {
            if (v[i] instanceof Field f) {
                // 为了使同一字段支持同一个注解的多个实例
                if (f.getType().isAssignableFrom(requiredType)) {
                    break;
                }
            } else if (v[i] instanceof Method m) {
                if (m.getReturnType().equals(requiredType)) {
                    annotationMap.put(m, deduplicationAnnotations(m, method));
                    return v;
                }
            }
        }
        Object[] objects;
        if (i < v.length) {
            objects = new Object[v.length];
            System.arraycopy(v, 0, objects, 0, i);
            objects[i] = method;
            annotationMap.put(method, deduplicationAnnotations(method, v[i]));
            System.arraycopy(v, ++i, objects, i, v.length - i);
        } else {
            objects = new Object[v.length + 1];
            System.arraycopy(v, 0, objects, 0, v.length);
            objects[v.length] = method;
            annotationMap.put(method, getAnnotations(method));
        }
        return objects;
    }

    private static void loadSetter(Map<String, Object[]> setterMap, Map<Object, Object[]> setterAnnotationMap, Method method) {
        if (method.getParameterCount() != 1) {
            return;
        }
        Class<?> parameterType = method.getParameterTypes()[0];
        merge(setterMap, setterAnnotationMap, method, setterPrefixes, parameterType);
    }

    private static Map<Object, Object[]> getAnnotationMap(Map<String, Object[]> getterMap) {
        return getterMap.values().stream()
                .flatMap(Arrays::stream)
                .collect(Collectors.toMap(Function.identity(), SingletonUtil::getAnnotations));
    }

    private static Object[] deduplicationAnnotations(Method method, Object supplement) {
        Object[] supplementAnnotations = getAnnotations(supplement);
        if (supplementAnnotations.length > 0) {
            Object[] methodAnnotations = getAnnotations(method);
            if (methodAnnotations.length > 0) {
                Map<Class<?>, Object> annotationMap = new HashMap<>(supplementAnnotations.length, 1);
                for (Object fieldAnnotation : supplementAnnotations) {
                    annotationMap.put(fieldAnnotation.getClass(), fieldAnnotation);
                }
                for (Object methodAnnotation : methodAnnotations) {
                    annotationMap.put(methodAnnotation.getClass(), methodAnnotation);
                }
                return annotationMap.values().toArray();
            }
        }
        return supplementAnnotations;
    }

    private static Object[] getAnnotations(Object getter) {
        return AnnotationFieldServiceStrategy.getAnnotationNameSet().stream()
                .map(annotationName -> {
                    if (getter instanceof Field field) {
                        for (Annotation annotation : field.getAnnotations()) {
                            if (annotation.annotationType().getSimpleName().equals(annotationName)){
                                return annotation;
                            }
                        }
                    } else if (getter instanceof Method method) {
                        for (Annotation annotation : method.getAnnotations()) {
                            if (annotation.annotationType().getSimpleName().equals(annotationName)){
                                return annotation;
                            }
                        }
                    }
                    return null;
                }).filter(Objects::nonNull).toArray();
    }
    static class ClassInfoHourCacheSingletonHolder {
        private ClassInfoHourCacheSingletonHolder() {
        }

        private static final TimedCache<Class<?>, ClassInfoBean> HOUR_TIMED_CACHE_FOR_CLASS_INFO = new TimedCache<>(60 * 60 * 1000L, new ConcurrentHashMap<>());

        static {
            HOUR_TIMED_CACHE_FOR_CLASS_INFO.schedulePrune(60 * 60 * 1000L);
        }
    }

}
