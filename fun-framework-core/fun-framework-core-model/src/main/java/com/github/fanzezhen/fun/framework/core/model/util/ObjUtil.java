package com.github.fanzezhen.fun.framework.core.model.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象处理工具类
 * <p>
 * 提供对象类型转换、字段值解析、Java原生类型判断等功能。
 * 支持List/Map泛型解析和JSON字符串反序列化。
 *
 */
@Slf4j
public class ObjUtil {
    private ObjUtil() {
    }

    /**
     * 返回对应类型的空值
     * <p>
     * 如果为List，则返回空数组，如果为map，则返回空map，如果为对象，则返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T empty(Class<T> tClass) {
        if (Object.class.equals(tClass)) {
            return null;
        }
        // 按照字段类型解析值
        if (tClass.isAssignableFrom(List.class)) {
            return (T) new ArrayList<>(0);
        } else if (tClass.isAssignableFrom(Map.class)) {
            return (T) HashMap.newHashMap(0);
        } else if (tClass.isAssignableFrom(String.class)) {
            return (T) CharSequenceUtil.EMPTY;
        }
        return null;
    }
    /**
     * 将value解析为targetField的值
     * <p>
     * 根据目标字段的类型自动进行类型转换：
     * <ul>
     *   <li>List类型：解析泛型并转换为List（支持JSON字符串）</li>
     *   <li>JSONObject：从Map或JSON字符串构建</li>
     *   <li>自定义类：从JSON字符串反序列化</li>
     *   <li>原生类型：原样返回</li>
     * </ul>
     *
     * @param targetField 目标属性
     * @param value       原始值
     * @return 解析后的值，类型匹配targetField
     */
    public static Object resolveByField(Field targetField, Object value) {
        final Class<?> targetFieldClass = targetField.getType();

        // 按照字段类型解析值
        if (List.class.isAssignableFrom(targetFieldClass)) {
            // 获取List的泛型
            Class<?> actualType = (Class<?>) TypeUtil.getTypeArgument(targetField.getGenericType());
            if (value instanceof List) {
                return Convert.toList(actualType, value);
            }
            if (value instanceof String) {
                try {
                    return JSON.parseArray(value.toString(), actualType);
                }catch (Exception e){
                    log.debug("", e);
                }
            }
            return JSON.parseArray(JSON.toJSONString(Collections.singletonList(value)), actualType);
        } else if (targetFieldClass.isAssignableFrom(JSONObject.class)) {
            if (value instanceof Map<?, ?> map) {
                return new JSONObject(map);
            } else if (value instanceof String) {
                return JSON.parseObject(value.toString(), JSONObject.class);
            }
        } else if (!isNativeClass(targetFieldClass)) {
            if (!(value instanceof String)) {
                return value;
            }
            if (JSONUtil.isTypeJSONObject(value.toString())) {
                return JSON.parseObject(value.toString(), targetFieldClass);
            }
        }
        return value;
    }

    /**
     * 判断类型是否为Java原生类型
     * <p>
     * 通过ClassLoader是否为null判断，原生类（如String、Integer等）的ClassLoader为null
     *
     * @param clz 待判断的类
     * @return true表示为原生类型
     */
    public static boolean isNativeClass(Class<?> clz) {
        return clz != null && clz.getClassLoader() == null;
    }
}
