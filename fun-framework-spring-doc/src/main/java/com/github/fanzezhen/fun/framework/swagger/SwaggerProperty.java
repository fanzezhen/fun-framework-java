package com.github.fanzezhen.fun.framework.swagger;

import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fanzezhen
 */
@Data
@Component
@ConfigurationProperties(prefix = "fun.spring-doc")
public class SwaggerProperty {
    private boolean headerParameterCommonDisabled;
    private List<Parameter> headerParameterList;
}
