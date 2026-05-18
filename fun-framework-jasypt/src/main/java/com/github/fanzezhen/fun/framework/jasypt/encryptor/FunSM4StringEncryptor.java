package com.github.fanzezhen.fun.framework.jasypt.encryptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.github.fanzezhen.fun.framework.jasypt.config.FunJasyptEncryptorProperties;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 国密 SM4 对称算法加密器。
 * <p>
 * 使用国密 SM4 对称加密算法进行加密和解密。
 * <p>
 * 支持从配置中获取密钥，或从加密消息中解析密钥。
 * 格式：ENC(明文`密钥)
 */
@Slf4j
public class FunSM4StringEncryptor extends AbstractStringEncryptor {

    /**
     * 构造国密 SM4 加密器实例。
     *
     * @param funJasyptEncryptorProperties Fun 框架的 Jasypt 配置属性
     * @param jasyptEncryptorConfigurationProperties Jasypt 原生配置属性
     */
    public FunSM4StringEncryptor(final FunJasyptEncryptorProperties funJasyptEncryptorProperties,
                                 final JasyptEncryptorConfigurationProperties jasyptEncryptorConfigurationProperties) {
        super(funJasyptEncryptorProperties, jasyptEncryptorConfigurationProperties);
    }

    /**
     * 使用 SM4 密钥加密字符串。
     * <p>
     * 支持的消息格式：
     * <ul>
     *   <li>明文 - 使用配置中的密钥</li>
     *   <li>明文`密钥 - 使用指定密钥</li>
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
        String plaintext = splits[0].trim();
        String secret = getSecret(splits);
        if (CharSequenceUtil.isEmpty(secret)) {
            secret = jasyptEncryptorConfigurationProperties.getPublicKeyString();
        }
        if (CharSequenceUtil.isEmpty(secret)) {
            secret = jasyptEncryptorConfigurationProperties.getPrivateKeyString();
        }
        if (CharSequenceUtil.isEmpty(secret)) {
            log.warn("ENC() 未正确配置信息和秘钥，将直接采用 {} ，正确格式： ENC(信息`密钥)", message);
            return plaintext;
        }
        try {
            SymmetricCrypto sm4 = SmUtil.sm4(secret.trim().getBytes(StandardCharsets.UTF_8));
            return sm4.encryptBase64(plaintext);
        } catch (Exception e) {
            log.warn("配置信息加密失败", e);
            return plaintext;
        }
    }

    /**
     * 从分割的字符串数组中获取密钥。
     * <p>
     * 按以下优先级查找密钥：
     * <ol>
     *   <li>从消息中解析的密钥片段</li>
     *   <li>从配置的 password 获取</li>
     * </ol>
     *
     * @param splits 分割后的消息数组
     * @return 密钥字符串，未找到则返回 null
     */
    private String getSecret(final String[] splits) {
        String secret = null;
        for (int i = 1; i < splits.length; i++) {
            String trimmed = splits[i].trim();
            if (CharSequenceUtil.isNotEmpty(trimmed)) {
                secret = trimmed;
            }
        }
        if (secret == null) {
            secret = jasyptEncryptorConfigurationProperties.getPassword();
        }
        return secret;
    }

    /**
     * 使用 SM4 密钥解密字符串。
     * <p>
     * 支持的消息格式：
     * <ul>
     *   <li>密文 - 使用配置中的密钥</li>
     *   <li>密文`密钥 - 使用指定密钥</li>
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
        String secret = getSecret(splits);
        if (CharSequenceUtil.isEmpty(secret)) {
            secret = jasyptEncryptorConfigurationProperties.getPrivateKeyString();
        }
        if (CharSequenceUtil.isEmpty(secret)) {
            secret = jasyptEncryptorConfigurationProperties.getPublicKeyString();
        }
        if (CharSequenceUtil.isEmpty(secret)) {
            log.warn("ENC() 未正确配置信息和秘钥，将直接采用 {} ，正确格式： ENC(信息`密钥)", message);
            return data;
        }
        try {
            SymmetricCrypto sm4 = SmUtil.sm4(secret.getBytes(StandardCharsets.UTF_8));
            return sm4.decryptStr(data);
        } catch (Exception e) {
            log.warn("配置信息解密失败", e);
        }
        return message;
    }

}
