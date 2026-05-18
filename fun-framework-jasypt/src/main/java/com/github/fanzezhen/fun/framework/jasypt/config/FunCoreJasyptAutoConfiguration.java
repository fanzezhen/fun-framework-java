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
 * Jasypt 加密模块自动配置类。
 * <p>
 * 根据配置属性 jasypt.encryptor.bean 动态注册加密器：
 * <ul>
 *   <li>funSM2StringEncryptor - 国密 SM2 加密器</li>
 *   <li>funSM4StringEncryptor - 国密 SM4 加密器</li>
 *   <li>funRSAStringEncryptor - RSA 加密器</li>
 * </ul>
 * 用于配置文件中敏感信息的加解密，配合 jasypt-spring-boot 使用。
 */
@Configuration
@EnableConfigurationProperties({FunJasyptEncryptorProperties.class,
        JasyptEncryptorConfigurationProperties.class})
@ComponentScan("com.github.fanzezhen.fun.framework.jasypt")
public class FunCoreJasyptAutoConfiguration {
    /**
     * Fun 框架的 Jasypt 配置属性。
     */
    @Resource
    private FunJasyptEncryptorProperties funJasyptEncryptorProperties;

    /**
     * Jasypt 原生配置属性。
     */
    @Resource
    private JasyptEncryptorConfigurationProperties jasyptEncryptorConfigurationProperties;

    /**
     * 创建国密 SM2 字符串加密器 Bean.
     * <p>
     * 当配置属性 jasypt.encryptor.bean=funSM2StringEncryptor 时生效。
     *
     * @return SM2 加密器实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "jasypt.encryptor", name = "bean",
            havingValue = "funSM2StringEncryptor")
    public FunSM2StringEncryptor funSM2StringEncryptor() {
        return new FunSM2StringEncryptor(funJasyptEncryptorProperties, jasyptEncryptorConfigurationProperties);
    }

    /**
     * 创建国密 SM4 字符串加密器 Bean.
     * <p>
     * 当配置属性 jasypt.encryptor.bean=funSM4StringEncryptor 时生效。
     *
     * @return SM4 加密器实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "jasypt.encryptor", name = "bean",
            havingValue = "funSM4StringEncryptor")
    public FunSM4StringEncryptor funSM4StringEncryptor() {
        return new FunSM4StringEncryptor(funJasyptEncryptorProperties, jasyptEncryptorConfigurationProperties);
    }

    /**
     * 创建 RSA 字符串加密器 Bean.
     * <p>
     * 当配置属性 jasypt.encryptor.bean=funRSAStringEncryptor 时生效。
     *
     * @return RSA 加密器实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "jasypt.encryptor", name = "bean",
            havingValue = "funRSAStringEncryptor")
    public FunRSAStringEncryptor funRSAStringEncryptor() {
        return new FunRSAStringEncryptor(funJasyptEncryptorProperties, jasyptEncryptorConfigurationProperties);
    }
}
