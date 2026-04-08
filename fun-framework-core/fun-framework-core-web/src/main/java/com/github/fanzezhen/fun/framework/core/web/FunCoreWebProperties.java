package com.github.fanzezhen.fun.framework.core.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
 * @author fanzezhen
 */
@Data
@ConfigurationProperties(prefix = "fun.core.web")
public class FunCoreWebProperties {
    private ResponseWrapper responseWrapper;
    private Register register;

    @Data
    public static class Register {
        /**
         * null：不启用此功能；
         * true：注册registerPaths；
         * false：排除registerPaths
         */
        private Boolean flag;
        /**
         * 接口路径，支持通配符
         */
        private String[] paths;
    }
    @Data
    public static class ResponseWrapper {
        /**
         * 是否启用响应结果包装
         */
        private Boolean enabled;
        /**
         * 接口路径，支持通配符
         */
        private Set<String> ignorePaths = Collections.emptySet();

        public boolean enabled() {
            return Boolean.TRUE.equals(enabled);
        }
    }
}
