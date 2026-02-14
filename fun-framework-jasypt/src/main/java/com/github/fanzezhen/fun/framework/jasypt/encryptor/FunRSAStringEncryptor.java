package com.github.fanzezhen.fun.framework.jasypt.encryptor;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.jasypt.config.FunJasyptEncryptorProperties;
import com.github.fanzezhen.fun.framework.jasypt.enums.FunJasyptExceptionEnum;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA非对称算法加密器
 */
@Slf4j
public class FunRSAStringEncryptor extends AbstractStringEncryptor {

    public FunRSAStringEncryptor(FunJasyptEncryptorProperties funJasyptEncryptorProperties,
                                 JasyptEncryptorConfigurationProperties jasyptEncryptorConfigurationProperties) {
        super(funJasyptEncryptorProperties, jasyptEncryptorConfigurationProperties);
    }

    /**
     * 加密
     */
    @Override
    public String encrypt(String message) {
        if (CharSequenceUtil.isEmpty(message)) {
            return message;
        }
        final String[] splits = message.split(funJasyptEncryptorProperties.getSeparator());
        String plainText = splits[0].trim();
        PublicKey publicKey = getPublicKey(splits);
        if (publicKey == null) {
            log.warn("ENC() 未正确配置信息和秘钥，将直接采用 {} ，正确格式： ENC(信息`私钥`公钥)", message);
            return plainText;
        }
        try {
            // 执行加密操作
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            if (plainText.isEmpty()) {
                return plainText;
            }
            byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] cipherBytes = cipher.doFinal(plainBytes);
            return cn.hutool.core.codec.Base64.encode(cipherBytes);
        } catch (Exception e) {
            log.error("ENC() 信息加密报错，明文：{}", message, e);
            // 加密失败时返回原始明文
            return message;
        }
    }

    private PublicKey getPublicKey(String[] splits) {
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        if (splits.length >= 3 && CharSequenceUtil.isNotEmpty(splits[2])) {
            publicKey = getPublicKey(splits[2].trim());
            if (publicKey == null) {
                privateKey = getPrivateKey(splits[2]);
            }
        }
        if (splits.length >= 2 && publicKey == null) {
            publicKey = getPublicKey(splits[1].trim());
            if (publicKey == null && privateKey == null) {
                privateKey = getPrivateKey(splits[1]);
            }
        }
        if (publicKey == null && privateKey != null) {
            publicKey = generatePublicKeyFromPrivateKey(privateKey);
        }
        String secretKey;
        if (publicKey == null) {
            secretKey = jasyptEncryptorConfigurationProperties.getPublicKeyString();
            if (CharSequenceUtil.isNotEmpty(secretKey)) {
                publicKey = getPublicKey(secretKey);
            }
        }
        if (publicKey == null) {
            secretKey = jasyptEncryptorConfigurationProperties.getPrivateKeyString();
            if (CharSequenceUtil.isNotEmpty(secretKey)) {
                privateKey = getPrivateKey(secretKey);
                publicKey = generatePublicKeyFromPrivateKey(privateKey);
            }
        }
        if (publicKey == null) {
            secretKey = jasyptEncryptorConfigurationProperties.getPassword();
            if (CharSequenceUtil.isNotEmpty(secretKey)) {
                publicKey = getPublicKey(secretKey);
                if (publicKey == null) {
                    privateKey = getPrivateKey(secretKey);
                    publicKey = generatePublicKeyFromPrivateKey(privateKey);
                }
            }
        }
        return publicKey;
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
        String ciphertext = splits[0].trim();
        PrivateKey privateKey = null;
        for (int i = 1; i < splits.length && privateKey == null; i++) {
            privateKey = getPrivateKey(splits[i]);
        }
        if (privateKey == null) {
            privateKey = getPrivateKey(jasyptEncryptorConfigurationProperties.getPrivateKeyString());
        }
        if (privateKey == null) {
            privateKey = getPrivateKey(jasyptEncryptorConfigurationProperties.getPassword());
        }
        if (privateKey == null) {
            log.warn("ENC() 未正确配置信息和秘钥，将直接采用 {} ，正确格式： ENC(信息`私钥)", message);
            return ciphertext;
        }
        try {
            String value = decrypt(privateKey, ciphertext);
            log.debug("ENC() 密文解密，密文：{} , 明文：{}", ciphertext, value);
            return value;
        } catch (Exception e) {
            log.error("ENC() 密文解密报错，密文：{} ,秘钥：{}", ciphertext, privateKey, e);
        }
        return ciphertext;
    }

    /**
     * 使用私钥解密字符串
     * 该方法采用RSA算法，ECB模式和PKCS1填充方式对密文进行解密
     *
     * @param privateKey 私钥，用于解密密文
     * @param cipherText 密文，即加密后的字符串
     *
     * @return 解密后的字符串如果密文为空或null，则直接返回密文
     */
    public static String decrypt(PrivateKey privateKey, String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            if (cipherText == null || cipherText.isEmpty()) {
                return cipherText;
            }
            byte[] cipherBytes = cn.hutool.core.codec.Base64.decode(cipherText);
            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes);
        } catch (Exception e) {
            log.warn("使用私钥{}解密字符串{}失败", privateKey, cipherText, e);
            return null;
        }
    }

    /**
     * 通过私钥字符串获取PrivateKey对象
     */
    public static PrivateKey getPrivateKey(String privateKeyText) {
        try {
            byte[] privateKeyBytes = cn.hutool.core.codec.Base64.decode(privateKeyText);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SunRsaSign");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.warn("获取私钥失败{}", privateKeyText, e);
            return null;
        }
    }

    /**
     * 通过公钥字符串获取 PublicKey 对象
     */
    public static PublicKey getPublicKey(String publicKeyText) {
        if (publicKeyText == null || publicKeyText.isEmpty()) {
            throw new ServiceException(FunJasyptExceptionEnum.PUBLIC_KEY_MISSING);
        }
        try {
            byte[] publicKeyBytes = cn.hutool.core.codec.Base64.decode(publicKeyText);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SunRsaSign");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.warn("获取公钥失败{}", publicKeyText, e);
            return null;
        }
    }

    /**
     * 通过私钥生成公钥
     *
     * @param privateKey 私钥对象
     *
     * @return 公钥对象
     *
     */
    public static PublicKey generatePublicKeyFromPrivateKey(PrivateKey privateKey) {
        try {
            // 获取私钥的KeySpec
            RSAPrivateCrtKeySpec privateKeySpec = KeyFactory.getInstance("RSA").getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
            // 提取模数和公钥指数
            java.math.BigInteger modulus = privateKeySpec.getModulus();
            java.math.BigInteger publicExponent = privateKeySpec.getPublicExponent();
            // 构造公钥的KeySpec
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
            // 生成公钥
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            log.warn("生成公钥失败{}", privateKey, e);
            return null;
        }
    }
}
