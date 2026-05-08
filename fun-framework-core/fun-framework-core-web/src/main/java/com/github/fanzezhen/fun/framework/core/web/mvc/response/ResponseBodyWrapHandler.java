package com.github.fanzezhen.fun.framework.core.web.mvc.response;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.web.FunCoreWebProperties;
import org.springframework.core.MethodParameter;
import jakarta.annotation.Nonnull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;


/**
 * 响应体统一包装处理器
 * <p>
 * 通过代理原生的HandlerMethodReturnValueHandler，在返回值序列化前自动包装为统一格式（如ActionResult）。
 * 支持通过配置忽略特定URL（如/actuator、/swagger-ui等）。
 * <p>
 * <b>执行时机：</b>Controller方法执行完成后，ResponseBody序列化前
 *
 */
public class ResponseBodyWrapHandler implements HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;
    private final ResponseBodyWrapper responseBodyWrapper;
    private final FunCoreWebProperties funCoreWebProperties;

    public ResponseBodyWrapHandler(HandlerMethodReturnValueHandler delegate,
                                   ResponseBodyWrapper responseBodyWrapper,
                                   FunCoreWebProperties funCoreWebProperties) {
        this.delegate = delegate;
        this.responseBodyWrapper = responseBodyWrapper;
        this.funCoreWebProperties = funCoreWebProperties;
    }

    @Override
    public boolean supportsReturnType(@Nonnull MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  @Nonnull MethodParameter returnType,
                                  @Nonnull ModelAndViewContainer mavContainer,
                                  @Nonnull NativeWebRequest webRequest) throws Exception {
        if (responseBodyWrapper.isWrapped(returnValue) || 
            funCoreWebProperties.getResponseWrapper() == null ||
            !funCoreWebProperties.getResponseWrapper().enabled()) {
            delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            return;
        }
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request != null) {
            String requestUri = request.getRequestURI();
            String separator = "?";
            if (CharSequenceUtil.contains(requestUri, separator)) {
                requestUri = requestUri.substring(0, requestUri.indexOf(separator));
            }
            // 对特殊的URL不进行统一包装结果处理
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            String finalRequestUri = requestUri;
            if (funCoreWebProperties.getResponseWrapper().getIgnorePaths()
                .stream().noneMatch(ignore -> antPathMatcher.match(ignore, finalRequestUri))) {
                delegate.handleReturnValue(responseBodyWrapper.wrap(returnValue), returnType, mavContainer, webRequest);
            } else {
                delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            }
        }
    }
}
