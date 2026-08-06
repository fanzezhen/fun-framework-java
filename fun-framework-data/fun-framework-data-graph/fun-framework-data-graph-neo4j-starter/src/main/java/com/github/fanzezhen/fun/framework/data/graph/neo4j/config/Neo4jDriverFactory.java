package com.github.fanzezhen.fun.framework.data.graph.neo4j.config;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.fun.framework.data.graph.base.config.FunGraphProperties;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Neo4j 驱动工厂
 * <p>
 * 把框架配置翻译为驱动配置并创建驱动实例。独立成工厂而非写在自动配置里，
 * 便于单独测试配置翻译逻辑。
 * </p>
 *
 * @since 4.1.1
 */
public final class Neo4jDriverFactory {

    private Neo4jDriverFactory() {
    }

    /**
     * 按数据源配置创建驱动
     *
     * @param datasourceConfig 数据源配置
     * @return Neo4j 驱动
     */
    public static Driver createDriver(final FunGraphProperties.Config datasourceConfig) {
        return GraphDatabase.driver(datasourceConfig.getUri(),
            buildAuthToken(datasourceConfig),
            buildDriverConfig(datasourceConfig));
    }

    /**
     * 构建认证令牌
     * <p>
     * 用户名为空时按免认证连接，避免为无认证的本地实例强行要求账号。
     * </p>
     *
     * @param datasourceConfig 数据源配置
     * @return 认证令牌
     */
    static AuthToken buildAuthToken(final FunGraphProperties.Config datasourceConfig) {
        if (CharSequenceUtil.isEmpty(datasourceConfig.getUsername())) {
            return AuthTokens.none();
        }
        return AuthTokens.basic(datasourceConfig.getUsername(),
            CharSequenceUtil.nullToEmpty(datasourceConfig.getPassword()));
    }

    /**
     * 构建驱动配置
     * <p>
     * 仅对显式配置的项做设置，未配置的项保持驱动默认值，
     * 避免框架默认值覆盖驱动的合理默认。
     * </p>
     *
     * @param datasourceConfig 数据源配置
     * @return 驱动配置
     */
    static Config buildDriverConfig(final FunGraphProperties.Config datasourceConfig) {
        final Config.ConfigBuilder builder = Config.builder();
        applyTimeout(datasourceConfig.getConnectTimeout(),
            millis -> builder.withConnectionTimeout(millis, TimeUnit.MILLISECONDS));
        applyTimeout(datasourceConfig.getConnectionAcquisitionTimeout(),
            millis -> builder.withConnectionAcquisitionTimeout(millis, TimeUnit.MILLISECONDS));
        applyTimeout(datasourceConfig.getMaxConnectionLifetime(),
            millis -> builder.withMaxConnectionLifetime(millis, TimeUnit.MILLISECONDS));
        if (datasourceConfig.getMaxConnectionPoolSize() != null) {
            builder.withMaxConnectionPoolSize(datasourceConfig.getMaxConnectionPoolSize());
        }
        if (datasourceConfig.getFetchSize() != null) {
            builder.withFetchSize(datasourceConfig.getFetchSize());
        }
        applyEncryption(datasourceConfig, builder);
        return builder.build();
    }

    /**
     * 应用超时配置
     *
     * @param timeout  超时时长
     * @param consumer 毫秒值消费者
     */
    private static void applyTimeout(final Duration timeout, final java.util.function.LongConsumer consumer) {
        if (timeout != null) {
            consumer.accept(timeout.toMillis());
        }
    }

    /**
     * 应用加密配置
     * <p>
     * URI 已带 {@code +s} / {@code +ssc} 后缀时驱动自行决定加密策略，
     * 此时再显式设置会触发驱动的配置冲突异常，故跳过。
     * </p>
     *
     * @param datasourceConfig 数据源配置
     * @param builder          驱动配置构建器
     */
    private static void applyEncryption(final FunGraphProperties.Config datasourceConfig,
                                        final Config.ConfigBuilder builder) {
        final Boolean encrypted = datasourceConfig.getEncrypted();
        if (encrypted == null || isSchemeWithSecurity(datasourceConfig.getUri())) {
            return;
        }
        if (encrypted) {
            builder.withEncryption();
            return;
        }
        builder.withoutEncryption();
    }

    /**
     * 判断连接地址是否自带安全后缀
     *
     * @param uri 连接地址
     * @return true 表示自带安全后缀
     */
    static boolean isSchemeWithSecurity(final String uri) {
        if (CharSequenceUtil.isEmpty(uri)) {
            return false;
        }
        final int schemeEnd = uri.indexOf("://");
        final String scheme = schemeEnd < 0 ? uri : uri.substring(0, schemeEnd);
        return scheme.contains("+s") || scheme.contains("+ssc");
    }
}
