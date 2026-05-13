package com.github.fanzezhen.fun.framework.core.springboot.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 */
@Data
@ConfigurationProperties(prefix = "fun.springboot.web")
public class FunSpringbootWebProperties {
    private ResponseWrapper responseWrapper;
    private Register register;
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

    @Data
    public static class Register {
        /**
         * null：不启用此功能；
         * true：注册registerPaths；
         * false：排除registerPaths
         */
        private Boolean flag;
        /**
         * 接口路径，支持通配符
         */
        private String[] paths;
    }
    @Data
    public static class ResponseWrapper {
        /**
         * 是否启用响应结果包装
         */
        private Boolean enabled;
        /**
         * 接口路径，支持通配符
         */
        private Set<String> ignorePaths = Collections.emptySet();

        public boolean enabled() {
            return Boolean.TRUE.equals(enabled);
        }
    }
}
