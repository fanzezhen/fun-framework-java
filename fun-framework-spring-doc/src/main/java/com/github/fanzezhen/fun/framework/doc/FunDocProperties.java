package com.github.fanzezhen.fun.framework.doc;

import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fun 框架文档配置属性.
 *
 * <p>用于配置 SpringDoc 文档的全局参数，包括 Header 参数的启用/禁用和自定义参数列表
 */
@Data
@Component
@ConfigurationProperties(prefix = "fun.spring-doc")
public class FunDocProperties {
    /**
     * Header 参数配置.
     */
    private Header header;

    /**
     * Header 参数配置类.
     *
     * <p>用于配置全局 Header 参数的启用状态和自定义参数列表
     */
    @Data
    public static class Header {
        /**
         * 是否禁用 Fun 框架默认的 Header 参数.
         */
        private boolean disabledFunDefaultParameter;

        /**
         * 自定义的 Header 参数列表.
         */
        private List<Parameter> parameters;
    }
}
