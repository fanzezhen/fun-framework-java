package com.github.fanzezhen.fun.framework.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author fanzezhen
 * Desc:
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ClassInfoBean {
    private Field[] fields;
    private Map<Object, Object[]> getterAnnotationsMap;
    private Map<Object, Object[]> setterAnnotationsMap;
    private Map<String, Object[]> fieldNameToGetterMap;
    private Map<String, Object[]> fieldNameToSetterMap;
    private Map<String, Field> fieldNameToGetFieldMap;
    private Map<String, Method> fieldNameToGetMethodMap;
    private Map<String, Field> fieldNameToSetFieldMap;
    private Map<String, Method[]> fieldNameToSetMethodMap;

}
