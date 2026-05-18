package com.github.fanzezhen.fun.framework.jasypt.encryptor;

import com.github.fanzezhen.fun.framework.jasypt.config.FunJasyptEncryptorProperties;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;

/**
 * 字符串加密器抽象基类，封装 Jasypt 加密配置的公共部分.
 * <p>
 * 子类实现 {@link #encrypt} 和 {@link #decrypt} 方法定义具体的加密算法，
 * 如 AES、RSA 等，配置通过构造函数注入避免重复声明.
 */
@Slf4j
public abstract class AbstractStringEncryptor implements StringEncryptor {

    /**
     * Fun 框架的 Jasypt 配置属性.
     */
    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected final FunJasyptEncryptorProperties funJasyptEncryptorProperties;

    /**
     * Jasypt 原生配置属性.
     */
    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected final JasyptEncryptorConfigurationProperties jasyptEncryptorConfigurationProperties;

    /**
     * 构造加密器实例.
     *
     * @param funJasyptEncryptorProperties Fun 框架的 Jasypt 配置属性
     * @param jasyptEncryptorConfigurationProperties Jasypt 原生配置属性
     */
    protected AbstractStringEncryptor(
            final FunJasyptEncryptorProperties funJasyptEncryptorProperties,
            final JasyptEncryptorConfigurationProperties jasyptEncryptorConfigurationProperties) {
        this.funJasyptEncryptorProperties = funJasyptEncryptorProperties;
        this.jasyptEncryptorConfigurationProperties = jasyptEncryptorConfigurationProperties;
    }
}
