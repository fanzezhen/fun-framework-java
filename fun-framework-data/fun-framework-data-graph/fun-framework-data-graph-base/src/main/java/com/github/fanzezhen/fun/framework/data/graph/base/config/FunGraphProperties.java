package com.github.fanzezhen.fun.framework.data.graph.base.config;

import com.github.fanzezhen.fun.framework.core.model.constant.FunFrameworkCoreDataConstant;
import com.github.fanzezhen.fun.framework.core.model.template.IDatasourceConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

/**
 * 图数据库配置属性类
 * <p>
 * 用于绑定 Spring Boot 配置文件中的图数据库配置项，支持多数据源配置。
 * 配置前缀为 "fun.data.graph"。
 * </p>
 * <p>配置示例：</p>
 * <pre>
 * fun:
 *   data:
 *     graph:
 *       default-datasource: default
 *       configs:
 *         - name: default
 *           uri: bolt://localhost:7687
 *           username: neo4j
 *           password: password
 *           database: neo4j
 *         - name: secondary
 *           uri: neo4j://cluster-host:7687
 * </pre>
 *
 * @since 4.1.1
 */
@Data
@ConfigurationProperties(prefix = "fun.data.graph")
public class FunGraphProperties {

    /**
     * 默认数据源名称
     */
    private String defaultDatasource = FunFrameworkCoreDataConstant.DEFAULT_DATASOURCE_NAME;

    /**
     * 图实体扫描包
     * <p>
     * 启动时扫描这些包下标注 {@code @GraphNode} / {@code @GraphRelationship} 的类，
     * 登记标签到实体类的反查关系。为空时取启动类所在包。
     * </p>
     * <p>
     * 不复用 JPA 的 {@code @EntityScan}：图模块与 JPA 无关，
     * 借用其语义会把 {@code spring-boot-persistence} 拖进依赖。
     * </p>
     */
    private List<String> entityPackages;

    /**
     * 数据源配置列表
     */
    private List<Config> configs;

    /**
     * 图数据库单个数据源配置
     * <p>
     * 包含连接地址、认证信息、连接池与超时配置。
     * </p>
     */
    @Data
    public static class Config implements IDatasourceConfig {

        /**
         * 默认连接地址
         */
        private static final String DEFAULT_URI = "bolt://localhost:7687";

        /**
         * 数据源名称
         */
        private String name = FunFrameworkCoreDataConstant.DEFAULT_DATASOURCE_NAME;

        /**
         * 连接地址，默认 bolt://localhost:7687
         * <p>
         * 支持 {@code bolt://}、{@code bolt+s://}、{@code neo4j://}、{@code neo4j+s://} 等协议，
         * 具体取值范围由所用图数据库驱动决定。
         * </p>
         */
        private String uri = DEFAULT_URI;

        /**
         * 用户名
         * <p>
         * 与密码同时为空时以免认证方式连接。
         * </p>
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 数据库名
         * <p>
         * 为空时使用图库的默认数据库。
         * </p>
         */
        private String database;

        /**
         * 连接超时时间
         */
        private Duration connectTimeout;

        /**
         * 连接获取超时时间
         */
        private Duration connectionAcquisitionTimeout;

        /**
         * 连接最大存活时间
         */
        private Duration maxConnectionLifetime;

        /**
         * 连接池最大连接数
         */
        private Integer maxConnectionPoolSize;

        /**
         * 单次拉取记录数
         * <p>
         * 对应驱动的 fetchSize，控制流式拉取批量大小。
         * </p>
         */
        private Long fetchSize;

        /**
         * 是否启用传输加密
         * <p>
         * 为 null 时不干预驱动默认行为；URI 已带 {@code +s}/{@code +ssc} 后缀时不可再显式设置，
         * 否则驱动会抛出配置冲突异常。
         * </p>
         */
        private Boolean encrypted;
    }
}
