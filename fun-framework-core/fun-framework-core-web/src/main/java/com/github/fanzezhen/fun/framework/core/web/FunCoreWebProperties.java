package com.github.fanzezhen.fun.framework.core.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * @author fanzezhen
 */
@Data
@ConfigurationProperties(prefix = "fun.core.web")
public class FunCoreWebProperties {
    private Set<String> responseIgnoreWrapUrls;

}
