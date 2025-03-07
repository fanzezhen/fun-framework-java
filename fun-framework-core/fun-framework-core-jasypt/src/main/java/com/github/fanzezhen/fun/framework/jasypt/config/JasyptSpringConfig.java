package com.github.fanzezhen.fun.framework.jasypt.config;

import com.github.fanzezhen.fun.framework.jasypt.encryptor.SM2StringEncryptor;
import com.github.fanzezhen.fun.framework.jasypt.encryptor.SM4StringEncryptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zezhen.fan
 */
@Configuration
@ComponentScan("com.github.fanzezhen.fun.framework.jasypt")
@ConfigurationPropertiesScan("com.github.fanzezhen.fun.framework.jasypt")
@ConditionalOnProperty(name = {"com.github.fanzezhen.fun.framework.jasypt.encryptor.bootstrap"}, havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties
public class JasyptSpringConfig {

    @Bean(name = "sm2ECBStringEncryptor")
    @ConditionalOnProperty(prefix = "com.github.fanzezhen.fun.framework.jasypt", name = "type", havingValue = "SM2")
    public SM2StringEncryptor sm2ECBStringEncryptor() {
        return new SM2StringEncryptor();
    }

    @Bean(name = "sm4ECBStringEncryptor")
    @ConditionalOnProperty(prefix = "com.github.fanzezhen.fun.framework.jasypt", name = "type", havingValue = "SM4")
    public SM4StringEncryptor sm4ECBStringEncryptor() {
        return new SM4StringEncryptor();
    }
}
