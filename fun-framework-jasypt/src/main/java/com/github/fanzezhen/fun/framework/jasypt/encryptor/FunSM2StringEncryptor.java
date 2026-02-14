package com.github.fanzezhen.fun.framework.jasypt.encryptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.github.fanzezhen.fun.framework.jasypt.config.FunJasyptEncryptorProperties;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FunSM2StringEncryptor extends AbstractStringEncryptor {

    public FunSM2StringEncryptor(FunJasyptEncryptorProperties funJasyptEncryptorProperties,
                                 JasyptEncryptorConfigurationProperties jasyptEncryptorConfigurationProperties) {
        super(funJasyptEncryptorProperties, jasyptEncryptorConfigurationProperties);
    }

    /**
     * 加密
     */
    @Override
    public String encrypt(String message) {
        if (CharSequenceUtil.isEmpty(message)){
            return message;
        }
        final String[] splits = message.split(funJasyptEncryptorProperties.getSeparator());
        String data = splits[0].trim();
        String publicKey = null;
        String privateKey = null;
        if (splits.length >= 3 && CharSequenceUtil.isNotEmpty(splits[2])) {
            privateKey = splits[1];
            publicKey = splits[2];
        }
        if (CharSequenceUtil.isEmpty(publicKey)) {
            publicKey = jasyptEncryptorConfigurationProperties.getPublicKeyString();
        }
        if (CharSequenceUtil.isEmpty(privateKey)) {
            privateKey = jasyptEncryptorConfigurationProperties.getPrivateKeyString();
        }
        try {
            log.debug("加密前配置信息:{}", data);
            SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
            String encryptedBase64 = sm2.encryptBase64(data, KeyType.PublicKey);
            log.debug("加密后配置信息:{}", encryptedBase64);
            return encryptedBase64;
        } catch (Exception e) {
            log.error("配置信息加密失败", e);
            return message;
        }
    }

    /**
     * 解密
     */
    @Override
    public String decrypt(String message) {
        if (CharSequenceUtil.isEmpty(message)) {
            return message;
        }
        final String[] splits = message.split(funJasyptEncryptorProperties.getSeparator());
        String data = splits[0].trim();
        String publicKey = null;
        String privateKey = null;
        if (splits.length >= 3 && CharSequenceUtil.isNotEmpty(splits[2])) {
            privateKey = splits[1];
            publicKey = splits[2];
        }
        if (CharSequenceUtil.isEmpty(publicKey)) {
            publicKey = jasyptEncryptorConfigurationProperties.getPublicKeyString();
        }
        if (CharSequenceUtil.isEmpty(privateKey)) {
            privateKey = jasyptEncryptorConfigurationProperties.getPrivateKeyString();
        }
        try {
            SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
            String decrypted = sm2.decryptStr(data, KeyType.PrivateKey);
            log.debug("解密后配置信息:{}", decrypted);
            return decrypted;
        } catch (Exception e) {
            log.warn("配置信息解密失败", e);
            return data;
        }
    }

}
