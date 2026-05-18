package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;

/**
 * 上下文过滤器.
 * <p>
 * 用于在HTTP请求处理前从请求头中提取上下文信息，并在请求结束后清理上下文，
 * 防止内存泄漏和线程池中的数据污染。
 * <p>
 * 过滤器优先级为0，确保在其他过滤器之前执行。
 */
@Slf4j
@WebFilter
@Order(0)
public class FunContextFilter implements Filter {
    /**
     * 追踪ID的Key.
     */
    @Value("${fun.log.key.trace-id:traceId}")
    private String traceIdKey;

    /**
     * 过滤器核心方法.
     * <p>
     * 从请求头中提取上下文信息，设置到ContextHolder中，
     * 请求处理完成后清理上下文。
     *
     * @param servletRequest  请求对象
     * @param servletResponse 响应对象
     * @param chain           过滤器链
     */
    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            if (log.isDebugEnabled()) {
                log.debug("system context init");
            }
            Enumeration<String> headerNames = request.getHeaderNames();
            JSONObject headers = new JSONObject();
            while (headerNames.hasMoreElements()) {
                String curHeader = headerNames.nextElement();
                String headerVal = request.getHeader(curHeader);
                headers.put(curHeader, headerVal);
                if (!CharSequenceUtil.startWithIgnoreCase(curHeader, ContextHolder.getProperties().getKey().getPrefix())) {
                    continue;
                }
                String requestUri = request.getRequestURI();
                ContextHolder.put(curHeader, headerVal);
                log(requestUri, curHeader, headerVal);
            }
            ContextHolder.setOriginHeaders(headers);
            String traceId = MDC.get(traceIdKey);
            if (CharSequenceUtil.isNotBlank(traceId)) {
                ContextHolder.setTraceId(traceId);
            }
            chain.doFilter(servletRequest, servletResponse);
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            ContextHolder.clean();
        }
    }

    /**
     * 记录请求头日志.
     *
     * @param requestUri 请求URI
     * @param headerKey  请求头Key
     * @param headerVal  请求头Value
     */
    private static void log(final String requestUri, final String headerKey, final String headerVal) {
        if (log.isDebugEnabled()) {
            log.debug("request {} has  header: {}, with value {}", requestUri, headerKey, headerVal);
        }
    }
}
