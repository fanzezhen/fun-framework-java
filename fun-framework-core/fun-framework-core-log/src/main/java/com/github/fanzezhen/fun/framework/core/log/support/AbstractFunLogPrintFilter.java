package com.github.fanzezhen.fun.framework.core.log.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 打印日志
 *
 * @since 3.1.7
 */
@Slf4j
@Component
@WebFilter(filterName = "funLogPrintFilter", urlPatterns = "/*")
@SuppressWarnings("unused")
@Order(Short.MIN_VALUE + 1)
public abstract class AbstractFunLogPrintFilter implements Filter {
    private static final LevelLogger debugLogger = log::debug;
    private static final LevelLogger infoLogger = log::info;
    private static final LevelLogger warnLogger = log::warn;
    private static final LevelLogger errorLogger = log::error;
    private static final LevelLogger traceLogger = log::trace;
    private static final String REQUEST_START_TIME_KEY = "REQUEST_START_TIME_KEY";

    protected FunLogHelper funLogHelper;

    protected AbstractFunLogPrintFilter(FunLogHelper funLogHelper) {
        this.funLogHelper = funLogHelper;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest httpServletRequest) {
            preHandle(httpServletRequest, (HttpServletResponse) servletResponse, null);
            filterChain.doFilter(httpServletRequest, servletResponse);
            postHandle(httpServletRequest, (HttpServletResponse) servletResponse, null);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void preHandle( HttpServletRequest request,  HttpServletResponse response, Object handler) {
        LevelLogger levelLogger = funLogHelper.getOrGenerateLevelLogger(AbstractFunLogPrintFilter.class.getName());
        if (!LevelLogger.EMPTY.equals(levelLogger)) {
            MDC.put(REQUEST_START_TIME_KEY, String.valueOf(System.currentTimeMillis()));
            Enumeration<String> headerNames = request.getHeaderNames();
            Map<String, String> headerMap = new HashMap<>();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headerMap.put(headerName, headerValue);
            }
            levelLogger.log("==================================接口调用======================================");
            levelLogger.log("请求url：    {} {}", request.getMethod(), request.getRequestURL());
            levelLogger.log("请求Type：   {}", request.getContentType());
            levelLogger.log("请求IP：     {}", request.getRemoteAddr());
            levelLogger.log("请求Header： {}", headerMap);
            levelLogger.log("请求param：  {}", JSON.toJSONString(request.getParameterMap()));
        }
    }

    public void postHandle( HttpServletRequest request,  HttpServletResponse response, Object handler) {
        LevelLogger levelLogger = funLogHelper.getOrGenerateLevelLogger(AbstractFunLogPrintFilter.class.getName());
        if (!LevelLogger.EMPTY.equals(levelLogger)) {
            // 打印响应体  
            String contentType = response.getContentType();
            levelLogger.log("请求返回Type：{}", contentType);
            JSONObject headers = new JSONObject();
            for (String headerName : response.getHeaderNames()) {
                String headerValue = response.getHeader(headerName);
                headers.put(headerName, headerValue);
            }
            levelLogger.log("请求返回Headers：{}", headers);
            try {
                String startedTime = MDC.get(REQUEST_START_TIME_KEY);
                long used = System.currentTimeMillis() - Long.parseLong(startedTime);
                levelLogger.log("请求总耗时：  {}毫秒", used);
            } catch (Exception ignored) {
            }
            levelLogger.log("==================================调用结束=======================================");
        }
    }

}
