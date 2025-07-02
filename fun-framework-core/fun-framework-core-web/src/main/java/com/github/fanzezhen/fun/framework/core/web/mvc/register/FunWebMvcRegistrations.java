package com.github.fanzezhen.fun.framework.core.web.mvc.register;

import com.github.fanzezhen.fun.framework.core.web.FunCoreWebProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.Resource;

/**
 * 接口注册
 * @author fanzezhen
 */
@Component
@ConditionalOnExpression("${fun.core.web.register.flag:null} != null")
public class FunWebMvcRegistrations implements WebMvcRegistrations {
    @Resource
    private FunCoreWebProperties funCoreWebProperties;

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new FunRequestMappingHandlerMapping(funCoreWebProperties.getRegister());
    }
}
