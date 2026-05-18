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
 * Web层日志打印过滤器.
 * <p>
 * 使用AOP切面拦截Controller层方法，自动打印请求和响应日志。
 * 支持通过配置禁用日志打印功能。
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

    /**
     * 构造函数.
     *
     * @param funLogHelper 日志助手
     */
    public FunSpringbootWebLogPrintFilter(final FunLogHelper funLogHelper) {
        super(funLogHelper);
    }

    /**
     * 定义切点，拦截RestController和Controller注解的类.
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) " +
        "|| @within(org.springframework.stereotype.Controller)")
    public void webExecutePointcut() {
    }

    /**
     * 环绕通知，打印方法执行日志.
     *
     * @param joinPoint 连接点
     *
     * @return 方法执行结果
     *
     * @throws Throwable 方法执行异常
     */
    @Around("webExecutePointcut()")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        if (getFunLogHelper().isDisabled(FunSpringbootWebLogPrintFilter.class.getName())) {
            return joinPoint.proceed(joinPoint.getArgs());
        }
        return getFunLogHelper().executeByLog(
            FunSpringbootWebLogPrintFilter.class.getName(),
            joinPoint::proceed,
            joinPoint.getArgs()
        );
    }

}
