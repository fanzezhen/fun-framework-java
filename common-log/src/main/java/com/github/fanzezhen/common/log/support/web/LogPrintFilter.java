package com.github.fanzezhen.common.log.support.web;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.ContentType;
import com.alibaba.fastjson.JSON;
import com.github.fanzezhen.common.core.context.SysContextHolder;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 日志拦截器
 *
 * @author fanzezhen
 * @createTime 2024/2/2 17:25
 * @since 3.1.7
 */
@Slf4j
@WebFilter(filterName = "traceIdFilter", urlPatterns = "/*")
@SuppressWarnings("unused")
@Component
@Order(Integer.MIN_VALUE + 1)
public class LogPrintFilter implements Filter {

    private static final LevelLogger debugLogger = log::debug;
    private static final LevelLogger infoLogger = log::info;
    private static final LevelLogger warnLogger = log::warn;
    private static final LevelLogger errorLogger = log::error;
    private static final LevelLogger traceLogger = log::trace;
    private static LevelLogger levelLogger;
    private static final String REQUEST_START_TIME_KEY = "REQUEST_START_TIME_KEY";
    @Value("${com.github.fanzezhen.common.log.level:DEBUG}")
    private Level level;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest httpServletRequest) {
            HttpServletRequestWrapper requestWrapper = new LoggingHttpServletRequestWrapper(httpServletRequest);
            preHandle(requestWrapper, (HttpServletResponse) servletResponse, null);
            filterChain.doFilter(requestWrapper, servletResponse);
            // 打印响应体  
            String contentType = servletResponse.getContentType();
            levelLogger.log("请求返回Type：{}", contentType);
            if (CharSequenceUtil.containsIgnoreCase(contentType, ContentType.JSON.getValue())) {
                levelLogger.log("请求返回值：  {}", MDC.get("接口返回结果"));
            }
            postHandle(requestWrapper, (HttpServletResponse) servletResponse, null, null);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler) {
        if (!Objects.isNull(levelLogger)) {
            SysContextHolder.set(REQUEST_START_TIME_KEY, System.currentTimeMillis());
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
            try {
                if (CharSequenceUtil.containsIgnoreCase(request.getContentType(), ContentType.JSON.getValue())) {
                    levelLogger.log("请求body：   {}", IoUtil.read(request.getReader()));
                }
            } catch (IOException e) {
                levelLogger.log("读取请求体失败", e);
            }
        }
        return true;
    }

    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (!Objects.isNull(levelLogger)) {
            long starTime = SysContextHolder.getLongOrZero(REQUEST_START_TIME_KEY);
            levelLogger.log("请求总耗时：  {}毫秒", System.currentTimeMillis() - starTime);
            levelLogger.log("==================================调用结束=======================================");
        }
    }

    interface LevelLogger {
        void log(String var1, Object... var2);
    }

    private LevelLogger findLevelLogger() {
        if (Level.DEBUG.equals(level)) {
            return !log.isDebugEnabled() ? null : debugLogger;
        } else if (Level.INFO.equals(level)) {
            return !log.isInfoEnabled() ? null : infoLogger;
        } else if (Level.WARN.equals(level)) {
            return !log.isWarnEnabled() ? null : warnLogger;
        } else if (Level.ERROR.equals(level)) {
            return !log.isErrorEnabled() ? null : errorLogger;
        } else if (Level.TRACE.equals(level)) {
            return !log.isTraceEnabled() ? null : traceLogger;
        }
        return null;
    }

    private static void setLevelLogger(LevelLogger levelLogger) {
        LogPrintFilter.levelLogger = levelLogger;
    }

    @PostConstruct
    public void init() {
        setLevelLogger(findLevelLogger());
    }

}
