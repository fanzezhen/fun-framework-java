package com.github.fanzezhen.fun.framework.core.strategy;

import com.github.fanzezhen.fun.framework.core.service.AbstractAnnotationFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author zezhen.fan
 * @date 2023/10/13
 */
@Component
public class StrategyPostConstruct {
    private final Map<String, AbstractAnnotationFieldService> annotationFieldServiceMap;

    @Autowired
    public StrategyPostConstruct(Map<String, AbstractAnnotationFieldService> annotationFieldServiceMap) {
        this.annotationFieldServiceMap = annotationFieldServiceMap;
    }

    @PostConstruct
    public void init() {
        AnnotationFieldServiceStrategy.setServiceMap(annotationFieldServiceMap);
    }
}
