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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * @author zezhen.fan
 */
@Slf4j
@WebFilter
@Order(0)
public class SysContextFilter implements Filter {

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
            SysContextHolder.clean();
        }
    }

    private static void filterHeader(String curHeader, HttpServletRequest request) {
        if (!CharSequenceUtil.startWithIgnoreCase(curHeader, ContextConstant.CONTEXT_HEADER_PREFIX)) {
            return;
        }
        String headerVal = request.getHeader(curHeader);
        String requestUri = request.getRequestURI();
        if (CharSequenceUtil.equalsIgnoreCase(curHeader, ContextConstant.HEADER_USER_ID)) {
            SysContextHolder.setUserId(headerVal);
            log(requestUri, ContextConstant.HEADER_USER_ID, headerVal);
        } else if (CharSequenceUtil.equalsIgnoreCase(curHeader, ContextConstant.HEADER_ACCOUNT_ID)) {
            SysContextHolder.set(ContextConstant.HEADER_ACCOUNT_ID, headerVal);
            log(requestUri, ContextConstant.HEADER_ACCOUNT_ID, headerVal);
        } else if (CharSequenceUtil.equalsIgnoreCase(curHeader, ContextConstant.HEADER_ACCOUNT_NAME)) {
            headerVal = URLDecoder.decode(headerVal, StandardCharsets.UTF_8);
            SysContextHolder.set(ContextConstant.HEADER_ACCOUNT_NAME, headerVal);
            log(requestUri, ContextConstant.HEADER_ACCOUNT_NAME, headerVal);
        } else if (CharSequenceUtil.equalsIgnoreCase(curHeader, ContextConstant.HEADER_USER_NAME)) {
            headerVal = URLDecoder.decode(headerVal, StandardCharsets.UTF_8);
            SysContextHolder.set(ContextConstant.HEADER_USER_NAME, headerVal);
            log(requestUri, ContextConstant.HEADER_USER_NAME, headerVal);
        } else if (CharSequenceUtil.equalsIgnoreCase(curHeader, ContextConstant.HEADER_USER_IP)) {
            SysContextHolder.set(ContextConstant.HEADER_USER_IP, headerVal);
            log(requestUri, ContextConstant.HEADER_USER_IP, headerVal);
        } else if (CharSequenceUtil.equalsIgnoreCase(curHeader, ContextConstant.HEADER_USER_AGENT)) {
            SysContextHolder.set(ContextConstant.HEADER_USER_AGENT, headerVal);
            log(requestUri, ContextConstant.HEADER_USER_AGENT, headerVal);
        } else if (CharSequenceUtil.equalsIgnoreCase(curHeader, ContextConstant.HEADER_TENANT_ID)) {
            SysContextHolder.set(ContextConstant.HEADER_TENANT_ID, headerVal);
            log(requestUri, ContextConstant.HEADER_TENANT_ID, headerVal);
        } else if (CharSequenceUtil.equalsIgnoreCase(curHeader, ContextConstant.HEADER_LOCALE)) {
            SysContextHolder.set(ContextConstant.HEADER_LOCALE, headerVal);
            log(requestUri, ContextConstant.HEADER_LOCALE, headerVal);
        } else if (CharSequenceUtil.equalsIgnoreCase(curHeader, ContextConstant.HEADER_APP_CODE)) {
            SysContextHolder.set(ContextConstant.HEADER_APP_CODE, headerVal);
            if (log.isDebugEnabled()) {
                log(requestUri, ContextConstant.HEADER_APP_CODE, headerVal);
            }
        } else {
            SysContextHolder.set(curHeader, headerVal);
        }
    }

    private static void log(String requestUri, String headerUserId, String headerVal) {
        if (log.isDebugEnabled()) {
            log.debug("request {} has  header: {}, with value {}", requestUri, headerUserId, headerVal);
        }
    }
}
