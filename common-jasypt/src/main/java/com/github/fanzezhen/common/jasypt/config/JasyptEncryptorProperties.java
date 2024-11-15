package com.github.fanzezhen.common.jasypt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author fanzezhen
 * @since 3.1.8
 */
@Data
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.jasypt")
public class JasyptEncryptorProperties {

    /**
     * 加密类型 SM2:非对称加密 或者 SM4:对称加密
     */
    private String type;

    /**
     * SM4:对称加密密钥
     */
    private String secretKey;

    /**
     * SM2:非对称加密私钥
     */
    private String privateKey;

    /**
     * SM2:非对称加密公钥
     */
    private String publicKey;

    /**
     * 加密参数
     */
    private Map<String, Object> encryptParam;

    /**
     * 解密参数
     */
    private Map<String, Object> decryptParam;
}
