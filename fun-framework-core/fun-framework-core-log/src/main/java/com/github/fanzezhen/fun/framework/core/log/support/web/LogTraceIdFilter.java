package com.github.fanzezhen.fun.framework.core.log.support.web;

import cn.hutool.core.lang.UUID;
import com.github.fanzezhen.fun.framework.core.context.ContextConstant;
import com.github.fanzezhen.fun.framework.core.context.SysContextHolder;
import com.github.fanzezhen.fun.framework.core.log.config.FunCoreLogAutoConfiguration;
import com.github.fanzezhen.fun.framework.core.log.support.LogSysContextTaskDecorator;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * TraceId 过滤器
 *
 * @author fanzezhen
 * @since 3.1.7
 */
@Slf4j
@WebFilter(filterName = "traceIdFilter", urlPatterns = "/*")
@Order(Integer.MIN_VALUE)
@Component
public class LogTraceIdFilter implements Filter {
    @Bean
    @ConditionalOnMissingBean
    LogSysContextTaskDecorator logSysContextTaskDecorator() {
        return new LogSysContextTaskDecorator();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String traceId = UUID.fastUUID().toString(true);
        MDC.put(ContextConstant.KEY_TRACE_ID, traceId);
        SysContextHolder.setTraceId(traceId);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }
    }

}
