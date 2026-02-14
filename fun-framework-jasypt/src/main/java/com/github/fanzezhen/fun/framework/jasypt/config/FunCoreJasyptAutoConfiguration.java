package com.github.fanzezhen.fun.framework.jasypt.config;

import com.github.fanzezhen.fun.framework.jasypt.encryptor.FunRSAStringEncryptor;
import com.github.fanzezhen.fun.framework.jasypt.encryptor.FunSM2StringEncryptor;
import com.github.fanzezhen.fun.framework.jasypt.encryptor.FunSM4StringEncryptor;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;

/**
 * @author fanzezhen
 */
@Configuration
@EnableConfigurationProperties({FunJasyptEncryptorProperties.class, JasyptEncryptorConfigurationProperties.class})
@ComponentScan("com.github.fanzezhen.fun.framework.jasypt")
public class FunCoreJasyptAutoConfiguration {
    @Resource
    private FunJasyptEncryptorProperties funJasyptEncryptorProperties;
    @Resource
    private JasyptEncryptorConfigurationProperties jasyptEncryptorConfigurationProperties;

    @Bean
    @ConditionalOnProperty(prefix = "jasypt.encryptor", name = "bean", havingValue = "funSM2StringEncryptor")
    public FunSM2StringEncryptor funSM2StringEncryptor() {
        return new FunSM2StringEncryptor(funJasyptEncryptorProperties, jasyptEncryptorConfigurationProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "jasypt.encryptor", name = "bean", havingValue = "funSM4StringEncryptor")
    public FunSM4StringEncryptor funSM4StringEncryptor() {
        return new FunSM4StringEncryptor(funJasyptEncryptorProperties, jasyptEncryptorConfigurationProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "jasypt.encryptor", name = "bean", havingValue = "funRSAStringEncryptor")
    public FunRSAStringEncryptor funRSAStringEncryptor() {
        return new FunRSAStringEncryptor(funJasyptEncryptorProperties, jasyptEncryptorConfigurationProperties);
    }
}
