package com.github.fanzezhen.fun.framework.data.elasticsearch.base.config;

import com.github.fanzezhen.fun.framework.core.model.constant.FunFrameworkCoreDataConstant;
import lombok.Data;
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
 *           uris: http://h1:9200,http://h2:9200
 *           username: elastic
 *           password: password
 *         - name: secondary
 *           uris:
 *             - http://h3:9200
 *             - http://h4:9200
 * </pre>
 * <p>{@code uris} 的标量与列表两种写法等价，详见 {@link Config#getUris()} 字段说明。</p>
 */
@Data
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
         * Elasticsearch 连接地址列表，默认 http://localhost:9200
         * <p>
         * 支持两种等价写法，均由 Spring Boot 原生绑定完成，逐项自动去除首尾空白：
         * <pre>
         * uris: http://h1:9200,http://h2:9200   # 标量，逗号分隔
         * uris:                                 # YAML 列表
         *   - http://h1:9200
         *   - http://h2:9200
         * </pre>
         * 约束：禁止为该字段手写 {@code setUris(Object)} 之类的宽参重载。Lombok 遇到同名同参数个数的方法
         * 就不再生成 {@code setUris(List<String>)}，而 Spring Boot 的 JavaBeanBinder 以 setter 参数类型
         * 判定属性类型，会把本属性当成 {@code Object}：列表写法不再走集合聚合绑定，setter 永不触发，
         * 字段静默保留默认值，运行时连本地 ES 且无任何报错。
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
         * 获取窗口大小，如果未配置则返回默认值
         *
         * @return 窗口大小，默认为 10000
         */
        public Integer getWindowSizeOrDefault() {
            return windowSize != null ? windowSize : DEFAULT_WINDOW_SIZE;
        }
    }
}
