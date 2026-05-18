package com.github.fanzezhen.fun.framework.core.log.support;

import cn.hutool.core.lang.UUID;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 生成和管理跟踪 ID 的过滤器.
 *
 * @since 3.1.7
 */
@Slf4j
@WebFilter(filterName = "funTraceIdFilter", urlPatterns = "/*")
@Order(Integer.MIN_VALUE)
@Component
public class FunLogTraceIdFilter implements Filter {
    /**
     * 跟踪 ID 的 MDC 键.
     */
    @Value("${fun.log.key.trace-id:traceId}")
    private String traceIdKey;
    /**
     * 生成跟踪 ID 并注入 MDC 以进行请求跟踪.
     *
     * @param servletRequest servlet 请求
     * @param servletResponse servlet 响应
     * @param filterChain 过滤器链
     * @throws IOException 如果发生 I/O 错误
     * @throws ServletException 如果发生 servlet 错误
     */
    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {
        String traceId = UUID.fastUUID().toString(true);
        MDC.put(traceIdKey, traceId);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }
    }

}
