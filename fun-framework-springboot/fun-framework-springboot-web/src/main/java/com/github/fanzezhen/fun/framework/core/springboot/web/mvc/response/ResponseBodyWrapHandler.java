package com.github.fanzezhen.fun.framework.core.springboot.web.mvc.response;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.springboot.web.FunSpringbootWebProperties;
import org.springframework.core.MethodParameter;
import jakarta.annotation.Nonnull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;


/**
 * 响应体统一包装处理器.
 * <p>
 * 通过代理原生的HandlerMethodReturnValueHandler，在返回值序列化前自动包装为统一格式（如ActionResult）。
 * 支持通过配置忽略特定URL（如/actuator、/swagger-ui等）。
 * <p>
 * <b>执行时机：</b>Controller方法执行完成后，ResponseBody序列化前
 */
public class ResponseBodyWrapHandler implements HandlerMethodReturnValueHandler {

    /**
     * URL查询参数分隔符.
     */
    private static final String QUERY_SEPARATOR = "?";

    /**
     * 委托的返回值处理器.
     */
    private final HandlerMethodReturnValueHandler delegate;

    /**
     * 响应包装器.
     */
    private final ResponseBodyWrapper responseBodyWrapper;

    /**
     * Web配置属性.
     */
    private final FunSpringbootWebProperties funSpringbootWebProperties;

    /**
     * 构造函数.
     *
     * @param delegate 委托的返回值处理器
     * @param responseBodyWrapper 响应包装器
     * @param funSpringbootWebProperties Web配置属性
     */
    public ResponseBodyWrapHandler(final HandlerMethodReturnValueHandler delegate,
                                   final ResponseBodyWrapper responseBodyWrapper,
                                   final FunSpringbootWebProperties funSpringbootWebProperties) {
        this.delegate = delegate;
        this.responseBodyWrapper = responseBodyWrapper;
        this.funSpringbootWebProperties = funSpringbootWebProperties;
    }

    /**
     * 判断是否支持该返回类型.
     *
     * @param returnType 返回类型
     * @return true表示支持
     */
    @Override
    public boolean supportsReturnType(@Nonnull final MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    /**
     * 处理返回值，自动包装为统一格式.
     *
     * @param returnValue 返回值
     * @param returnType 返回类型
     * @param mavContainer ModelAndView容器
     * @param webRequest Web请求
     * @throws Exception 处理异常
     */
    @Override
    public void handleReturnValue(final Object returnValue,
                                  @Nonnull final MethodParameter returnType,
                                  @Nonnull final ModelAndViewContainer mavContainer,
                                  @Nonnull final NativeWebRequest webRequest) throws Exception {
        if (responseBodyWrapper.isWrapped(returnValue) ||
            funSpringbootWebProperties.getResponseWrapper() == null ||
            !funSpringbootWebProperties.getResponseWrapper().enabled()) {
            delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            return;
        }
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request != null) {
            String requestUri = request.getRequestURI();
            if (CharSequenceUtil.contains(requestUri, QUERY_SEPARATOR)) {
                requestUri = requestUri.substring(0, requestUri.indexOf(QUERY_SEPARATOR));
            }
            // 对特殊的URL不进行统一包装结果处理
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            String finalRequestUri = requestUri;
            if (funSpringbootWebProperties.getResponseWrapper().getIgnorePaths()
                .stream().noneMatch(ignore -> antPathMatcher.match(ignore, finalRequestUri))) {
                delegate.handleReturnValue(responseBodyWrapper.wrap(returnValue), returnType,
                        mavContainer, webRequest);
            } else {
                delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            }
        }
    }
}
