package com.github.fanzezhen.fun.framework.jasypt.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fanzezhen
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
