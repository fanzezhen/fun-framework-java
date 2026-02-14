package com.github.fanzezhen.fun.framework.jasypt.encryptor;

import com.github.fanzezhen.fun.framework.jasypt.config.FunJasyptEncryptorProperties;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;

/**
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
