package com.github.fanzezhen.fun.framework.swagger.config;

import cn.hutool.core.collection.CollUtil;
import com.github.fanzezhen.fun.framework.core.constant.SysConstant;
import com.github.fanzezhen.fun.framework.swagger.SwaggerProperty;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanzezhen
 */
@Component
public class CommonGlobalOperationCustomizer implements GlobalOperationCustomizer {
    static final String HEADER = "header";
    @Resource
    private SwaggerProperty swaggerProperty;
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        List<Parameter> headerParameterList = swaggerProperty.getHeaderParameterList();
        if (!swaggerProperty.isHeaderParameterCommonDisabled()){
            operation.addParametersItem(new Parameter().in(HEADER).name(SysConstant.HEADER_TOKEN));
            operation.addParametersItem(new Parameter().in(HEADER).name(SysConstant.HEADER_LOCALE));
            operation.addParametersItem(new Parameter().in(HEADER).name(SysConstant.HEADER_ACCOUNT_ID));
            operation.addParametersItem(new Parameter().in(HEADER).name(SysConstant.HEADER_ACCOUNT_NAME));
            operation.addParametersItem(new Parameter().in(HEADER).name(SysConstant.HEADER_USER_ID));
            operation.addParametersItem(new Parameter().in(HEADER).name(SysConstant.HEADER_USER_NAME));
            operation.addParametersItem(new Parameter().in(HEADER).name(SysConstant.HEADER_USER_IP));
            operation.addParametersItem(new Parameter().in(HEADER).name(SysConstant.HEADER_APP_CODE));
            operation.addParametersItem(new Parameter().in(HEADER).name(SysConstant.HEADER_TENANT_ID));
            operation.addParametersItem(new Parameter().in(HEADER).name(SysConstant.HEADER_PROJECT_ID));
        }
        if (CollUtil.isNotEmpty(headerParameterList)) {
            headerParameterList.forEach(operation::addParametersItem);
        }
        return operation;
    }
}
