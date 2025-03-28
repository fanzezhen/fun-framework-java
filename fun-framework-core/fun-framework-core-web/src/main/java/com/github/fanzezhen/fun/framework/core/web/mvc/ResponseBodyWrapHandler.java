package com.github.fanzezhen.fun.framework.core.web.mvc;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.web.FunCoreWebProperties;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;


/**
 * @author fanzezhen
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
    public boolean supportsReturnType(@NonNull MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(@Nullable Object returnValue,
                                  @NonNull MethodParameter returnType,
                                  @NonNull ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest) throws Exception {
        if (responseBodyWrapper.isWrapped(returnValue)) {
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
            if (funCoreWebProperties.getResponseIgnoreWrapUrls().stream().noneMatch(ignore -> antPathMatcher.match(ignore, finalRequestUri))) {
                delegate.handleReturnValue(responseBodyWrapper.wrap(returnValue), returnType, mavContainer, webRequest);
            } else {
                delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            }
        }
    }
}
