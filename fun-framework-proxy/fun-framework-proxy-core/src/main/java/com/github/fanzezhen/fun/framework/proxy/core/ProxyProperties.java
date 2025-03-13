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
 * @author fanzezhen
 * @since 3.4.3.5
 */
@Slf4j
@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "fun.proxy")
public class ProxyProperties {
    /**
     * 启用代理
     */
    private Boolean enabled;
    /**
     * 非接口请求时使用的默认代理地址
     */
    private String api;
    /**
     * 外网地址映射列表
     */
    private List<Address> addressList;

    @Data
    public static class Address {
        /**
         * 源地址
         */
        private String origin;
        /**
         * 新地址
         */
        private String target;
        /**
         * 模板
         */
        private Pattern[] patterns;

        static String suffix = "([\\w/.-]*)/([\\da-zA-Z.]+)";

        public void setOrigin(String origin) {
            this.origin = origin;
            if (origin != null) {
                if (CharSequenceUtil.startWithIgnoreCase(origin, "http")) {
                    this.patterns = new Pattern[]{Pattern.compile("(?i)" + Pattern.quote(origin) + suffix)}; // (?i) 表示忽略大小写
                } else {
                    this.patterns = new Pattern[]{
                            Pattern.compile("(?i)" + Pattern.quote("http://" + origin) + suffix),
                            Pattern.compile("(?i)" + Pattern.quote("https://" + origin) + suffix)
                    };
                }
            }
        }
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
}
