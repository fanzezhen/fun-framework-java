package com.github.fanzezhen.fun.framework.jasypt.encryptor;

import com.github.fanzezhen.fun.framework.jasypt.config.FunJasyptEncryptorProperties;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;

/**
 * 字符串加密器抽象基类，封装Jasypt加密配置的公共部分
 * <p>
 * 子类实现 {@link #encrypt} 和 {@link #decrypt} 方法定义具体的加密算法，
 * 如AES、RSA等，配置通过构造函数注入避免重复声明。
 *
 */
@Slf4j
public abstract class AbstractStringEncryptor implements StringEncryptor {

    protected final FunJasyptEncryptorProperties funJasyptEncryptorProperties;

    protected final JasyptEncryptorConfigurationProperties jasyptEncryptorConfigurationProperties;

    protected AbstractStringEncryptor(FunJasyptEncryptorProperties funJasyptEncryptorProperties, 
                                   JasyptEncryptorConfigurationProperties jasyptEncryptorConfigurationProperties) {
        this.funJasyptEncryptorProperties = funJasyptEncryptorProperties;
        this.jasyptEncryptorConfigurationProperties = jasyptEncryptorConfigurationProperties;
    }
}
