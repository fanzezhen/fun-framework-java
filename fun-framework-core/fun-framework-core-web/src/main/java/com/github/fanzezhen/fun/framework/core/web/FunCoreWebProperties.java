package com.github.fanzezhen.fun.framework.core.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Set;

/**
 * @author zezhen.fan
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "fun.core.web")
public class FunCoreWebProperties {
    private Set<String> responseIgnoreWrapUrls;

}
