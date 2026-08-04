package com.github.fanzezhen.fun.framework.data.elasticsearch.base.config;

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
 * {@link FunElasticsearchProperties} 配置绑定测试。
 * <p>
 * 重点覆盖 {@code configs[].uris} 的两种等价写法：逗号分隔标量与 YAML 列表。
 * 历史缺陷：为 uris 手写 {@code setUris(Object)} 宽参重载后，Lombok 不再生成
 * {@code setUris(List<String>)}，而 Spring Boot 的 JavaBeanBinder 以 setter 参数类型判定属性类型，
 * 会把该属性当成 {@code Object}——列表写法不再走集合聚合绑定，setter 永不触发，
 * 字段静默保留默认值 {@code http://localhost:9200}，运行时连本地 ES 且无任何报错。
 * </p>
 *
 * @since 4.1.0
 */
class FunElasticsearchPropertiesTest {

    /**
     * 走真实 YAML 解析链路（{@link YamlPropertySourceLoader} + Environment），贴近应用实际加载方式。
     */
    private static FunElasticsearchProperties bindYaml(final String yaml) throws IOException {
        ByteArrayResource resource = new ByteArrayResource(yaml.getBytes(StandardCharsets.UTF_8));
        List<PropertySource<?>> loaded = new YamlPropertySourceLoader().load("test", resource);
        StandardEnvironment environment = new StandardEnvironment();
        loaded.forEach(environment.getPropertySources()::addFirst);
        return new Binder(ConfigurationPropertySources.get(environment))
                .bind("fun.data.elasticsearch", Bindable.of(FunElasticsearchProperties.class))
                .orElseThrow(() -> new AssertionError("fun.data.elasticsearch 未绑定成功"));
    }

    @Test
    @DisplayName("bindUris_YAML列表写法_应逐项绑定")
    void bindUris_YamlListForm_ShouldBindAllItems() throws IOException {
        FunElasticsearchProperties properties = bindYaml("""
                fun:
                  data:
                    elasticsearch:
                      configs:
                        - name: default
                          uris:
                            - http://h1:9200
                            - http://h2:9200
                """);

        assertThat(properties.getConfigs()).hasSize(1);
        assertThat(properties.getConfigs().getFirst().getUris())
                .containsExactly("http://h1:9200", "http://h2:9200");
    }

    @Test
    @DisplayName("bindUris_逗号分隔标量_应拆分为多项")
    void bindUris_CommaSeparatedScalar_ShouldSplitIntoItems() throws IOException {
        FunElasticsearchProperties properties = bindYaml("""
                fun:
                  data:
                    elasticsearch:
                      configs:
                        - name: default
                          uris: http://h1:9200,http://h2:9200
                """);

        assertThat(properties.getConfigs().getFirst().getUris())
                .containsExactly("http://h1:9200", "http://h2:9200");
    }

    @Test
    @DisplayName("bindUris_逗号后带空格_应去除首尾空白")
    void bindUris_WhitespaceAroundComma_ShouldBeTrimmed() throws IOException {
        FunElasticsearchProperties properties = bindYaml("""
                fun:
                  data:
                    elasticsearch:
                      configs:
                        - name: default
                          uris: http://h1:9200 ,  http://h2:9200
                """);

        assertThat(properties.getConfigs().getFirst().getUris())
                .containsExactly("http://h1:9200", "http://h2:9200");
    }

    @Test
    @DisplayName("bindUris_单节点标量_不应被误拆")
    void bindUris_SingleScalar_ShouldNotBeSplit() throws IOException {
        FunElasticsearchProperties properties = bindYaml("""
                fun:
                  data:
                    elasticsearch:
                      configs:
                        - name: default
                          uris: http://h1:9200
                """);

        assertThat(properties.getConfigs().getFirst().getUris()).containsExactly("http://h1:9200");
    }

    @Test
    @DisplayName("bindUris_多数据源混用两种写法_应互不干扰")
    void bindUris_MixedFormsAcrossDatasources_ShouldNotInterfere() throws IOException {
        FunElasticsearchProperties properties = bindYaml("""
                fun:
                  data:
                    elasticsearch:
                      default-datasource: primary
                      configs:
                        - name: primary
                          uris: http://h1:9200,http://h2:9200
                        - name: secondary
                          uris:
                            - http://h3:9200
                            - http://h4:9200
                """);

        assertThat(properties.getDefaultDatasource()).isEqualTo("primary");
        assertThat(properties.getConfigs()).hasSize(2);
        assertThat(properties.getConfigs().get(0).getName()).isEqualTo("primary");
        assertThat(properties.getConfigs().get(0).getUris())
                .containsExactly("http://h1:9200", "http://h2:9200");
        assertThat(properties.getConfigs().get(1).getName()).isEqualTo("secondary");
        assertThat(properties.getConfigs().get(1).getUris())
                .containsExactly("http://h3:9200", "http://h4:9200");
    }

    @Test
    @DisplayName("bindUris_未配置_应回落默认本地地址")
    void bindUris_Absent_ShouldFallBackToLocalhost() throws IOException {
        FunElasticsearchProperties properties = bindYaml("""
                fun:
                  data:
                    elasticsearch:
                      configs:
                        - name: default
                """);

        assertThat(properties.getConfigs().getFirst().getUris())
                .containsExactly("http://localhost:9200");
    }

    @Test
    @DisplayName("bindConfig_列表写法与同级属性共存_应全部绑定")
    void bindConfig_SiblingPropertiesAlongsideUrisList_ShouldAllBind() throws IOException {
        FunElasticsearchProperties properties = bindYaml("""
                fun:
                  data:
                    elasticsearch:
                      configs:
                        - name: default
                          index-prefix: fun_
                          uris:
                            - http://h1:9200
                          username: elastic
                          password: secret
                          connect-timeout: 63s
                          window-size: 20000
                """);

        FunElasticsearchProperties.Config config = properties.getConfigs().getFirst();
        assertThat(config.getUris()).containsExactly("http://h1:9200");
        assertThat(config.getIndexPrefix()).isEqualTo("fun_");
        assertThat(config.getUsername()).isEqualTo("elastic");
        assertThat(config.getPassword()).isEqualTo("secret");
        assertThat(config.getConnectTimeout()).isEqualTo(Duration.ofSeconds(63));
        assertThat(config.getWindowSizeOrDefault()).isEqualTo(20000);
    }

    @Test
    @DisplayName("getWindowSizeOrDefault_未配置_应返回默认值")
    void getWindowSizeOrDefault_Absent_ShouldReturnDefault() throws IOException {
        FunElasticsearchProperties properties = bindYaml("""
                fun:
                  data:
                    elasticsearch:
                      configs:
                        - name: default
                """);

        assertThat(properties.getConfigs().getFirst().getWindowSizeOrDefault()).isEqualTo(10000);
    }
}
