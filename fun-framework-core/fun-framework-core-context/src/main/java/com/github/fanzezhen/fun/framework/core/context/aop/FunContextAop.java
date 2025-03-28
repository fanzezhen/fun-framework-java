package com.github.fanzezhen.fun.framework.core.context.aop;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.context.properties.FunContextExceptionEnum;
import com.github.fanzezhen.fun.framework.core.context.FunContextFilter;
import com.github.fanzezhen.fun.framework.core.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * @author fanzezhen
 */
@Slf4j
@Aspect
@Component
@ConditionalOnBean(FunContextFilter.class)
public class FunContextAop {

    /**
     * 要处理的方法，包名+类名+方法名
     */
    @Pointcut("@annotation(com.github.fanzezhen.fun.framework.core.context.aop.ContextHeader)")
    public void cut() {
    }

    /**
     * 在调用上面 @Pointcut标注的方法前执行以下方法
     *
     * @param joinPoint JoinPoint
     */
    @Before("cut()")
    public void doBefore(JoinPoint joinPoint) {
        
        ContextHeader annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(ContextHeader.class);
        if (annotation.required()!=null){
            for (String header : annotation.required()) {
                String context = ContextHolder.get(header);
                if (CharSequenceUtil.isEmpty(context)){
                    throw ExceptionUtil.wrapException(FunContextExceptionEnum.CONTEXT_HEADER_MISSING, header);
                }
            }
        }
    }
    @Around("cut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ContextHeader annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(ContextHeader.class);
        JSONObject context = null;
        if (annotation.hidden() != null) {
            context = new JSONObject(annotation.hidden().length);
            for (String header : annotation.hidden()) {
                context.put(header, ContextHolder.remove(header));
            }
        }
        Object proceeded = joinPoint.proceed();
        if (context != null) {
            ContextHolder.put(context);
        }
        return proceeded;
    }
}
