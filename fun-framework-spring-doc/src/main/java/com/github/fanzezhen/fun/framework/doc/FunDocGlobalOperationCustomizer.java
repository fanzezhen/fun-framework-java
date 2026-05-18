package com.github.fanzezhen.fun.framework.doc;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.core.context.properties.ContextConstant;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * Fun 框架文档全局操作定制器.
 *
 * <p>为所有 API 操作添加全局的 Header 参数，包括框架默认的上下文参数和用户自定义参数
 */
@Component
public class FunDocGlobalOperationCustomizer implements GlobalOperationCustomizer {
    /**
     * Header 参数类型常量.
     */
    static final String HEADER = "header";

    /**
     * 文档配置属性.
     */
    @Resource
    private FunDocProperties funDocProperties;

    /**
     * 定制 API 操作，添加全局 Header 参数.
     *
     * @param operation     OpenAPI 操作对象
     * @param handlerMethod 处理器方法
     * @return 定制后的操作对象
     */
    @Override
    public Operation customize(final Operation operation,
                               final HandlerMethod handlerMethod) {
        if (funDocProperties.getHeader() != null) {
            List<Parameter> headerParameterList =
                    funDocProperties.getHeader().getParameters();
            if (CollUtil.isNotEmpty(headerParameterList)) {
                headerParameterList.forEach(operation::addParametersItem);
            }
            if (!funDocProperties.getHeader()
                    .isDisabledFunDefaultParameter()) {
                operation.addParametersItem(
                        new Parameter().in(HEADER).name(
                                ContextConstant.DEFAULT_HEADER_TOKEN));
                operation.addParametersItem(
                        new Parameter().in(HEADER).name(
                                ContextConstant.DEFAULT_HEADER_LOCALE));
                operation.addParametersItem(
                        new Parameter().in(HEADER).name(
                                ContextConstant.DEFAULT_HEADER_ACCOUNT_ID));
                operation.addParametersItem(
                        new Parameter().in(HEADER).name(
                                ContextConstant.DEFAULT_HEADER_ACCOUNT_NAME));
                operation.addParametersItem(
                        new Parameter().in(HEADER).name(
                                ContextConstant.DEFAULT_HEADER_USER_ID));
                operation.addParametersItem(
                        new Parameter().in(HEADER).name(
                                ContextConstant.DEFAULT_HEADER_USER_NAME));
                operation.addParametersItem(
                        new Parameter().in(HEADER).name(
                                ContextConstant.DEFAULT_HEADER_USER_IP));
                operation.addParametersItem(
                        new Parameter().in(HEADER).name(
                                ContextConstant.DEFAULT_HEADER_APP_CODE));
                operation.addParametersItem(
                        new Parameter().in(HEADER).name(
                                ContextConstant.DEFAULT_HEADER_TENANT_ID));
                operation.addParametersItem(
                        new Parameter().in(HEADER).name(
                                ContextConstant.DEFAULT_HEADER_PROJECT_ID));
            }
        }
        return operation;
    }
}
