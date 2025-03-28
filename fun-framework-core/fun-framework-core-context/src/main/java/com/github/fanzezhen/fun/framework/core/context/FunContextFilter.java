package com.github.fanzezhen.fun.framework.core.context;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            if (log.isDebugEnabled()) {
                log.debug("system context init");
            }
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) filterHeader(headerNames.nextElement(), request);
            chain.doFilter(servletRequest, servletResponse);
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            ContextHolder.clean();
        }
    }

    private static void filterHeader(String curHeader, HttpServletRequest request) {
        if (!CharSequenceUtil.startWithIgnoreCase(curHeader, ContextHolder.properties.getKey().getPrefix())) {
            return;
        }
        String headerVal = request.getHeader(curHeader);
        String requestUri = request.getRequestURI();
        ContextHolder.set(curHeader, headerVal);
        log(requestUri, curHeader, headerVal);
    }

    private static void log(String requestUri, String headerKey, String headerVal) {
        if (log.isDebugEnabled()) {
            log.debug("request {} has  header: {}, with value {}", requestUri, headerKey, headerVal);
        }
    }
}
