package com.github.fanzezhen.fun.framework.doc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fun 框架 Swagger 配置类.
 *
 * <p>提供 OpenAPI 规范的基础配置，包括 API 信息和文档元数据
 */
@Configuration
public class FunDocSwaggerConfig {

    /**
     * 创建 API 信息对象.
     *
     * @return API 信息对象
     */
    private Info info() {
        return new Info();
    }

    /**
     * 创建 Spring OpenAPI 配置.
     *
     * @return OpenAPI 配置对象
     */
    @Bean
    public OpenAPI springOpenApi() {
        return new OpenAPI().info(this.info());
    }
}
