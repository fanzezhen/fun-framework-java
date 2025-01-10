package com.github.fanzezhen.fun.framework.log.support.web;

import cn.hutool.core.lang.UUID;
import com.github.fanzezhen.fun.framework.log.config.LogSpringConfig;
import com.github.fanzezhen.fun.framework.log.support.LogSysContextTaskDecorator;
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
 * @createTime 2024/2/1 17:40
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
        MDC.put(LogSpringConfig.TRACE_ID, UUID.fastUUID().toString(true));
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }
    }

}
