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
 * RSA 非对称算法加密器.
 * <p>
 * 使用 RSA/ECB/OAEPWithSHA-256AndMGF1Padding 算法进行加密和解密.
 * <p>
 * 支持从配置中获取公钥和私钥，或从加密消息中解析密钥.
 * 格式：ENC(明文`私钥`公钥) 或 ENC(明文`密钥)
 */
@Slf4j
public class FunRSAStringEncryptor extends AbstractStringEncryptor {

    /**
     * RSA 加密算法名称.
     */
    private static final String RSA_ALGORITHM =
            "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * RSA 密钥算法.
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 密钥工厂提供者.
     */
    private static final String KEY_FACTORY_PROVIDER = "SunRsaSign";

    /**
     * 数组最小长度（包含密钥信息）.
     */
    private static final int MIN_ARRAY_LENGTH_WITH_KEY = 2;

    /**
     * 数组完整长度（包含公钥和私钥）.
     */
    private static final int FULL_ARRAY_LENGTH = 3;

    /**
     * 构造 RSA 加密器实例.
     *
     * @param funJasyptEncryptorProperties Fun 框架的 Jasypt 配置属性
     * @param jasyptEncryptorConfigurationProperties Jasypt 原生配置属性
     */
    public FunRSAStringEncryptor(
            final FunJasyptEncryptorProperties funJasyptEncryptorProperties,
            final JasyptEncryptorConfigurationProperties
                    jasyptEncryptorConfigurationProperties) {
        super(funJasyptEncryptorProperties,
                jasyptEncryptorConfigurationProperties);
    }

    /**
     * 使用 RSA 公钥加密字符串.
     * <p>
     * 支持的消息格式：
     * <ul>
     *   <li>明文 - 使用配置中的公钥</li>
     *   <li>明文`私钥 - 从私钥生成公钥</li>
     *   <li>明文`私钥`公钥 - 直接使用指定公钥</li>
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
        String plainText = splits[0].trim();
        PublicKey publicKey = getPublicKey(splits);
        if (publicKey == null) {
            log.warn("ENC() 未正确配置信息和秘钥，将直接采用 {} ，"
                    + "正确格式： ENC(信息`私钥`公钥)", message);
            return plainText;
        }
        try {
            // 执行加密操作
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
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

    /**
     * 从分割的字符串数组中获取公钥.
     * <p>
     * 按以下优先级查找公钥：
     * <ol>
     *   <li>从消息第3段解析公钥或私钥（私钥会生成公钥）</li>
     *   <li>从消息第2段解析公钥或私钥</li>
     *   <li>从配置的 publicKeyString 获取</li>
     *   <li>从配置的 privateKeyString 生成</li>
     *   <li>从配置的 password 解析或生成</li>
     * </ol>
     *
     * @param splits 分割后的消息数组
     * @return 公钥对象，未找到则返回 null
     */
    private PublicKey getPublicKey(final String[] splits) {
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        if (splits.length >= FULL_ARRAY_LENGTH
                && CharSequenceUtil.isNotEmpty(
                        splits[MIN_ARRAY_LENGTH_WITH_KEY])) {
            publicKey = getPublicKey(
                    splits[MIN_ARRAY_LENGTH_WITH_KEY].trim());
            if (publicKey == null) {
                privateKey = getPrivateKey(
                        splits[MIN_ARRAY_LENGTH_WITH_KEY]);
            }
        }
        if (splits.length >= MIN_ARRAY_LENGTH_WITH_KEY && publicKey == null) {
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
     * 使用 RSA 私钥解密字符串.
     * <p>
     * 支持的消息格式：
     * <ul>
     *   <li>密文 - 使用配置中的私钥</li>
     *   <li>密文`私钥 - 使用指定私钥</li>
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
        String ciphertext = splits[0].trim();
        PrivateKey privateKey = null;
        for (int i = 1; i < splits.length && privateKey == null; i++) {
            privateKey = getPrivateKey(splits[i]);
        }
        if (privateKey == null) {
            privateKey = getPrivateKey(jasyptEncryptorConfigurationProperties
                    .getPrivateKeyString());
        }
        if (privateKey == null) {
            privateKey = getPrivateKey(jasyptEncryptorConfigurationProperties
                    .getPassword());
        }
        if (privateKey == null) {
            log.warn("ENC() 未正确配置信息和秘钥，将直接采用 {} ，"
                    + "正确格式： ENC(信息`私钥)", message);
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
     * 使用私钥解密字符串.
     * <p>
     * 该方法采用 RSA 算法，ECB 模式和 OAEP 填充方式对密文进行解密.
     *
     * @param privateKey 私钥，用于解密密文
     * @param cipherText 密文，即加密后的 Base64 字符串
     * @return 解密后的明文字符串，如果密文为空或解密失败则返回 null
     */
    public static String decrypt(final PrivateKey privateKey,
                                  final String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            if (cipherText == null || cipherText.isEmpty()) {
                return cipherText;
            }
            byte[] cipherBytes = cn.hutool.core.codec.Base64.decode(cipherText);
            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("使用私钥{}解密字符串{}失败", privateKey, cipherText, e);
            return null;
        }
    }

    /**
     * 通过私钥字符串获取 PrivateKey 对象.
     *
     * @param privateKeyText Base64 编码的私钥字符串
     * @return 私钥对象，解析失败则返回 null
     */
    public static PrivateKey getPrivateKey(final String privateKeyText) {
        try {
            byte[] privateKeyBytes = cn.hutool.core.codec.Base64
                    .decode(privateKeyText);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
                    privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM,
                    KEY_FACTORY_PROVIDER);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.warn("获取私钥失败{}", privateKeyText, e);
            return null;
        }
    }

    /**
     * 通过公钥字符串获取 PublicKey 对象.
     *
     * @param publicKeyText Base64 编码的公钥字符串
     * @return 公钥对象
     * @throws ServiceException 如果公钥字符串为空
     */
    public static PublicKey getPublicKey(final String publicKeyText) {
        if (publicKeyText == null || publicKeyText.isEmpty()) {
            throw new ServiceException(
                    FunJasyptExceptionEnum.PUBLIC_KEY_MISSING);
        }
        try {
            byte[] publicKeyBytes = cn.hutool.core.codec.Base64
                    .decode(publicKeyText);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                    publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM,
                    KEY_FACTORY_PROVIDER);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.warn("获取公钥失败{}", publicKeyText, e);
            return null;
        }
    }

    /**
     * 通过私钥生成公钥.
     * <p>
     * 从 RSA 私钥中提取模数和公钥指数，构造对应的公钥对象.
     *
     * @param privateKey RSA 私钥对象
     * @return 对应的公钥对象，生成失败则返回 null
     */
    public static PublicKey generatePublicKeyFromPrivateKey(final PrivateKey privateKey) {
        try {
            // 获取私钥的 KeySpec
            RSAPrivateCrtKeySpec privateKeySpec = KeyFactory
                    .getInstance(KEY_ALGORITHM)
                    .getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
            // 提取模数和公钥指数
            java.math.BigInteger modulus = privateKeySpec.getModulus();
            java.math.BigInteger publicExponent = privateKeySpec
                    .getPublicExponent();
            // 构造公钥的 KeySpec
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus,
                    publicExponent);
            // 生成公钥
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            log.warn("生成公钥失败{}", privateKey, e);
            return null;
        }
    }
}
