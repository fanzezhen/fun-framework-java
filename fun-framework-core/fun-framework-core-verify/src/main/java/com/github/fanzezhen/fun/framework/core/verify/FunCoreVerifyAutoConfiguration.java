package com.github.fanzezhen.fun.framework.core.verify;

import com.github.fanzezhen.fun.framework.core.verify.jwt.service.FunDefaultJwtServiceImpl;
import com.github.fanzezhen.fun.framework.core.verify.jwt.service.JwtService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanzezhen
 */
@Configuration
@EnableConfigurationProperties(FunCoreVerifyProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.verify")
@ConfigurationPropertiesScan("com.github.fanzezhen.fun.framework.core.verify")
public class FunCoreVerifyAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(value = JwtService.class)
    public JwtService funDefaultJwtService(FunCoreVerifyProperties funCoreVerifyProperties) {
        return new FunDefaultJwtServiceImpl(funCoreVerifyProperties);
    }
}
