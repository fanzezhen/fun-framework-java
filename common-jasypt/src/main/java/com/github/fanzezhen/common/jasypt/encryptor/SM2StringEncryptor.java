package com.github.fanzezhen.common.jasypt.encryptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.github.fanzezhen.common.jasypt.config.JasyptEncryptorProperties;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.commons.CommonUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SM2StringEncryptor implements StringEncryptor {

    @Autowired
    private JasyptEncryptorProperties configurationProperties;

    @Override
    public String encrypt(String message) {
        String privateKey = configurationProperties.getPrivateKey();
        String publicKey = configurationProperties.getPublicKey();
        CommonUtils.validateNotNull(message, "msg cannot be set null");
        CommonUtils.validateNotNull(privateKey, "privateKey config not be set");
        CommonUtils.validateNotNull(publicKey, "publicKey config not be set");
        try {
            log.debug("加密前配置信息:{}", message);
            SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
            message = sm2.encryptBcd(message, KeyType.PublicKey);
            log.debug("加密后配置信息:{}", message);
        } catch (Exception e) {
            log.error("配置信息加密失败,{}", e.getStackTrace());
        }
        return message;
    }

    @Override
    public String decrypt(String message) {
        String privateKey = configurationProperties.getPrivateKey();
        String publicKey = configurationProperties.getPublicKey();
        CommonUtils.validateNotNull(message, "msg cannot be set null");
        CommonUtils.validateNotNull(privateKey, "privateKey config not be set");
        CommonUtils.validateNotNull(publicKey, "publicKey config not be set");
        try {
            log.debug("解密前配置信息:{}", message);
            SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
            message = StrUtil.utf8Str(sm2.decryptFromBcd(message, KeyType.PrivateKey));
            log.debug("解密后配置信息:{}", message);
        } catch (Exception e) {
            log.error("配置信息解密失败,{}", e.getStackTrace());
        }
        return message;
    }

}
