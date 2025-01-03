package com.github.fanzezhen.fun.framework.core.strategy;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.service.AbstractAnnotationFieldService;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zezhen.fan
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class AnnotationFieldServiceStrategy {
    private AnnotationFieldServiceStrategy() {
    }

    static Map<String, AbstractAnnotationFieldService> serviceMap;
    static Set<String> annotationNameSet = null;

    public static void setAnnotationFieldValue(Object bean, Object fieldValue, Annotation annotation, String fieldName, Map<String, Object[]> setterMap) {
        serviceMap.get(StringUtils.uncapitalize(annotation.annotationType().getSimpleName()) + "Service").setAnnotationField(bean, fieldValue, annotation, fieldName, setterMap);
    }

    public static Set<String> getAnnotationNameSet() {
        if (annotationNameSet == null) {
            annotationNameSet = serviceMap.keySet().stream().map(name -> StringUtils.capitalize(CharSequenceUtil.removeSuffix(name, "Service"))).collect(Collectors.toSet());
        }
        return annotationNameSet;
    }

     static void setServiceMap(Map<String, AbstractAnnotationFieldService> serviceMap) {
        AnnotationFieldServiceStrategy.serviceMap = serviceMap;
    }
}
