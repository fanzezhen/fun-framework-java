package com.github.fanzezhen.fun.framework.data.elasticsearch.base.config;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.fun.framework.core.data.constant.FunFrameworkCoreDataConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Collections;
import java.util.List;


/**
 * es 配置参数
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "fun.data.elasticsearch")
public class FunElasticsearchProperties {

    /**
     * 数据源名称
     */
    private String defaultDatasource = FunFrameworkCoreDataConstant.DEFAULT_DATASOURCE_NAME;

    private List<Config> configs;

    @Data
    public static class Config {

        /**
         * 数据源名称
         */
        private String name = FunFrameworkCoreDataConstant.DEFAULT_DATASOURCE_NAME;
        /**
         * 索引前缀
         */
        private String indexPrefix;

        /**
         * es连接
         */
        private List<String> uris = Collections.singletonList("http://localhost:9200");

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 连接超时时间
         */
        private Duration connectTimeout;

        /**
         * 连接请求超时时间
         */
        private Duration connectionRequestTimeout;

        /**
         * socket超时时间
         */
        private Duration socketTimeout;

        /**
         * 心跳时间
         */
        private Duration keepAliveTime;

        /**
         * 限流每秒查询次数
         */
        private Long qpsLimit;
        /**
         * 单次查询窗口大小限制
         */
        private Integer windowSize;

        @SuppressWarnings("unchecked")
        public void setUris(Object uris) {
            if (uris instanceof List) {
                this.uris = (List<String>) uris;
            } else if (uris instanceof String urisStr) {
                this.uris = CharSequenceUtil.split(urisStr, StrPool.COMMA);
            }
            log.info("uris set as {}", JSON.toJSONString(this.uris));
        }

        public Integer getWindowSizeOrDefault() {
            return windowSize != null ? windowSize : 10000;
        }
    }
}
