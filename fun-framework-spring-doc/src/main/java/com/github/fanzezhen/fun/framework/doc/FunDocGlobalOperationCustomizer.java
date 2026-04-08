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
 * @author fanzezhen
 */
@Component
public class FunDocGlobalOperationCustomizer implements GlobalOperationCustomizer {
    static final String HEADER = "header";
    @Resource
    private FunDocProperties funDocProperties;

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        if (funDocProperties.getHeader() != null) {
            List<Parameter> headerParameterList = funDocProperties.getHeader().getParameters();
            if (CollUtil.isNotEmpty(headerParameterList)) {
                headerParameterList.forEach(operation::addParametersItem);
            }
            if (!funDocProperties.getHeader().isDisabledFunDefaultParameter()) {
                operation.addParametersItem(new Parameter().in(HEADER).name(ContextConstant.DEFAULT_HEADER_TOKEN));
                operation.addParametersItem(new Parameter().in(HEADER).name(ContextConstant.DEFAULT_HEADER_LOCALE));
                operation.addParametersItem(new Parameter().in(HEADER).name(ContextConstant.DEFAULT_HEADER_ACCOUNT_ID));
                operation.addParametersItem(new Parameter().in(HEADER).name(ContextConstant.DEFAULT_HEADER_ACCOUNT_NAME));
                operation.addParametersItem(new Parameter().in(HEADER).name(ContextConstant.DEFAULT_HEADER_USER_ID));
                operation.addParametersItem(new Parameter().in(HEADER).name(ContextConstant.DEFAULT_HEADER_USER_NAME));
                operation.addParametersItem(new Parameter().in(HEADER).name(ContextConstant.DEFAULT_HEADER_USER_IP));
                operation.addParametersItem(new Parameter().in(HEADER).name(ContextConstant.DEFAULT_HEADER_APP_CODE));
                operation.addParametersItem(new Parameter().in(HEADER).name(ContextConstant.DEFAULT_HEADER_TENANT_ID));
                operation.addParametersItem(new Parameter().in(HEADER).name(ContextConstant.DEFAULT_HEADER_PROJECT_ID));
            }
        }
        return operation;
    }
}
