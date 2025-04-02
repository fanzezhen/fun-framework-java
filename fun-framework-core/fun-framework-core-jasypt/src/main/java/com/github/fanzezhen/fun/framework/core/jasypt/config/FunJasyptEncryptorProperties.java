package com.github.fanzezhen.fun.framework.core.jasypt.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author fanzezhen
 * @since 3.1.8
 */
@Data
@ConfigurationProperties(prefix = "com.github.fanzezhen.fun.framework.core.jasypt")
public class FunJasyptEncryptorProperties {

    /**
     * 加密类型 SM2:非对称加密 或者 SM4:对称加密
     */
    private String type;

    /**
     * SM2非对称加密配置
     */
    private ASymmetric aSymmetric;

    /**
     * SM4对称加密配置
     */
    private Symmetric symmetric;

    /**
     * 加密参数
     */
    private Map<String, Object> encryptParam;

    /**
     * 解密参数
     */
    private Map<String, Object> decryptParam;

    /**
     * SM2非对称加密配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ASymmetric {
        /**
         * SM2:非对称加密私钥
         */
        private String privateKey;
        /**
         * SM2:非对称加密公钥
         */
        private String publicKey;
    }

    /**
     * SM4对称加密配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Symmetric {
        /**
         * SM4:对称加密密钥
         */
        private String secretKey;
    }
}
