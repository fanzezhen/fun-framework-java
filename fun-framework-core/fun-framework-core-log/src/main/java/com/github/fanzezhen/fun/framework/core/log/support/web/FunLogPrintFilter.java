package com.github.fanzezhen.fun.framework.core.log.support.web;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 打印日志
 *
 * @author fanzezhen
 * @since 3.1.7
 */
@Slf4j
@Aspect
@Component
@WebFilter(filterName = "funLogPrintFilter", urlPatterns = "/*")
@SuppressWarnings("unused")
@Order(Short.MIN_VALUE + 1)
public class FunLogPrintFilter implements Filter {
    private static final LevelLogger debugLogger = log::debug;
    private static final LevelLogger infoLogger = log::info;
    private static final LevelLogger warnLogger = log::warn;
    private static final LevelLogger errorLogger = log::error;
    private static final LevelLogger traceLogger = log::trace;
    private static LevelLogger levelLogger;
    private static final String REQUEST_START_TIME_KEY = "REQUEST_START_TIME_KEY";

    @Value("${fun.core.log.print-level:DEBUG}")
    private Level level;

    @Resource
    private List<ArgResolve> argResolves;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) " +
        "|| @within(org.springframework.stereotype.Controller)")
    public void webExecutePointcut() {
    }

    @Around("webExecutePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (Objects.isNull(levelLogger)) {
            return joinPoint.proceed(joinPoint.getArgs());
        }
        levelLogger.log("入参：{}", resolveArgs(joinPoint.getArgs()));
        final Object object = joinPoint.proceed(joinPoint.getArgs());
        levelLogger.log("出参：{}", resolveArg(object));
        return object;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest httpServletRequest && !Objects.isNull(levelLogger)) {
            preHandle(httpServletRequest, (HttpServletResponse) servletResponse, null);
            filterChain.doFilter(httpServletRequest, servletResponse);
            postHandle(httpServletRequest, (HttpServletResponse) servletResponse, null, null);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler) {
        if (!Objects.isNull(levelLogger)) {
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

    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (!Objects.isNull(levelLogger)) {
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


    private String resolveArgs(Object... args) {
        if (Objects.isNull(args) || args.length == 0) {
            return "";
        }
        if (args.length == 1) {
            return resolveArg(args[0]);
        } else {
            String[] strArr = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                strArr[i] = resolveArg(args[i]);
            }
            return JSON.toJSONString(strArr);
        }
    }


    private String resolveArg(Object arg) {
        for (ArgResolve argResolve : argResolves) {
            if (argResolve.isSupport(arg)) {
                return argResolve.resolve(arg);
            }
        }
        return "'不支持的类型：" + arg.getClass().getName() + "'";
    }

    interface ArgResolve {
        boolean isSupport(Object o);

        String resolve(Object o);
    }

    /**
     * 默认不做处理
     */
    @Order
    @Component
    static class DefaultArgResolve implements ArgResolve {

        private final Set<Class<?>> toStringClassSet = ConcurrentHashMap.newKeySet();

        @Override
        public boolean isSupport(Object o) {
            return true;
        }

        /**
         * 优先json转，如果失败则直接toString
         */
        @Override
        public String resolve(Object o) {
            if (o == null) {
                return null;
            }
            if (toStringClassSet.contains(o.getClass())) {
                return o.toString();
            }
            //如果不能用json，就直接toString
            try {
                return JSON.toJSONString(o);
            } catch (Exception e) {
                log.warn("此对象不能用fastJson序列化 ：class {} 对象 {} ", o.getClass(), o);
                toStringClassSet.add(o.getClass());
                return o.toString();
            }
        }

    }

    @Order(Short.MAX_VALUE-4)
    @Component
    static class IoArgResolve implements ArgResolve {

        @Override
        public boolean isSupport(Object o) {
            if (Objects.isNull(o)) {
                return false;
            }
            final Class<?> aClass = o.getClass();
            return InputStream.class.isAssignableFrom(aClass) || Reader.class.isAssignableFrom(aClass);
        }

        @Override
        public String resolve(Object o) {
            return "'不支持的类型：InputStream'";
        }

    }

    @Order(Short.MAX_VALUE-3)
    @Component
    static class PartArgResolve extends DefaultArgResolve {

        @Override
        public boolean isSupport(Object o) {
            return Part.class.isAssignableFrom(o.getClass());
        }

        @Override
        public String resolve(Object o) {
            Part p = (Part) o;
            return super.resolve(new FileInfo(p.getName(), p.getSubmittedFileName(), p.getSize()));
        }

    }

    @Component
    @Order(Short.MAX_VALUE-2)
    static class MultipartFileArgResolve extends DefaultArgResolve {

        @Override
        public boolean isSupport(Object o) {
            return MultipartFile.class.isAssignableFrom(o.getClass());
        }

        @Override
        public String resolve(Object o) {
            MultipartFile file = (MultipartFile) o;
            return super.resolve(new FileInfo(file.getName(), file.getOriginalFilename(), file.getSize()));
        }
    }

    @Component
    @Order(Short.MAX_VALUE-1)
    static class BytesArgResolve extends DefaultArgResolve {

        @Override
        public boolean isSupport(Object o) {
            return o instanceof byte[];
        }

        @Override
        public String resolve(Object o) {
            return "'不支持的类型：byte[]'";
        }
    }

    @Component
    @Order(Short.MAX_VALUE)
    static class CharSequenceArgResolve extends DefaultArgResolve {

        @Override
        public boolean isSupport(Object o) {
            return o instanceof CharSequence;
        }

        @Override
        public String resolve(Object o) {
            return o.toString();
        }
    }

    @Data
    static class FileInfo {

        private static final String DESC = "参数为文件类型，只打印文件描述信息";
        private String name;
        private String fileName;
        private long size;

        public FileInfo(String name, String fileName, long size) {
            this.name = name;
            this.fileName = fileName;
            this.size = size;
        }

        public String getDesc() {
            return DESC;
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
        FunLogPrintFilter.levelLogger = levelLogger;
    }

    @PostConstruct
    public void init() {
        setLevelLogger(findLevelLogger());
    }

}
