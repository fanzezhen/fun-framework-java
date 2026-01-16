package com.github.fanzezhen.fun.framework.core.log.base.support;

import cn.hutool.core.lang.UUID;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
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
@WebFilter(filterName = "funTraceIdFilter", urlPatterns = "/*")
@Order(Integer.MIN_VALUE)
@Component
public class FunLogTraceIdFilter implements Filter {
    /**
     * 痕迹的key
     */
    @Value("${fun.log.key.trace-id:traceId}")
    private String traceIdKey;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String traceId = UUID.fastUUID().toString(true);
        MDC.put(traceIdKey, traceId);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }
    }

}
