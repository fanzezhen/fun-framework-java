package com.github.fanzezhen.fun.framework.core.web.config;

import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import com.github.fanzezhen.fun.framework.core.web.FunCoreWebProperties;
import com.github.fanzezhen.fun.framework.core.web.mvc.response.ResponseBodyWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Web模块自动配置类
 * <p>
 * 仅在Servlet类型的Web应用中生效，提供统一响应包装、跨域处理、请求日志等功能。
 * 默认注册 {@link ResponseBodyWrapper} 实现，将Controller返回值包装为 {@link ActionResult}。
 *
 */
@Configuration
@EnableConfigurationProperties(FunCoreWebProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.web")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class FunCoreWebAutoConfiguration {
    @Bean
    @Order
    @ConditionalOnMissingBean
    public ResponseBodyWrapper funResponseBodyWrapper() {
        return new ResponseBodyWrapper() {
            /**
             * 是否已经包装过了
             */
            @Override
            public boolean isWrapped(Object data) {
                return data instanceof ActionResult;
            }

            /**
             * 包装
             */
            @Override
            public Object wrap(Object data) {
                return ActionResult.success(data);
            }
        };
    }
}
