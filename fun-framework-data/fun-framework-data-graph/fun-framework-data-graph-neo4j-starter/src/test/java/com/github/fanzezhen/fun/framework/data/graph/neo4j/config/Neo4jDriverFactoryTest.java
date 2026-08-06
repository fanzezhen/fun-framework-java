package com.github.fanzezhen.fun.framework.data.graph.neo4j.config;

import com.github.fanzezhen.fun.framework.data.graph.base.config.FunGraphProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Neo4j 驱动工厂测试
 */
@DisplayName("Neo4j 驱动工厂")
class Neo4jDriverFactoryTest {

    @Test
    @DisplayName("认证：配置了用户名时用基础认证")
    void testBuildAuthToken_WithUsername_ShouldUseBasicAuth() {
        final FunGraphProperties.Config config = new FunGraphProperties.Config();
        config.setUsername("neo4j");
        config.setPassword("secret");

        final AuthToken authToken = Neo4jDriverFactory.buildAuthToken(config);

        assertEquals(AuthTokens.basic("neo4j", "secret"), authToken);
    }

    @Test
    @DisplayName("认证：未配置用户名时用免认证，不因缺账号而失败")
    void testBuildAuthToken_WithoutUsername_ShouldUseNoneAuth() {
        final AuthToken authToken = Neo4jDriverFactory.buildAuthToken(new FunGraphProperties.Config());

        assertEquals(AuthTokens.none(), authToken);
    }

    @Test
    @DisplayName("认证：配置用户名但密码为空时按空密码处理，不抛空指针")
    void testBuildAuthToken_NullPassword_ShouldFallbackToEmpty() {
        final FunGraphProperties.Config config = new FunGraphProperties.Config();
        config.setUsername("neo4j");

        assertEquals(AuthTokens.basic("neo4j", ""), Neo4jDriverFactory.buildAuthToken(config));
    }

    @Test
    @DisplayName("驱动配置：显式配置项被应用")
    void testBuildDriverConfig_ExplicitValues_ShouldBeApplied() {
        final FunGraphProperties.Config config = new FunGraphProperties.Config();
        config.setConnectTimeout(Duration.ofSeconds(5));
        config.setConnectionAcquisitionTimeout(Duration.ofSeconds(30));
        config.setMaxConnectionLifetime(Duration.ofMinutes(30));
        config.setMaxConnectionPoolSize(50);
        config.setFetchSize(500L);

        final Config driverConfig = Neo4jDriverFactory.buildDriverConfig(config);

        assertAll(
            () -> assertEquals(5000L, driverConfig.connectionTimeoutMillis()),
            () -> assertEquals(30_000L, driverConfig.connectionAcquisitionTimeoutMillis()),
            () -> assertEquals(Duration.ofMinutes(30).toMillis(), driverConfig.maxConnectionLifetimeMillis()),
            () -> assertEquals(50, driverConfig.maxConnectionPoolSize()),
            () -> assertEquals(500L, driverConfig.fetchSize())
        );
    }

    @Test
    @DisplayName("驱动配置：未配置项保持驱动默认值，框架不越权覆盖")
    void testBuildDriverConfig_Defaults_ShouldKeepDriverDefaults() {
        final Config defaultConfig = Config.defaultConfig();

        final Config driverConfig = Neo4jDriverFactory.buildDriverConfig(new FunGraphProperties.Config());

        assertAll(
            () -> assertNotNull(driverConfig),
            () -> assertEquals(defaultConfig.maxConnectionPoolSize(), driverConfig.maxConnectionPoolSize()),
            () -> assertEquals(defaultConfig.fetchSize(), driverConfig.fetchSize()),
            () -> assertEquals(defaultConfig.connectionTimeoutMillis(), driverConfig.connectionTimeoutMillis())
        );
    }

    @Test
    @DisplayName("加密：显式关闭加密时生效")
    void testBuildDriverConfig_EncryptionDisabled_ShouldApply() {
        final FunGraphProperties.Config config = new FunGraphProperties.Config();
        config.setUri("bolt://localhost:7687");
        config.setEncrypted(false);

        assertEquals(false, Neo4jDriverFactory.buildDriverConfig(config).encrypted());
    }

    @Test
    @DisplayName("加密：URI 自带安全后缀时跳过显式设置，避免驱动配置冲突")
    void testBuildDriverConfig_SecureScheme_ShouldSkipExplicitEncryption() {
        final FunGraphProperties.Config config = new FunGraphProperties.Config();
        config.setUri("neo4j+s://cluster:7687");
        config.setEncrypted(false);

        // 未显式调用 withoutEncryption，故保持驱动默认值
        assertEquals(Config.defaultConfig().encrypted(), Neo4jDriverFactory.buildDriverConfig(config).encrypted());
    }

    @ParameterizedTest
    @DisplayName("识别 URI 是否自带安全后缀")
    @CsvSource({
        "bolt://localhost:7687, false",
        "neo4j://localhost:7687, false",
        "bolt+s://host:7687, true",
        "neo4j+s://host:7687, true",
        "bolt+ssc://host:7687, true",
        "neo4j+ssc://host:7687, true",
        ", false",
    })
    void testIsSchemeWithSecurity(final String uri, final boolean expected) {
        assertEquals(expected, Neo4jDriverFactory.isSchemeWithSecurity(uri));
    }
}
