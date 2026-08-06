package com.github.fanzezhen.fun.framework.data.graph.base.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link FunGraphProperties} 配置绑定测试
 * <p>
 * 走真实 YAML 解析链路，验证多数据源与各连接参数能正确绑定。
 * </p>
 */
class FunGraphPropertiesTest {

    /**
     * 按真实加载方式绑定 YAML 配置
     *
     * @param yaml YAML 文本
     * @return 绑定后的配置对象
     * @throws IOException YAML 解析失败时抛出
     */
    private static FunGraphProperties bindYaml(final String yaml) throws IOException {
        final ByteArrayResource resource = new ByteArrayResource(yaml.getBytes(StandardCharsets.UTF_8));
        final List<PropertySource<?>> loadedList = new YamlPropertySourceLoader().load("test", resource);
        final StandardEnvironment environment = new StandardEnvironment();
        loadedList.forEach(environment.getPropertySources()::addFirst);
        return new Binder(ConfigurationPropertySources.get(environment))
            .bind("fun.data.graph", Bindable.of(FunGraphProperties.class))
            .orElseThrow(() -> new AssertionError("fun.data.graph 未绑定成功"));
    }

    @Test
    @DisplayName("绑定多数据源配置_应逐项绑定名称与连接信息")
    void bindConfigs_MultipleDatasources_ShouldBindEachItem() throws IOException {
        final FunGraphProperties properties = bindYaml("""
            fun:
              data:
                graph:
                  default-datasource: primary
                  configs:
                    - name: primary
                      uri: bolt://h1:7687
                      username: neo4j
                      password: secret
                      database: graph1
                    - name: secondary
                      uri: neo4j+s://h2:7687
            """);

        assertThat(properties.getDefaultDatasource()).isEqualTo("primary");
        assertThat(properties.getConfigs()).hasSize(2);
        assertThat(properties.getConfigs().getFirst().getName()).isEqualTo("primary");
        assertThat(properties.getConfigs().getFirst().getUri()).isEqualTo("bolt://h1:7687");
        assertThat(properties.getConfigs().getFirst().getUsername()).isEqualTo("neo4j");
        assertThat(properties.getConfigs().getFirst().getPassword()).isEqualTo("secret");
        assertThat(properties.getConfigs().getFirst().getDatabase()).isEqualTo("graph1");
        assertThat(properties.getConfigs().get(1).getUri()).isEqualTo("neo4j+s://h2:7687");
    }

    @Test
    @DisplayName("绑定连接参数_时长与数值类型应正确转换")
    void bindConnectionParams_ShouldConvertDurationAndNumbers() throws IOException {
        final FunGraphProperties properties = bindYaml("""
            fun:
              data:
                graph:
                  configs:
                    - name: default
                      connect-timeout: 5s
                      connection-acquisition-timeout: 30s
                      max-connection-lifetime: 30m
                      max-connection-pool-size: 50
                      fetch-size: 500
                      encrypted: false
            """);

        final FunGraphProperties.Config config = properties.getConfigs().getFirst();
        assertThat(config.getConnectTimeout()).isEqualTo(Duration.ofSeconds(5));
        assertThat(config.getConnectionAcquisitionTimeout()).isEqualTo(Duration.ofSeconds(30));
        assertThat(config.getMaxConnectionLifetime()).isEqualTo(Duration.ofMinutes(30));
        assertThat(config.getMaxConnectionPoolSize()).isEqualTo(50);
        assertThat(config.getFetchSize()).isEqualTo(500L);
        assertThat(config.getEncrypted()).isFalse();
    }

    @Test
    @DisplayName("绑定实体扫描包_列表写法应逐项绑定")
    void bindEntityPackages_ShouldBindAllItems() throws IOException {
        final FunGraphProperties properties = bindYaml("""
            fun:
              data:
                graph:
                  entity-packages:
                    - com.example.graph.node
                    - com.example.graph.relationship
            """);

        assertThat(properties.getEntityPackages())
            .containsExactly("com.example.graph.node", "com.example.graph.relationship");
    }

    @Test
    @DisplayName("未配置连接地址_应使用本地默认地址")
    void bindUri_Absent_ShouldFallbackToLocalDefault() throws IOException {
        final FunGraphProperties properties = bindYaml("""
            fun:
              data:
                graph:
                  configs:
                    - name: default
            """);

        assertThat(properties.getConfigs().getFirst().getUri()).isEqualTo("bolt://localhost:7687");
    }

    @Test
    @DisplayName("未配置加密开关_应保持为空以沿用驱动默认行为")
    void bindEncrypted_Absent_ShouldStayNull() throws IOException {
        final FunGraphProperties properties = bindYaml("""
            fun:
              data:
                graph:
                  configs:
                    - name: default
            """);

        assertThat(properties.getConfigs().getFirst().getEncrypted()).isNull();
    }
}
