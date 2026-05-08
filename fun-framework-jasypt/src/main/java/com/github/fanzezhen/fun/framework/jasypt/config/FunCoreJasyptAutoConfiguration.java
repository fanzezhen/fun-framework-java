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
 * Jasypt加密模块自动配置类
 * <p>
 * 根据配置属性 jasypt.encryptor.bean 动态注册加密器：
 * <ul>
 *   <li>funSM2StringEncryptor - 国密SM2加密器</li>
 *   <li>funSM4StringEncryptor - 国密SM4加密器</li>
 *   <li>funRSAStringEncryptor - RSA加密器</li>
 * </ul>
 * 用于配置文件中敏感信息的加解密，配合jasypt-spring-boot使用。
 *
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
