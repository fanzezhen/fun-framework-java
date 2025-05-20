package com.github.fanzezhen.fun.framework.swagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 */
@Configuration
public class CommonSwaggerConfig {

    private Info info() {
        return new Info();
    }

    @Bean
    public OpenAPI springOpenApi() {
        return new OpenAPI().info(this.info());
    }
}
