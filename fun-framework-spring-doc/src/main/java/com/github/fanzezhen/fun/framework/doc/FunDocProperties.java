package com.github.fanzezhen.fun.framework.doc;

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
public class FunDocProperties {
    /**
     * 是否禁用fun的header参数
     */
    private Header header;
    @Data
    public static class Header{
        private boolean disabledFunDefaultParameter;
        private List<Parameter> parameters;
    }
}
