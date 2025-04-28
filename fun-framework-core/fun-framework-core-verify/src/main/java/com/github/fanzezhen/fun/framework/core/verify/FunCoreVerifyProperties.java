package com.github.fanzezhen.fun.framework.core.verify;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

/**
 * @author fanzezhen
 */
@Data
@ConfigurationProperties(prefix = "fun.core.verify")
public class FunCoreVerifyProperties {
    /**
     * "{\n\"admin\": {\n\"secret\": \"admin\",\n\"tenantId\": \"\"\n}}"
     */
    private Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        /**
         * 请求头配置
         */
        private Header header;
        /**
         * 网络延迟超时时间
         */
        private Long networkDelayMillis;
        /**
         * token超时时间
         */
        private Long timeOutSeconds;
        /**
         * 忽略校验的接口列表，支持通配符
         * 例如：/generate/**
         */
        private Set<String> ignoreUris;
        /**
         * 授权账户
         */
        private Map<String, AccountInfo> accountInfos;

        @Data
        public static class AccountInfo {
            /**
             * 账号
             */
            String account;
            /**
             * 秘钥
             */
            String secret;
            /**
             * 租户ID
             */
            String tenantId;
        }

        @Data
        public static class Header {
            /**
             * token
             */
            String token = "fun-token";
            /**
             * 发出请求时的时间戳
             */
            String timestamp = "fun-timestamp";
        }

        public AccountInfo getUserInfo(String account) {
            return accountInfos != null ? accountInfos.get(account) : null;
        }
    }
}
