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
    private Set<String> responseIgnoreWrapPaths;
    private Register register;

    @Data
    public static class Register {
        /**
         * 接口路径，支持通配符
         */
        private String[] paths;
        /**
         * null：不启用此功能；
         * true：注册registerPaths；
         * false：排除registerPaths
         */
        private Boolean flag;
    }
}
