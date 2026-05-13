package com.github.fanzezhen.fun.framework.core.springboot.ai.log;

import com.github.fanzezhen.fun.framework.core.log.support.AbstractFunLogPrintFilter;
import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 打印日志
 *
 * @since 4.0.6
 */
@Slf4j
@Aspect
@Component
@SuppressWarnings("unused")
@Order(Short.MIN_VALUE + 1)
public class FunSpringbootAiLogPrintFilter extends AbstractFunLogPrintFilter {

    public FunSpringbootAiLogPrintFilter(FunLogHelper funLogHelper) {
        super(funLogHelper);
    }

    @Around("@annotation(tool)")
    public Object around(ProceedingJoinPoint joinPoint, Tool tool) throws Throwable {
        if (funLogHelper.isDisabled(FunSpringbootAiLogPrintFilter.class.getName())) {
            return joinPoint.proceed(joinPoint.getArgs());
        }
        return funLogHelper.executeByLog(FunSpringbootAiLogPrintFilter.class.getName(), joinPoint::proceed, joinPoint.getArgs());
    }

}
