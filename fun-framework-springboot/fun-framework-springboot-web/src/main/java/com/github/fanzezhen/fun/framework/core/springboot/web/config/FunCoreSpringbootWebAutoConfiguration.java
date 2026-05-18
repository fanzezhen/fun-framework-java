package com.github.fanzezhen.fun.framework.core.springboot.web.config;

import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import com.github.fanzezhen.fun.framework.core.springboot.web.FunSpringbootWebProperties;
import com.github.fanzezhen.fun.framework.core.springboot.web.jwt.service.FunDefaultJwtServiceImpl;
import com.github.fanzezhen.fun.framework.core.springboot.web.jwt.service.JwtService;
import com.github.fanzezhen.fun.framework.core.springboot.web.mvc.response.ResponseBodyWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Fun Springboot Web 模块自动配置类.
 * <p>
 * 提供Web层功能的自动配置，包括响应包装、JWT认证等。
 * 仅在Servlet容器环境下生效。
 *
 * @since 4.0.5
 */
@Configuration
@EnableConfigurationProperties(FunSpringbootWebProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.springboot.web")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class FunCoreSpringbootWebAutoConfiguration {
    /**
     * 注册默认的响应包装器.
     * <p>
     * 将Controller返回值包装为ActionResult格式。
     *
     * @return 响应包装器实例
     */
    @Bean
    @Order
    @ConditionalOnMissingBean
    public ResponseBodyWrapper funResponseBodyWrapper() {
        return new ResponseBodyWrapper() {
            /**
             * 判断是否已经包装过.
             *
             * @param data 返回数据
             * @return true表示已包装
             */
            @Override
            public boolean isWrapped(final Object data) {
                return data instanceof ActionResult;
            }

            /**
             * 包装返回数据为ActionResult格式.
             *
             * @param data 原始返回数据
             * @return 包装后的数据
             */
            @Override
            public Object wrap(final Object data) {
                return ActionResult.success(data);
            }
        };
    }

    /**
     * 注册默认的JWT服务实现.
     *
     * @param funCoreVerifyProperties Web配置属性
     * @return JWT服务实例
     */
    @Bean
    @ConditionalOnMissingBean(value = JwtService.class)
    public JwtService funDefaultJwtService(final FunSpringbootWebProperties funCoreVerifyProperties) {
        return new FunDefaultJwtServiceImpl(funCoreVerifyProperties);
    }
}
