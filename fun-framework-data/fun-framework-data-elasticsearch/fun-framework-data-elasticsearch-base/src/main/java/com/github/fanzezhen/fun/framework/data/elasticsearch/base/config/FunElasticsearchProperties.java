package com.github.fanzezhen.fun.framework.data.elasticsearch.base.config;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.fun.framework.core.model.constant.FunFrameworkCoreDataConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Collections;
import java.util.List;


/**
 * Elasticsearch 配置属性类
 * <p>
 * 用于绑定 Spring Boot 配置文件中的 Elasticsearch 配置项，支持多数据源配置。
 * 配置前缀为 "fun.data.elasticsearch"。
 * </p>
 * <p>配置示例：</p>
 * <pre>
 * fun:
 *   data:
 *     elasticsearch:
 *       default-datasource: default
 *       configs:
 *         - name: default
 *           uris: http://localhost:9200
 *           username: elastic
 *           password: password
 * </pre>
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "fun.data.elasticsearch")
public class FunElasticsearchProperties {

    /**
     * 默认数据源名称
     */
    private String defaultDatasource = FunFrameworkCoreDataConstant.DEFAULT_DATASOURCE_NAME;

    /**
     * 数据源配置列表
     */
    private List<Config> configs;

    /**
     * Elasticsearch 单个数据源配置
     * <p>
     * 包含连接地址、认证信息、超时配置、限流配置等。
     * </p>
     */
    @Data
    public static class Config {

        /**
         * 默认窗口大小
         */
        private static final int DEFAULT_WINDOW_SIZE = 10000;

        /**
         * 数据源名称
         */
        private String name = FunFrameworkCoreDataConstant.DEFAULT_DATASOURCE_NAME;

        /**
         * 索引前缀
         */
        private String indexPrefix;

        /**
         * Elasticsearch 连接地址列表
         * <p>
         * 默认值为 http://localhost:9200
         * </p>
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
         * Socket 超时时间
         */
        private Duration socketTimeout;

        /**
         * 心跳时间
         */
        private Duration keepAliveTime;

        /**
         * 限流每秒查询次数（QPS）
         */
        private Long qpsLimit;

        /**
         * 单次查询窗口大小限制
         */
        private Integer windowSize;

        /**
         * 设置连接地址列表
         * <p>
         * 支持两种格式：
         * <ul>
         *   <li>List<String> 类型的地址列表</li>
         *   <li>逗号分隔的字符串，如 "http://host1:9200,http://host2:9200"</li>
         * </ul>
         * </p>
         *
         * @param uris 连接地址，可以是 List 或逗号分隔的字符串
         */
        @SuppressWarnings("unchecked")
        public void setUris(final Object uris) {
            if (uris instanceof List) {
                this.uris = (List<String>) uris;
            } else if (uris instanceof String urisStr) {
                this.uris = CharSequenceUtil.split(urisStr, StrPool.COMMA);
            }
            log.info("uris set as {}", JSON.toJSONString(this.uris));
        }

        /**
         * 获取窗口大小，如果未配置则返回默认值
         *
         * @return 窗口大小，默认为 10000
         */
        public Integer getWindowSizeOrDefault() {
            return windowSize != null ? windowSize : DEFAULT_WINDOW_SIZE;
        }
    }
}
