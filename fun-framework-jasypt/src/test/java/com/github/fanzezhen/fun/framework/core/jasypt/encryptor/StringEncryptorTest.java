package com.github.fanzezhen.fun.framework.core.jasypt.encryptor;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.github.fanzezhen.fun.framework.jasypt.config.FunJasyptEncryptorProperties;
import com.github.fanzezhen.fun.framework.jasypt.encryptor.SM2StringEncryptor;
import com.github.fanzezhen.fun.framework.jasypt.encryptor.SM4StringEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * StringEncryptor单元测试
 *
 * @author fanzezhen
 */
@Slf4j
class StringEncryptorTest {
    String privateKey = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg+MxXxSMFFgNtXGoFzkH3TJg0jXAo2J5XnM8isT7i0higCgYIKoEcz1UBgi2hRANCAARiDlNDvfGANqiqPHLWUN1mg1nz+4hN/06skj9DelWhIDK8IQ35NvFqf8dWoJkQ0KkxNvbuneWO0xt/e3fOgWkU";
    String publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEYg5TQ73xgDaoqjxy1lDdZoNZ8/uITf9OrJI/Q3pVoSAyvCEN+Tbxan/HVqCZENCpMTb27p3ljtMbf3t3zoFpFA==";
    String password = "password";

    @Test
    void testSM2StringEncryptorKey() {
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        privateKey = new String(Base64.getEncoder().encode(pair.getPrivate().getEncoded()), StandardCharsets.UTF_8);
        log.info("privateKey:{}", privateKey);
        publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()), StandardCharsets.UTF_8);
        log.info("publicKey:{}", publicKey);
    }

    @Test
    void testSM2StringEncryptor() {
        StringEncryptor stringEncryptor = new SM2StringEncryptor(new FunJasyptEncryptorProperties.ASymmetric(privateKey, publicKey));
        log.info("加密前:{}", password);
        String encrypted = stringEncryptor.encrypt(password);
        log.info("加密后:{}", encrypted);
        String decrypted = stringEncryptor.decrypt(encrypted);
        log.info("解密后:{}", decrypted);
        Assertions.assertEquals(password, decrypted);
    }

    @Test
    void testSM4StringEncryptor() {
        // 生成符合SM4规范的128位密钥示例
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[8]; // 16字节 = 128位
        secureRandom.nextBytes(bytes);
        String hexKey = Hex.encodeHexString(bytes); // 转换为32字符的十六进制字符串
        log.info("secretKey:{}", hexKey);
        StringEncryptor stringEncryptor = new SM4StringEncryptor(new FunJasyptEncryptorProperties.Symmetric(hexKey));
        log.info("加密前:{}", password);
        String encrypted = stringEncryptor.encrypt(password);
        log.info("加密后:{}", encrypted);
        String decrypted = stringEncryptor.decrypt(encrypted);
        log.info("解密后:{}", decrypted);
        Assertions.assertEquals(password, decrypted);
    }

    @Test
    void testBCryptStringEncryptor() {
        log.info("加密前:{}", password);
        String encrypted = BCrypt.hashpw(password);
        log.info("加密后:{}", encrypted);
        Assertions.assertTrue(BCrypt.checkpw(password, encrypted));
    }
}
