package com.github.fanzezhen.fun.framework.core.springboot.web.mvc.register;

import com.github.fanzezhen.fun.framework.core.springboot.web.FunSpringbootWebProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.Resource;

/**
 * 接口注册
 */
@Component
@ConditionalOnExpression("${fun.core.web.register.flag:null} != null")
public class FunWebMvcRegistrations implements WebMvcRegistrations {
    @Resource
    private FunSpringbootWebProperties funSpringbootWebProperties;

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new FunRequestMappingHandlerMapping(funSpringbootWebProperties.getRegister());
    }
}
