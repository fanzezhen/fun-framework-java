package com.github.fanzezhen.fun.framework.core.verify;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Map;

/**
 * @author zezhen.fan
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "fun")
public class FunCoreVerifyProperties {
    /**
     * "{\n\"admin\": {\n\"secret\": \"admin\",\n\"tenantId\": \"\"\n}}"
     */
    private Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        private Map<String, AccountInfo> accountInfos;

        @Data
        public static class AccountInfo {
            String account;
            String secret;
            String tenantId;
        }

        public AccountInfo getUserInfo(String account) {
            return accountInfos != null ? accountInfos.get(account) : null;
        }
    }
}
