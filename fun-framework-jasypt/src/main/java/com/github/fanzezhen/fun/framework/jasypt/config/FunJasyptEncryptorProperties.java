package com.github.fanzezhen.fun.framework.jasypt.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jasypt 加密器配置属性.
 * <p>
 * 配置前缀：fun.jasypt
 * <p>
 * 用于配置加密密钥的分隔符，支持 SM2/SM4/RSA 等多种加密算法的密钥格式.
 * 例如：公钥`分隔符`私钥 的格式存储密钥对。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "fun.jasypt")
public class FunJasyptEncryptorProperties {
    /**
     * 默认分隔符.
     */
    private static final String DEFAULT_SEPARATOR = "`";

    /**
     * 密钥分隔符，默认为反引号.
     */
    private String separator = DEFAULT_SEPARATOR;
}
