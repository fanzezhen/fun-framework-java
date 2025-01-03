package com.github.fanzezhen.fun.framework.log.support.web;

import com.alibaba.fastjson.JSON;
import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 日志切面
 *
 * @author fanzezhen
 * @createTime 2024/2/21 19:19
 * @since 1.0.0
 */
@Component
@Slf4j
@Aspect
public class LogAop {
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) " +
        "|| @within(org.springframework.stereotype.Controller)")
    public void webExecutePointcut() {
    }

    @Around("webExecutePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final Object object = joinPoint.proceed(joinPoint.getArgs());
        if (object instanceof ActionResult<?> || object instanceof Serializable){
            MDC.put("接口返回结果", JSON.toJSONString(object));
        }
        return object;
    }
}
