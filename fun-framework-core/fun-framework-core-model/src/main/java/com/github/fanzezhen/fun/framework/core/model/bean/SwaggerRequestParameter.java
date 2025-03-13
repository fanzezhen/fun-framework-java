package com.github.fanzezhen.fun.framework.core.model.bean;

import lombok.Data;

/**
 * @author fanzezhen
 */
@Data
public class SwaggerRequestParameter {
    private String name;
    private int parameterIndex;
    private String description;
    private Boolean required;
    private Boolean deprecated;
    private Boolean hidden;
    private Integer precedence;
}
