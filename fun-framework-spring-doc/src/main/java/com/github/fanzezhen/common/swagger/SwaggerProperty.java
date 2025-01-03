package com.github.fanzezhen.fun.framework.swagger;

import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zezhen.fan
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.github.fanzezhen.fun.framework.swagger")
public class SwaggerProperty {
    private boolean headerParameterCommonDisabled;
    private List<Parameter> headerParameterList;
}
