package com.github.fanzezhen.fun.framework.core.springboot.web.log;

import com.github.fanzezhen.fun.framework.core.log.support.AbstractFunLogPrintFilter;
import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import jakarta.annotation.Resource;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 打印日志
 *
 * @since 3.1.7
 */
@Slf4j
@Aspect
@Component
@WebFilter(filterName = "funLogPrintFilter", urlPatterns = "/*")
@SuppressWarnings("unused")
@Order(Short.MIN_VALUE + 1)
public class FunSpringbootWebLogPrintFilter extends AbstractFunLogPrintFilter {

    public FunSpringbootWebLogPrintFilter(FunLogHelper funLogHelper) {
        super(funLogHelper);
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) " +
        "|| @within(org.springframework.stereotype.Controller)")
    public void webExecutePointcut() {
    }

    @Around("webExecutePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (funLogHelper.isDisabled(FunSpringbootWebLogPrintFilter.class.getName())) {
            return joinPoint.proceed(joinPoint.getArgs());
        }
        return funLogHelper.executeByLog(FunSpringbootWebLogPrintFilter.class.getName(), joinPoint::proceed, joinPoint.getArgs());
    }

}
