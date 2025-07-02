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
 * @author fanzezhen
 */
@Slf4j
@WebFilter
@Order(0)
public class FunContextFilter implements Filter {
    /**
     * 痕迹的key
     */
    @Value("${fun.core.log.key.trace-id:traceId}")
    private String traceIdKey;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
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
                if (!CharSequenceUtil.startWithIgnoreCase(curHeader, ContextHolder.properties.getKey().getPrefix())) {
                    return;
                }
                String requestUri = request.getRequestURI();
                ContextHolder.set(curHeader, headerVal);
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

    private static void log(String requestUri, String headerKey, String headerVal) {
        if (log.isDebugEnabled()) {
            log.debug("request {} has  header: {}, with value {}", requestUri, headerKey, headerVal);
        }
    }
}
