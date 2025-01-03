package com.github.fanzezhen.fun.framework.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.fun.framework.core.dict.AbstractDict;
import com.github.fanzezhen.fun.framework.core.model.bean.AttributeBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * @author fanzezhen
 */
@Slf4j
public class ReflectionUtil {
    /**
     * 获取bean的所有field
     *
     * @param bean bean
     * @return Field[]
     */
    public static Field[] getAttributeFields(Object bean) {
        return bean.getClass().getDeclaredFields();
    }

    /**
     * @param field 字段
     * @param bean  实体
     * @return AttributeBean
     */
    public static AttributeBean getAttribute(Field field, Object bean) {
        String name = field.getName();
        // 获取属性的类型
        String type = field.getGenericType().toString();
        Object value = extractValue(bean, name);
        return new AttributeBean(type, name, value);
    }

    private static Object extractValue(Object bean, String name) {
        Object value;
        try {
            // 将属性的首字符大写，构造get方法
            Method m = bean.getClass().getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
            // 调用getter方法获取属性值
            value = m.invoke(bean);
        } catch (NoSuchMethodException e) {
            String errMsg = "反射Getter方法未找到！" + e.getLocalizedMessage();
            log.warn(errMsg);
            e.printStackTrace();
            throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), errMsg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            String errMsg = "反射Getter方法调用失败！" + e.getLocalizedMessage();
            log.warn(errMsg);
            e.printStackTrace();
            throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), errMsg);
        }
        return value;
    }

    /**
     * @param fieldName 字段名
     * @param bean      实体
     * @return value
     */
    public static Object getValue(String fieldName, Object bean) {

        return extractValue(bean, fieldName);
    }

    /**
     * @param fieldName 字段名
     * @param bean      实体
     * @return value
     */
    public static <V> V getValue(String fieldName, Object bean, Function<? super Object, V> converter) {
        return converter.apply(getValue(fieldName, bean));
    }

    /**
     * @param fieldName 字段名
     * @param bean      实体
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public static boolean isEmptyValue(String fieldName, Object bean) {
        Object value = getValue(fieldName, bean);
        if (value == null || CharSequenceUtil.isEmpty(String.valueOf(value))) {
            return true;
        }
        if (value instanceof Iterable) {
            return CollUtil.isEmpty((Iterable<Object>) value);
        }
        if (value instanceof Iterator) {
            return CollUtil.isEmpty((Iterator<Object>) value);
        }
        if (value instanceof Enumeration) {
            return CollUtil.isEmpty((Enumeration<Object>) value);
        }
        if (value instanceof Map) {
            return MapUtil.isEmpty((Map<Object, Object>) value);
        }
        if (value instanceof Object[]) {
            return ArrayUtil.isEmpty((Object[]) value);
        }
        if (value instanceof Array) {
            return ArrayUtil.isEmpty(value);
        }
        return false;
    }

    /**
     * bean转hashMap
     *
     * @param javaBean javaBean
     * @return HashMap
     */
    public static HashMap<String, Object> javaBeanToHashMap(Object javaBean) {
        HashMap<String, Object> map = new HashMap<>(32);
        // 获取所有方法
        Method[] methods = javaBean.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                // 拼接属性名
                String field = method.getName();
                field = field.substring(field.indexOf("get") + 3);
                field = field.toLowerCase().charAt(0) + field.substring(1);
                // 执行方法
                Object value = null;
                try {
                    value = method.invoke(javaBean, (Object[]) null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                map.put(field, value);
            }
        }
        return map;
    }

    /**
     * 获取值为Null的属性名
     *
     * @param source source
     * @return String[]
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 获取两个bean属性值的区别
     *
     * @param oldObject oldObject
     * @param newObject newObject
     * @return List
     */
    public static List<HashMap<String, Object>> getDifferentPropertyList(Object oldObject, Object newObject) {
        List<HashMap<String, Object>> result = new ArrayList<>();
        List<Field> oldFieldList = Arrays.asList(getAttributeFields(oldObject));
        List<Field> newFieldList = Arrays.asList(getAttributeFields(newObject));
        List<Field> fieldList = new ArrayList<>();
        fieldList.addAll(oldFieldList);
        fieldList.addAll(newFieldList);
        for (Field field : new HashSet<>(fieldList)) {
            HashMap<String, Object> hashMap = new HashMap<>(4);
            if (!oldFieldList.contains(field)) {
                Object newValue = getAttribute(field, newObject).getValue();
                if (newValue != null) {
                    hashMap.put("name", field.getName());
                    hashMap.put("oldValue", null);
                    hashMap.put("newValue", newValue);
                }
            } else if (!newFieldList.contains(field)) {
                Object oldValue = getAttribute(field, oldObject).getValue();
                if (oldValue != null) {
                    hashMap.put("name", field.getName());
                    hashMap.put("oldValue", oldValue);
                    hashMap.put("newValue", null);
                }
            } else {
                Object oldValue = getAttribute(field, oldObject).getValue();
                Object newValue = getAttribute(field, newObject).getValue();
                if (oldValue != null) {
                    if (!oldValue.equals(newValue)) {
                        hashMap.put("name", field.getName());
                        hashMap.put("oldValue", oldValue);
                        hashMap.put("newValue", newValue);
                    }
                } else if (newValue != null) {
                    hashMap.put("name", field.getName());
                    hashMap.put("oldValue", "");
                    hashMap.put("newValue", newValue);
                }
            }
            if (!hashMap.isEmpty()) {
                result.add(hashMap);
            }
        }
        return result;
    }

    public static HashMap<String, HashMap<String, Object>> getDiff(Object before, Object after, AbstractDict dict, boolean isGetAll) {
        HashMap<String, Object> beforeMap = new HashMap<>(8);
        HashMap<String, Object> afterMap = new HashMap<>(8);
        for (HashMap<String, Object> hashMap : getDifferentPropertyList(before, after)) {
            if (isGetAll || dict.containsKey(hashMap.get("name"))) {
                beforeMap.put(dict.get(String.valueOf(hashMap.get("name"))), hashMap.get("oldValue"));
                afterMap.put(dict.get(String.valueOf(hashMap.get("name"))), hashMap.get("newValue"));
            }
        }
        return new HashMap<>(2, 1) {{
            put("before", beforeMap);
            put("after", afterMap);
        }};
    }

    public static HashMap<String, Object> extractByDict(Object o, AbstractDict dict) {
        HashMap<String, Object> result = new HashMap<>(8);
        HashMap<String, Object> map = javaBeanToHashMap(o);
        for (String key : map.keySet()) {
            if (dict.containsKey(key)) {
                result.put(dict.get(key), map.get(key));
            }
        }
        return result;
    }

    /**
     * 通过反射为属性赋值， 需要同时存在该属性的setter和getter方法
     *
     * @param bean      bean
     * @param fieldName fieldName
     * @param value     value
     * @return boolean
     */
    public static boolean set(Object bean, String fieldName, Object value) {
        try {
            String capitalized = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getterMethod = bean.getClass().getMethod("get" + capitalized);
            Method setterMethod = bean.getClass().getMethod("set" + capitalized, getterMethod.getReturnType());
            setterMethod.invoke(bean, CommonUtil.cast(getterMethod.getReturnType(), value));
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isSubClass(Class<?> clazz1, Class<?> clazz2) {
        for (Class<?> parent = clazz1.getSuperclass(); parent != null; parent = parent.getSuperclass()) {
            if (parent.getName().equals(clazz2.getName())) {
                return true;
            }
        }

        return false;
    }
}
