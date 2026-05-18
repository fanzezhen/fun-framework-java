package com.github.fanzezhen.fun.framework.proxy.core;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 代理配置属性。
 * <p>
 * 配置前缀：fun.proxy
 * <p>
 * 用于配置静态资源地址映射，将内网地址自动转换为外网可访问地址。
 * 支持HTTP/HTTPS协议的自动匹配和正则模式匹配。
 *
 * @since 3.4.3.5
 */
@Slf4j
@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "fun.proxy")
public class ProxyProperties {
    /**
     * 启用代理。
     */
    private Boolean enabled;
    /**
     * 非接口请求时使用的默认代理地址。
     */
    private String api;
    /**
     * 外网地址映射列表。
     */
    private List<Address> addressList;

    /**
     * 地址映射配置类。
     * <p>
     * 用于配置源地址到目标地址的映射关系，
     * 支持HTTP/HTTPS协议自动匹配。
     */
    @Data
    public static class Address {
        /**
         * 源地址（内网地址或旧地址）。
         * <p>
         * 示例：{@code "internal.cdn.com"} 或
         * {@code "http://old.example.com"}
         */
        private String origin;
        /**
         * 目标地址（外网地址或新地址）。
         * <p>
         * 示例：{@code "cdn.example.com"} 或
         * {@code "http://new.example.com"}
         */
        private String target;
        /**
         * 正则匹配模板，用于匹配源地址的URL模式。
         */
        private Pattern[] patterns;

        /**
         * URL后缀正则表达式模式。
         */
        private static final String SUFFIX =
                "([\\w/.-]*)/([\\da-zA-Z.]+)";

        /**
         * 设置源地址，并自动生成匹配模式。
         *
         * @param originAddress 源地址
         */
        public void setOrigin(final String originAddress) {
            this.origin = originAddress;
            if (originAddress != null) {
                if (CharSequenceUtil.startWithIgnoreCase(
                        originAddress, "http")) {
                    this.patterns = new Pattern[]{
                            Pattern.compile("(?i)"
                                    + Pattern.quote(originAddress)
                                    + SUFFIX)};
                } else {
                    this.patterns = new Pattern[]{
                            Pattern.compile("(?i)"
                                    + Pattern.quote("http://"
                                    + originAddress) + SUFFIX),
                            Pattern.compile("(?i)"
                                    + Pattern.quote("https://"
                                    + originAddress) + SUFFIX)
                    };
                }
            }
        }
    }

    /**
     * 判断代理功能是否启用
     *
     * @return true表示已启用，false表示未启用
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
}
