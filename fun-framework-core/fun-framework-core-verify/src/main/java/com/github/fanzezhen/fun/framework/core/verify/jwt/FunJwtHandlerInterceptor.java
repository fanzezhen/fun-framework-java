package com.github.fanzezhen.fun.framework.core.verify.jwt;

import com.github.fanzezhen.fun.framework.core.verify.FunCoreVerifyProperties;
import com.github.fanzezhen.fun.framework.core.verify.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;

/**
 * JWT拦截器
 *
 * @author fanzezhen
 */
@Component
@ConditionalOnBean(JwtService.class)
public class FunJwtHandlerInterceptor implements HandlerInterceptor {
    @Resource
    private JwtService jwtService;
    @Resource
    private FunCoreVerifyProperties funCoreVerifyProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        FunCoreVerifyProperties.Jwt jwt = funCoreVerifyProperties.getJwt();
        if (PatternMatchUtils.simpleMatch(jwt.getIgnoreUris().toArray(new String[]{}), request.getRequestURI())) {
            return true;
        }
        String token = request.getHeader(jwt.getHeader().getToken());
        String timestamp = request.getHeader(jwt.getHeader().getTimestamp());
        jwtService.checkToken(token, timestamp);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
