package com.github.fanzezhen.fun.framework.core.springboot.web.jwt;


import com.github.fanzezhen.fun.framework.core.springboot.web.FunSpringbootWebProperties;
import com.github.fanzezhen.fun.framework.core.springboot.web.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;

/**
 * JWT拦截器
 * <p>
 * 拦截HTTP请求并校验JWT令牌的有效性，支持通过配置忽略指定URI。
 * 仅在容器中存在JwtService时生效。
 * <p>
 * <b>执行时机：</b>preHandle阶段，在Controller方法执行前校验令牌
 * <p>
 * <b>性能考虑：</b>忽略URI采用PatternMatchUtils.simpleMatch进行快速匹配，避免正则开销
 *
 */
@Component
@ConditionalOnBean(JwtService.class)
public class FunJwtHandlerInterceptor implements HandlerInterceptor {
    @Resource
    private JwtService jwtService;
    @Resource
    private FunSpringbootWebProperties funSpringbootWebProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        FunSpringbootWebProperties.Jwt jwt = funSpringbootWebProperties.getJwt();
        if (PatternMatchUtils.simpleMatch(jwt.getIgnoreUris().toArray(new String[]{}), request.getRequestURI())) {
            return true;
        }
        String token = request.getHeader(jwt.getHeader().getToken());
        String timestamp = request.getHeader(jwt.getHeader().getTimestamp());
        jwtService.checkToken(token, timestamp);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
