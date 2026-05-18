package com.github.fanzezhen.fun.framework.core.verify;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 验证模块配置属性.
 * <p>
 * 用于绑定 fun.core.verify 前缀的配置项.
 */
@Data
@ConfigurationProperties(prefix = "fun.core.verify")
public class FunCoreVerifyProperties {
}
