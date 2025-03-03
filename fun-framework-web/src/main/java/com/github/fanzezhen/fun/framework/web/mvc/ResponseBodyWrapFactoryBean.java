package com.github.fanzezhen.fun.framework.web.mvc;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.core.property.CommonCoreProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zezhen.fan
 */
@Component
public class ResponseBodyWrapFactoryBean implements InitializingBean {

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Resource
    private CommonCoreProperties commonCoreProperties;

    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        requestMappingHandlerAdapter.setReturnValueHandlers(decorateHandlers(returnValueHandlers));
    }

    private List<HandlerMethodReturnValueHandler> decorateHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        if (CollUtil.isEmpty(handlers)) {
            return newHandlers;
        }
        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (handler instanceof RequestResponseBodyMethodProcessor) {
                //用自己的ResponseBody包装类替换掉框架的，达到返回Result的效果
                ResponseBodyWrapHandler decorator =
                        new ResponseBodyWrapHandler(handler, commonCoreProperties);
                newHandlers.add(decorator);
            } else {
                newHandlers.add(handler);
            }
        }
        return newHandlers;
    }

}
