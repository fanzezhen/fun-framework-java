package com.github.fanzezhen.fun.framework.jasypt.encryptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.github.fanzezhen.fun.framework.jasypt.config.FunJasyptEncryptorProperties;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * 国密 SM2 非对称算法加密器。
 * <p>
 * 使用国密 SM2 椭圆曲线算法进行加密和解密。
 * <p>
 * 支持从配置中获取公钥和私钥，或从加密消息中解析密钥。
 * 格式：ENC(明文`私钥`公钥)
 */
@Slf4j
public class FunSM2StringEncryptor extends AbstractStringEncryptor {

    /**
     * 数组完整长度（包含公钥和私钥）。
     */
    private static final int FULL_ARRAY_LENGTH = 3;

    /**
     * 私钥索引位置。
     */
    private static final int PRIVATE_KEY_INDEX = 1;

    /**
     * 公钥索引位置。
     */
    private static final int PUBLIC_KEY_INDEX = 2;

    /**
     * 构造国密 SM2 加密器实例。
     *
     * @param funJasyptEncryptorProperties Fun 框架的 Jasypt 配置属性
     * @param jasyptEncryptorConfigurationProperties Jasypt 原生配置属性
     */
    public FunSM2StringEncryptor(final FunJasyptEncryptorProperties funJasyptEncryptorProperties,
                                 final JasyptEncryptorConfigurationProperties jasyptEncryptorConfigurationProperties) {
        super(funJasyptEncryptorProperties, jasyptEncryptorConfigurationProperties);
    }

    /**
     * 使用 SM2 公钥加密字符串。
     * <p>
     * 支持的消息格式：
     * <ul>
     *   <li>明文 - 使用配置中的公钥</li>
     *   <li>明文`私钥`公钥 - 使用指定公钥</li>
     * </ul>
     *
     * @param message 待加密的消息
     * @return 加密后的 Base64 字符串，加密失败则返回原始消息
     */
    @Override
    public String encrypt(final String message) {
        if (CharSequenceUtil.isEmpty(message)) {
            return message;
        }
        final String[] splits = message.split(funJasyptEncryptorProperties.getSeparator());
        String data = splits[0].trim();
        String publicKey = null;
        String privateKey = null;
        if (splits.length >= FULL_ARRAY_LENGTH && CharSequenceUtil.isNotEmpty(splits[PUBLIC_KEY_INDEX])) {
            privateKey = splits[PRIVATE_KEY_INDEX];
            publicKey = splits[PUBLIC_KEY_INDEX];
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
     * 使用 SM2 私钥解密字符串。
     * <p>
     * 支持的消息格式：
     * <ul>
     *   <li>密文 - 使用配置中的私钥</li>
     *   <li>密文`私钥`公钥 - 使用指定私钥</li>
     * </ul>
     *
     * @param message 待解密的消息
     * @return 解密后的明文字符串，解密失败则返回密文
     */
    @Override
    public String decrypt(final String message) {
        if (CharSequenceUtil.isEmpty(message)) {
            return message;
        }
        final String[] splits = message.split(funJasyptEncryptorProperties.getSeparator());
        String data = splits[0].trim();
        String publicKey = null;
        String privateKey = null;
        if (splits.length >= FULL_ARRAY_LENGTH && CharSequenceUtil.isNotEmpty(splits[PUBLIC_KEY_INDEX])) {
            privateKey = splits[PRIVATE_KEY_INDEX];
            publicKey = splits[PUBLIC_KEY_INDEX];
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
