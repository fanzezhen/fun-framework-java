package com.github.fanzezhen.fun.framework.core.springboot.ai.log;

import com.github.fanzezhen.fun.framework.core.log.support.AbstractFunLogPrintFilter;
import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Spring AI 工具方法日志打印过滤器.
 * <p>
 * 拦截所有标注 {@link Tool} 注解的方法，自动打印日志。
 * </p>
 *
 * @since 4.0.6
 */
@Slf4j
@Aspect
@Component
@SuppressWarnings("unused")
@Order(Short.MIN_VALUE + 1)
public class FunSpringbootAiLogPrintFilter extends AbstractFunLogPrintFilter {

    /**
     * 构造函数.
     *
     * @param funLogHelper 日志辅助工具
     */
    public FunSpringbootAiLogPrintFilter(final FunLogHelper funLogHelper) {
        super(funLogHelper);
    }

    /**
     * 环绕通知，拦截 Tool 注解方法并打印日志.
     *
     * @param joinPoint 连接点
     * @param tool      Tool 注解
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(tool)")
    public Object around(final ProceedingJoinPoint joinPoint, final Tool tool) throws Throwable {
        if (funLogHelper.isDisabled(
                FunSpringbootAiLogPrintFilter.class.getName())) {
            return joinPoint.proceed(joinPoint.getArgs());
        }
        return funLogHelper.executeByLog(
                FunSpringbootAiLogPrintFilter.class.getName(),
                joinPoint::proceed,
                joinPoint.getArgs());
    }

}
