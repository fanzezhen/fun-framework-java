package com.github.fanzezhen.fun.framework.jasypt.encryptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.github.fanzezhen.fun.framework.jasypt.config.FunJasyptEncryptorProperties;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FunSM4StringEncryptor extends AbstractStringEncryptor {

    public FunSM4StringEncryptor(FunJasyptEncryptorProperties funJasyptEncryptorProperties,
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
        String plaintext = splits[0].trim();
        String secret = getSecret(splits);
        if(CharSequenceUtil.isEmpty(secret)){
            secret = jasyptEncryptorConfigurationProperties.getPublicKeyString();
        }
        if(CharSequenceUtil.isEmpty(secret)){
            secret = jasyptEncryptorConfigurationProperties.getPrivateKeyString();
        }
        if (CharSequenceUtil.isEmpty(secret)) {
            log.warn("ENC() 未正确配置信息和秘钥，将直接采用 {} ，正确格式： ENC(信息`密钥)", message);
            return plaintext;
        }
        try {
            SymmetricCrypto sm4 = SmUtil.sm4(secret.trim().getBytes());
            return sm4.encryptBase64(plaintext);
        } catch (Exception e) {
            log.warn("配置信息加密失败", e);
            return plaintext;
        }
    }

    private String getSecret(String[] splits) {
        String secret = null;
        for (int i = 1; i < splits.length; i++) {
            String trimmed = splits[1].trim();
            if (CharSequenceUtil.isNotEmpty(trimmed)){
                secret = trimmed;
            }
        }
        if(secret  == null){
            secret = jasyptEncryptorConfigurationProperties.getPassword();
        }
        return secret;
    }

    /**
     * 解密
     */
    @Override
    public String decrypt(String message) {
        if (CharSequenceUtil.isEmpty(message)){
            return message;
        }
        final String[] splits = message.split(funJasyptEncryptorProperties.getSeparator());
        String data = splits[0].trim();
        String secret = getSecret(splits);
        if(CharSequenceUtil.isEmpty(secret)){
            secret = jasyptEncryptorConfigurationProperties.getPrivateKeyString();
        }
        if(CharSequenceUtil.isEmpty(secret)){
            secret = jasyptEncryptorConfigurationProperties.getPublicKeyString();
        }
        if (CharSequenceUtil.isEmpty(secret)) {
            log.warn("ENC() 未正确配置信息和秘钥，将直接采用 {} ，正确格式： ENC(信息`密钥)", message);
            return data;
        }
        try {
            SymmetricCrypto sm4 = SmUtil.sm4(secret.getBytes());
            return sm4.decryptStr(data);
        } catch (Exception e) {
            log.warn("配置信息解密失败", e);
        }
        return message;
    }

}
