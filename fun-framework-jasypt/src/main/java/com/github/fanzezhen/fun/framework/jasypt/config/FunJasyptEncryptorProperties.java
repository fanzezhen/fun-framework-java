package com.github.fanzezhen.fun.framework.jasypt.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jasypt加密器配置属性
 * <p>
 * 配置前缀：fun.jasypt
 * <p>
 * 用于配置加密密钥的分隔符，支持SM2/SM4/RSA等多种加密算法的密钥格式。
 * 例如：公钥`分隔符`私钥 的格式存储密钥对。
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "fun.jasypt")
public class FunJasyptEncryptorProperties {
    /**
     * 分隔符
     */
    private String separator = "`";
}
