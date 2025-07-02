package com.github.fanzezhen.fun.framework.core.context.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fanzezhen
 */
@Data
@ConfigurationProperties(prefix = "fun.core.context")
public class FunCoreContextProperties {
    /**
     * 上下文key
     */
    private Key key = new Key();

    @Data
    public static class Key {
        /**
         * 上下文前缀
         */
        private String prefix = ContextConstant.DEFAULT_HEADER_PREFIX;
        /**
         * token
         */
        private String token = ContextConstant.DEFAULT_HEADER_TOKEN;
        /**
         * 应用标识
         */
        private String appCode = ContextConstant.DEFAULT_HEADER_APP_CODE;
        /**
         * 客户端标识
         */
        private String clientCode = ContextConstant.DEFAULT_HEADER_CLIENT_CODE;
        /**
         * 区域和语言
         */
        private String locale = ContextConstant.DEFAULT_HEADER_LOCALE;
        /**
         * 时区
         */
        private String timeZone = ContextConstant.DEFAULT_HEADER_TIME_ZONE;
        /**
         * 系统域名
         */
        private String serverHost = ContextConstant.DEFAULT_HEADER_SERVER_HOST;
        /**
         * 设备型号
         */
        private String device = ContextConstant.DEFAULT_HEADER_DEVICE;
        /**
         * 用户ip
         */
        private String userIp = ContextConstant.DEFAULT_HEADER_USER_IP;
        /**
         * 用户帐号名称
         */
        private String accountName = ContextConstant.DEFAULT_HEADER_ACCOUNT_NAME;
        /**
         * 用户名称
         */
        private String userName = ContextConstant.DEFAULT_HEADER_USER_NAME;
        /**
         * 用户帐号id
         */
        private String accountId = ContextConstant.DEFAULT_HEADER_ACCOUNT_ID;
        /**
         * 用户id
         */
        private String userId = ContextConstant.DEFAULT_HEADER_USER_ID;
        /**
         * 租户id
         */
        private String tenantId = ContextConstant.DEFAULT_HEADER_TENANT_ID;
        /**
         * 项目id
         */
        private String projectId = ContextConstant.DEFAULT_HEADER_PROJECT_ID;
        /**
         * 日志跟踪标识
         */
        private String traceId = ContextConstant.DEFAULT_HEADER_TRACE_ID;
        /**
         * 节点id
         */
        private String nodeId = ContextConstant.DEFAULT_HEADER_NODE_ID;
        /**
         * 原始
         */
        private String originHeaders = ContextConstant.DEFAULT_HEADER_NODE_ID;

        public String getTokenWithPrefix() {
            return prefix + token;
        }

        public String getAppCodeWithPrefix() {
            return prefix + appCode;
        }

        public String getClientCodeWithPrefix() {
            return prefix + clientCode;
        }

        public String getLocaleWithPrefix() {
            return prefix + locale;
        }

        public String getTimeZoneWithPrefix() {
            return prefix + timeZone;
        }

        public String getServerHostWithPrefix() {
            return prefix + serverHost;
        }

        public String getDeviceWithPrefix() {
            return prefix + device;
        }

        public String getUserIpWithPrefix() {
            return prefix + userIp;
        }

        public String getAccountNameWithPrefix() {
            return prefix + accountName;
        }

        public String getUserNameWithPrefix() {
            return prefix + userName;
        }

        public String getAccountIdWithPrefix() {
            return prefix + accountId;
        }

        public String getUserIdWithPrefix() {
            return prefix + userId;
        }

        public String getTenantIdWithPrefix() {
            return prefix + tenantId;
        }

        public String getProjectIdWithPrefix() {
            return prefix + projectId;
        }

        public String getTraceIdWithPrefix() {
            return prefix + traceId;
        }

        public String getNodeIdWithPrefix() {
            return prefix + nodeId;
        }
    }

}
