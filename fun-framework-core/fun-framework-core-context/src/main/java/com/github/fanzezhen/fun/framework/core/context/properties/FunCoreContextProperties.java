package com.github.fanzezhen.fun.framework.core.context.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 上下文配置属性类.
 * <p>
 * 配置前缀：fun.core.context
 * <p>
 * 定义上下文中使用的请求头Key名称（如token、tenantId、userId等），
 * 支持自定义前缀以适配不同的业务系统规范。
 */
@Data
@ConfigurationProperties(prefix = "fun.core.context")
public class FunCoreContextProperties {
    /**
     * 上下文Key配置.
     */
    private Key key = new Key();

    /**
     * 上下文Key配置类.
     * <p>
     * 定义各类上下文数据的请求头Key名称。
     */
    @Data
    public static class Key {
        /**
         * 上下文前缀.
         */
        private String prefix = ContextConstant.DEFAULT_HEADER_PREFIX;
        /**
         * Token.
         */
        private String token = ContextConstant.DEFAULT_HEADER_TOKEN;
        /**
         * 应用标识.
         */
        private String appCode = ContextConstant.DEFAULT_HEADER_APP_CODE;
        /**
         * 客户端标识.
         */
        private String clientCode = ContextConstant.DEFAULT_HEADER_CLIENT_CODE;
        /**
         * 区域和语言.
         */
        private String locale = ContextConstant.DEFAULT_HEADER_LOCALE;
        /**
         * 时区.
         */
        private String timeZone = ContextConstant.DEFAULT_HEADER_TIME_ZONE;
        /**
         * 系统域名.
         */
        private String serverHost = ContextConstant.DEFAULT_HEADER_SERVER_HOST;
        /**
         * 设备型号.
         */
        private String device = ContextConstant.DEFAULT_HEADER_DEVICE;
        /**
         * 用户IP.
         */
        private String userIp = ContextConstant.DEFAULT_HEADER_USER_IP;
        /**
         * 用户账号名称.
         */
        private String accountName = ContextConstant.DEFAULT_HEADER_ACCOUNT_NAME;
        /**
         * 用户名称.
         */
        private String userName = ContextConstant.DEFAULT_HEADER_USER_NAME;
        /**
         * 用户账号ID.
         */
        private String accountId = ContextConstant.DEFAULT_HEADER_ACCOUNT_ID;
        /**
         * 用户ID.
         */
        private String userId = ContextConstant.DEFAULT_HEADER_USER_ID;
        /**
         * 租户ID.
         */
        private String tenantId = ContextConstant.DEFAULT_HEADER_TENANT_ID;
        /**
         * 项目ID.
         */
        private String projectId = ContextConstant.DEFAULT_HEADER_PROJECT_ID;
        /**
         * 日志跟踪标识.
         */
        private String traceId = ContextConstant.DEFAULT_HEADER_TRACE_ID;
        /**
         * 节点ID.
         */
        private String nodeId = ContextConstant.DEFAULT_HEADER_NODE_ID;
        /**
         * 原始请求头.
         */
        private String originHeaders = ContextConstant.DEFAULT_HEADER_NODE_ID;

        /**
         * 获取带前缀的Token Key.
         *
         * @return 带前缀的Token Key
         */
        public String getTokenWithPrefix() {
            return prefix + token;
        }

        /**
         * 获取带前缀的应用编码Key.
         *
         * @return 带前缀的应用编码Key
         */
        public String getAppCodeWithPrefix() {
            return prefix + appCode;
        }

        /**
         * 获取带前缀的客户端编码Key.
         *
         * @return 带前缀的客户端编码Key
         */
        public String getClientCodeWithPrefix() {
            return prefix + clientCode;
        }

        /**
         * 获取带前缀的区域语言Key.
         *
         * @return 带前缀的区域语言Key
         */
        public String getLocaleWithPrefix() {
            return prefix + locale;
        }

        /**
         * 获取带前缀的时区Key.
         *
         * @return 带前缀的时区Key
         */
        public String getTimeZoneWithPrefix() {
            return prefix + timeZone;
        }

        /**
         * 获取带前缀的服务器主机Key.
         *
         * @return 带前缀的服务器主机Key
         */
        public String getServerHostWithPrefix() {
            return prefix + serverHost;
        }

        /**
         * 获取带前缀的设备Key.
         *
         * @return 带前缀的设备Key
         */
        public String getDeviceWithPrefix() {
            return prefix + device;
        }

        /**
         * 获取带前缀的用户IP Key.
         *
         * @return 带前缀的用户IP Key
         */
        public String getUserIpWithPrefix() {
            return prefix + userIp;
        }

        /**
         * 获取带前缀的账号名称Key.
         *
         * @return 带前缀的账号名称Key
         */
        public String getAccountNameWithPrefix() {
            return prefix + accountName;
        }

        /**
         * 获取带前缀的用户名称Key.
         *
         * @return 带前缀的用户名称Key
         */
        public String getUserNameWithPrefix() {
            return prefix + userName;
        }

        /**
         * 获取带前缀的账号ID Key.
         *
         * @return 带前缀的账号ID Key
         */
        public String getAccountIdWithPrefix() {
            return prefix + accountId;
        }

        /**
         * 获取带前缀的用户ID Key.
         *
         * @return 带前缀的用户ID Key
         */
        public String getUserIdWithPrefix() {
            return prefix + userId;
        }

        /**
         * 获取带前缀的租户ID Key.
         *
         * @return 带前缀的租户ID Key
         */
        public String getTenantIdWithPrefix() {
            return prefix + tenantId;
        }

        /**
         * 获取带前缀的项目ID Key.
         *
         * @return 带前缀的项目ID Key
         */
        public String getProjectIdWithPrefix() {
            return prefix + projectId;
        }

        /**
         * 获取带前缀的追踪ID Key.
         *
         * @return 带前缀的追踪ID Key
         */
        public String getTraceIdWithPrefix() {
            return prefix + traceId;
        }

        /**
         * 获取带前缀的节点ID Key.
         *
         * @return 带前缀的节点ID Key
         */
        public String getNodeIdWithPrefix() {
            return prefix + nodeId;
        }
    }

}
