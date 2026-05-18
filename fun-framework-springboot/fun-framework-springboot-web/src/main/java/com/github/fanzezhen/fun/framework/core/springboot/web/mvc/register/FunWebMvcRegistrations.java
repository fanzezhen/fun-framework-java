package com.github.fanzezhen.fun.framework.core.springboot.web.mvc.register;

import com.github.fanzezhen.fun.framework.core.springboot.web.FunSpringbootWebProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.Resource;

/**
 * Web MVC注册配置类.
 * <p>
 * 提供自定义的RequestMappingHandlerMapping实现，
 * 支持根据配置选择性注册接口。
 * 仅在配置了fun.core.web.register.flag时生效。
 */
@Component
@ConditionalOnExpression("${fun.core.web.register.flag:null} != null")
public class FunWebMvcRegistrations implements WebMvcRegistrations {
    /**
     * Web配置属性.
     */
    @Resource
    private FunSpringbootWebProperties funSpringbootWebProperties;

    /**
     * 获取自定义的RequestMappingHandlerMapping.
     *
     * @return 自定义的RequestMappingHandlerMapping实例
     */
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new FunRequestMappingHandlerMapping(funSpringbootWebProperties.getRegister());
    }
}
