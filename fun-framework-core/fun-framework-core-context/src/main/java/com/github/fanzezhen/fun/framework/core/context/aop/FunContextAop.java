package com.github.fanzezhen.fun.framework.core.context.aop;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.context.ContextHolder;
import com.github.fanzezhen.fun.framework.core.context.properties.FunContextExceptionEnum;
import com.github.fanzezhen.fun.framework.core.context.FunContextFilter;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
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
 * 上下文请求头校验和隐藏切面
 * <p>
 * 配合 {@link ContextHeader} 注解使用，提供两种能力：
 * <ul>
 *   <li>required: 校验必需的请求头是否存在，缺失时抛出ServiceException</li>
 *   <li>hidden: 临时隐藏指定请求头，方法执行完成后自动恢复</li>
 * </ul>
 * <p>
 * <b>使用场景：</b>防止内部方法调用时污染上下文（如移除traceId避免重复记录）
 *
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
     * 前置校验：检查必需的请求头是否存在
     *
     * @param joinPoint 切点信息
     * @throws ServiceException 当required中指定的请求头不存在时抛出
     */
    @Before("cut()")
    public void doBefore(JoinPoint joinPoint) {

        ContextHeader annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(ContextHeader.class);
        if (annotation.required()!=null){
            for (String header : annotation.required()) {
                String context = ContextHolder.get(header);
                if (CharSequenceUtil.isEmpty(context)){
                    throw new ServiceException(FunContextExceptionEnum.CONTEXT_HEADER_MISSING, header);
                }
            }
        }
    }

    /**
     * 环绕处理：临时隐藏指定请求头，方法执行完成后恢复
     * <p>
     * 执行流程：移除hidden指定的请求头 → 执行业务方法 → 恢复请求头
     *
     * @param joinPoint 切点信息
     * @return 业务方法返回值
     * @throws Throwable 业务方法抛出的异常
     */
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
