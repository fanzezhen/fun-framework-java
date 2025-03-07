package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Slf4j
@Aspect
@Component
@ConditionalOnBean(SysContextFilter.class)
public class ContextCheckAop {

    /**
     * 要处理的方法，包名+类名+方法名
     */
    @Pointcut("@annotation(com.github.fanzezhen.fun.framework.core.context.ContextCheck)")
    public void cut() {
    }

    /**
     * 在调用上面 @Pointcut标注的方法前执行以下方法
     *
     * @param joinPoint JoinPoint
     */
    @Before("cut()")
    public void doBefore(JoinPoint joinPoint) {
        
        ContextCheck annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(ContextCheck.class);
        if (annotation.headers()!=null){
            for (String header : annotation.headers()) {
                String context = SysContextHolder.get(header);
                if (CharSequenceUtil.isEmpty(context)){
                    throw ExceptionUtil.wrapException(FunContextExceptionEnum.CONTEXT_HEADER_MISSING, header);
                }
            }
        }
    }
}
