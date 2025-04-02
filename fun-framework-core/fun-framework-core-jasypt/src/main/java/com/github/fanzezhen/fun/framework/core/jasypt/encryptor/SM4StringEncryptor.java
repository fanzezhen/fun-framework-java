package com.github.fanzezhen.fun.framework.core.jasypt.encryptor;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.github.fanzezhen.fun.framework.core.jasypt.config.FunJasyptEncryptorProperties;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.commons.CommonUtils;
import org.jasypt.encryption.StringEncryptor;


@Slf4j
public class SM4StringEncryptor implements StringEncryptor {

    private final FunJasyptEncryptorProperties.Symmetric properties;

    public SM4StringEncryptor(FunJasyptEncryptorProperties.Symmetric properties) {
        this.properties = properties;
    }

    @Override
    public String encrypt(String message) {
        CommonUtils.validateNotNull(message, "msg cannot be set null");
        CommonUtils.validateNotNull(properties.getSecretKey(), "salt config not be set");
        try {
            log.debug("加密前配置信息:{}", message);
            SymmetricCrypto sm4 = SmUtil.sm4(properties.getSecretKey().getBytes());
            message = sm4.encryptBase64(message);
            log.debug("加密后配置信息:{}", message);
        } catch (Exception e) {
            log.warn("配置信息加密失败", e);
        }
        return message;
    }

    @Override
    public String decrypt(String encryptedMessage) {
        CommonUtils.validateNotNull(encryptedMessage, "encryptedMessage cannot be set null");
        CommonUtils.validateNotNull(properties.getSecretKey(), "salt config not be set");
        try {
            log.debug("解密前配置信息:{}", encryptedMessage);
            SymmetricCrypto sm4 = SmUtil.sm4(properties.getSecretKey().getBytes());
            encryptedMessage = sm4.decryptStr(encryptedMessage);
            log.debug("解密后配置信息:{}", encryptedMessage);
        } catch (Exception e) {
            log.warn("配置信息解密失败", e);
        }
        return encryptedMessage;
    }

}
