package com.github.fanzezhen.fun.framework.core.springboot.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Fun Springboot Web 模块配置属性类.
 * <p>
 * 提供响应包装、接口注册、JWT认证等Web层功能的配置。
 */
@Data
@ConfigurationProperties(prefix = "fun.springboot.web")
public class FunSpringbootWebProperties {
    /**
     * 响应包装配置.
     */
    private ResponseWrapper responseWrapper;
    /**
     * 接口注册配置.
     */
    private Register register;
    /**
     * JWT配置示例: {"admin": {"secret": "admin", "tenantId": ""}}.
     */
    private Jwt jwt = new Jwt();

    /**
     * JWT认证配置类.
     */
    @Data
    public static class Jwt {
        /**
         * 请求头配置.
         */
        private Header header;
        /**
         * 网络延迟超时时间（毫秒）.
         */
        private Long networkDelayMillis;
        /**
         * token超时时间（秒）.
         */
        private Long timeOutSeconds;
        /**
         * 忽略校验的接口列表，支持通配符.
         * 例如：/generate/**
         */
        private Set<String> ignoreUris;
        /**
         * 授权账户信息集合.
         */
        private Map<String, AccountInfo> accountInfos;

        /**
         * 账户信息配置类.
         */
        @Data
        public static class AccountInfo {
            /**
             * 账号.
             */
            String account;
            /**
             * 秘钥.
             */
            String secret;
            /**
             * 租户ID.
             */
            String tenantId;
        }

        /**
         * JWT请求头配置类.
         */
        @Data
        public static class Header {
            /**
             * token请求头名称.
             */
            String token = "fun-token";
            /**
             * 发出请求时的时间戳请求头名称.
             */
            String timestamp = "fun-timestamp";
        }

        /**
         * 根据账号获取用户信息.
         *
         * @param account 账号
         * @return 账户信息，不存在则返回null
         */
        public AccountInfo getUserInfo(final String account) {
            return accountInfos != null ? accountInfos.get(account) : null;
        }
    }

    /**
     * 接口注册配置类.
     */
    @Data
    public static class Register {
        /**
         * 注册策略标识.
         * <ul>
         *     <li>null - 不启用此功能</li>
         *     <li>true - 注册paths指定的接口</li>
         *     <li>false - 排除paths指定的接口</li>
         * </ul>
         */
        private Boolean flag;
        /**
         * 接口路径，支持通配符.
         */
        private String[] paths;
    }

    /**
     * 响应包装配置类.
     */
    @Data
    public static class ResponseWrapper {
        /**
         * 是否启用响应结果包装.
         */
        private Boolean enabled;
        /**
         * 忽略包装的接口路径，支持通配符.
         */
        private Set<String> ignorePaths = Collections.emptySet();

        /**
         * 判断是否启用响应包装.
         *
         * @return true表示启用
         */
        public boolean enabled() {
            return Boolean.TRUE.equals(enabled);
        }
    }
}
