package com.github.fanzezhen.fun.framework.web.mvc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import com.github.fanzezhen.fun.framework.core.property.CommonCoreProperties;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;


/**
 * @author zezhen.fan
 */
public class ResponseBodyWrapHandler implements HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;
    private final Set<String> autoWrapResponseIgnoreUrlSet;
    private final String[] resourcesFileSuffixArr;

    public ResponseBodyWrapHandler(HandlerMethodReturnValueHandler delegate, CommonCoreProperties commonCoreProperties) {
        this.delegate = delegate;
        this.autoWrapResponseIgnoreUrlSet = CollUtil.newHashSet(commonCoreProperties.getAutoWrapResponseIgnoreUrls().split(StrPool.COMMA));
        this.resourcesFileSuffixArr = commonCoreProperties.getResourcesFileSuffixArr();
    }

    @Override
    public boolean supportsReturnType(@NonNull MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    @SuppressWarnings("all")
    public void handleReturnValue(@Nullable Object returnValue,
                                  @NonNull MethodParameter returnType,
                                  @NonNull ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            delegate.handleReturnValue(new ActionResult<>(), returnType, mavContainer, webRequest);
        } else {
            if (returnValue instanceof ActionResult) {
                delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            } else {
                HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
                // 以下代码已兼容requestUri为空的情况，所以忽略空指针警告
                String requestUri = request.getRequestURI();
                String separator = "?";
                if (CharSequenceUtil.contains(requestUri, separator)) {
                    requestUri = requestUri.substring(0, requestUri.indexOf(separator));
                }
                // 对特殊的URL不进行统一包装结果处理
                AntPathMatcher antPathMatcher = new AntPathMatcher();
                String finalRequestUri = requestUri;
                boolean ignoreWrap = CharSequenceUtil.endWithAnyIgnoreCase(requestUri, resourcesFileSuffixArr) || (
                        autoWrapResponseIgnoreUrlSet != null &&
                                autoWrapResponseIgnoreUrlSet.stream().anyMatch(ignore ->
                                        antPathMatcher.match(ignore, finalRequestUri)));
                if (ignoreWrap) {
                    delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
                } else {
                    delegate.handleReturnValue(ActionResult.success(returnValue), returnType, mavContainer, webRequest);
                }
            }
        }
    }
}
