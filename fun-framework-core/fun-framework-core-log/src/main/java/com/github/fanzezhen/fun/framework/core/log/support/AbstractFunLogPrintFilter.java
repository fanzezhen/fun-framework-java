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
 * 记录 HTTP 请求和响应的抽象基础过滤器.
 * <p>
 * 在处理之前记录请求详细信息（URL、标头、参数），在处理之后记录响应详细信息（标头、耗时）。
 *
 * @since 3.1.7
 */
@Slf4j
@Component
@WebFilter(filterName = "funLogPrintFilter", urlPatterns = "/*")
@SuppressWarnings("unused")
@Order(Short.MIN_VALUE + 1)
public abstract class AbstractFunLogPrintFilter implements Filter {
    /**
     * DEBUG 级别日志记录器.
     */
    private static final LevelLogger DEBUG_LOGGER = log::debug;
    /**
     * INFO 级别日志记录器.
     */
    private static final LevelLogger INFO_LOGGER = log::info;
    /**
     * WARN 级别日志记录器.
     */
    private static final LevelLogger WARN_LOGGER = log::warn;
    /**
     * ERROR 级别日志记录器.
     */
    private static final LevelLogger ERROR_LOGGER = log::error;
    /**
     * TRACE 级别日志记录器.
     */
    private static final LevelLogger TRACE_LOGGER = log::trace;
    /**
     * 请求开始时间的 MDC 键.
     */
    private static final String REQUEST_START_TIME_KEY = "REQUEST_START_TIME_KEY";

    /**
     * 日志辅助实例.
     */
    protected FunLogHelper funLogHelper;

    /**
     * 使用提供的日志辅助构造过滤器.
     *
     * @param logHelper 日志辅助实例
     */
    protected AbstractFunLogPrintFilter(final FunLogHelper logHelper) {
        this.funLogHelper = logHelper;
    }

    /**
     * 获取 fun 日志辅助.
     *
     * @return fun 日志辅助
     */
    protected FunLogHelper getFunLogHelper() {
        return funLogHelper;
    }

    /**
     * 过滤 HTTP 请求，在处理前后记录详细信息.
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
        if (servletRequest instanceof HttpServletRequest httpServletRequest) {
            preHandle(httpServletRequest, (HttpServletResponse) servletResponse, null);
            filterChain.doFilter(httpServletRequest, servletResponse);
            postHandle(httpServletRequest, (HttpServletResponse) servletResponse, null);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    /**
     * 在处理之前记录请求详细信息.
     *
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param handler 处理器对象（可能为 null）
     */
    public void preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
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

    /**
     * 在处理之后记录响应详细信息.
     *
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param handler 处理器对象（可能为 null）
     */
    public void postHandle(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final Object handler) {
        LevelLogger levelLogger = funLogHelper.getOrGenerateLevelLogger(AbstractFunLogPrintFilter.class.getName());
        if (!LevelLogger.EMPTY.equals(levelLogger)) {
            // 打印响应体
            levelLogger.log("请求返回Type：{}", response.getContentType());
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
                // Ignore exception when calculating elapsed time
            }
            levelLogger.log("==================================调用结束=======================================");
        }
    }

}
