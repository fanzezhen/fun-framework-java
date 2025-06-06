package com.github.fanzezhen.fun.framework.core.jasypt.config;

import com.github.fanzezhen.fun.framework.core.jasypt.encryptor.SM2StringEncryptor;
import com.github.fanzezhen.fun.framework.core.jasypt.encryptor.SM4StringEncryptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author fanzezhen
 */
@Configuration
@EnableConfigurationProperties(FunJasyptEncryptorProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.core.jasypt")
public class FunCoreJasyptAutoConfiguration {
    @Resource
    private FunJasyptEncryptorProperties funJasyptEncryptorProperties;

    @Bean(name = "sm2ECBStringEncryptor")
    @ConditionalOnProperty(prefix = "com.github.fanzezhen.fun.framework.core.jasypt", name = "type", havingValue = "SM2")
    public SM2StringEncryptor sm2ECBStringEncryptor() {
        return new SM2StringEncryptor(funJasyptEncryptorProperties.getASymmetric());
    }

    @Bean(name = "sm4ECBStringEncryptor")
    @ConditionalOnProperty(prefix = "com.github.fanzezhen.fun.framework.core.jasypt", name = "type", havingValue = "SM4")
    public SM4StringEncryptor sm4ECBStringEncryptor() {
        return new SM4StringEncryptor(funJasyptEncryptorProperties.getSymmetric());
    }
}
