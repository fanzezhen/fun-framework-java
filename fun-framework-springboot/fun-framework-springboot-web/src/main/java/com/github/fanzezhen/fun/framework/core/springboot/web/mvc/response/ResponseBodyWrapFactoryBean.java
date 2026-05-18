package com.github.fanzezhen.fun.framework.core.springboot.web.mvc.response;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.core.springboot.web.FunSpringbootWebProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 响应包装处理器工厂Bean.
 * <p>
 * 在Spring容器初始化完成后，替换默认的RequestResponseBodyMethodProcessor，
 * 实现Controller返回值的自动包装功能。
 */
@Component
public class ResponseBodyWrapFactoryBean implements InitializingBean {

    /**
     * 请求映射处理器适配器.
     */
    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    /**
     * Web配置属性.
     */
    @Resource
    private FunSpringbootWebProperties funSpringbootWebProperties;

    /**
     * 响应包装器.
     */
    @Resource
    private ResponseBodyWrapper responseBodyWrapper;

    /**
     * 属性设置完成后的回调，替换返回值处理器.
     */
    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers =
                requestMappingHandlerAdapter.getReturnValueHandlers();
        requestMappingHandlerAdapter.setReturnValueHandlers(decorateHandlers(returnValueHandlers));
    }

    /**
     * 装饰返回值处理器列表，将RequestResponseBodyMethodProcessor替换为自定义实现.
     *
     * @param handlers 原始处理器列表
     * @return 装饰后的处理器列表
     */
    private List<HandlerMethodReturnValueHandler> decorateHandlers(
            final List<HandlerMethodReturnValueHandler> handlers) {
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        if (CollUtil.isEmpty(handlers)) {
            return newHandlers;
        }
        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (handler instanceof RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor) {
                // 用自己的ResponseBody包装类替换掉框架的，达到返回Result的效果
                ResponseBodyWrapHandler decorator = new ResponseBodyWrapHandler(requestResponseBodyMethodProcessor,
                        responseBodyWrapper, funSpringbootWebProperties);
                newHandlers.add(decorator);
            } else {
                newHandlers.add(handler);
            }
        }
        return newHandlers;
    }

}
