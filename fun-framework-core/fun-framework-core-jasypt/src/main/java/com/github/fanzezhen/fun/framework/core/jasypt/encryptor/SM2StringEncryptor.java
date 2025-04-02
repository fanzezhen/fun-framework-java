package com.github.fanzezhen.fun.framework.core.jasypt.encryptor;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.github.fanzezhen.fun.framework.core.jasypt.config.FunJasyptEncryptorProperties;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.commons.CommonUtils;
import org.jasypt.encryption.StringEncryptor;

import java.nio.charset.StandardCharsets;

@Slf4j
public class SM2StringEncryptor implements StringEncryptor {

    private final FunJasyptEncryptorProperties.ASymmetric properties;

    public SM2StringEncryptor(FunJasyptEncryptorProperties.ASymmetric properties) {
        this.properties = properties;
    }

    @Override
    public String encrypt(String message) {
        String privateKey = properties.getPrivateKey();
        String publicKey = properties.getPublicKey();
        CommonUtils.validateNotNull(message, "msg cannot be set null");
        CommonUtils.validateNotNull(privateKey, "privateKey config not be set");
        CommonUtils.validateNotNull(publicKey, "publicKey config not be set");
        try {
            log.debug("加密前配置信息:{}", message);
            SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
            String encryptedBase64 = sm2.encryptBase64(message, KeyType.PublicKey);
            log.debug("加密后配置信息:{}", encryptedBase64);
            return encryptedBase64;
        } catch (Exception e) {
            log.error("配置信息加密失败", e);
            return message;
        }
    }

    @Override
    public String decrypt(String message) {
        String privateKey = properties.getPrivateKey();
        String publicKey = properties.getPublicKey();
        CommonUtils.validateNotNull(message, "msg cannot be set null");
        CommonUtils.validateNotNull(privateKey, "privateKey config not be set");
        CommonUtils.validateNotNull(publicKey, "publicKey config not be set");
        try {
            log.debug("解密前配置信息:{}", message);
            SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
            String decrypted = sm2.decryptStr(message, KeyType.PrivateKey);
            log.debug("解密后配置信息:{}", decrypted);
            return decrypted;
        } catch (Exception e) {
            log.warn("配置信息解密失败", e);
            return message;
        }
    }

}
