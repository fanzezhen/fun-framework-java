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
 * MapperFacadeUtil 自动配置
 * <p>
 * 在 Spring 容器初始化完成后，自动将 MapperFacade 实例注入到 MapperFacadeUtil 工具类中，
 * 使其能够在任何地方通过静态方法调用进行对象映射。
 * </p>
 *
 * @since 4.0.5
 */
@Configuration
@EnableConfigurationProperties(FunSpringbootWebProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.springboot.web")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class FunCoreSpringbootWebAutoConfiguration {
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
    @Bean
    @ConditionalOnMissingBean(value = JwtService.class)
    public JwtService funDefaultJwtService(FunSpringbootWebProperties funCoreVerifyProperties) {
        return new FunDefaultJwtServiceImpl(funCoreVerifyProperties);
    }
}
