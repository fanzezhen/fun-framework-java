package com.github.fanzezhen.fun.framework.data.elasticsearch.base.config;

import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IElasticsearchResultDeserializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * Elasticsearch 自动配置类
 * <p>
 * 负责自动配置 Elasticsearch 相关的 Bean 和组件，启用 Elasticsearch 配置属性绑定。
 * 该配置类会扫描 Elasticsearch 包下的所有组件，并初始化反序列化器列表。
 * </p>
 */
@Configuration
@EnableConfigurationProperties(FunElasticsearchProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.data.elasticsearch")
public class FunElasticsearchAutoConfiguration {

    @Resource
    private FunElasticsearchProperties funElasticsearchProperties;

    @Resource
    private List<IElasticsearchResultDeserializer> elasticsearchResultDeserializerList;

    @Resource
    private List<IResponseDeserializer> elasticsearchResponseDeserializerList;

    /**
     * 静态结果反序列化器列表
     */
    @Getter
    private static List<IElasticsearchResultDeserializer> staticResultDeserializerList = Collections.emptyList();

    /**
     * 静态响应反序列化器列表
     */
    @Getter
    private static List<IResponseDeserializer> staticResponseDeserializerList = Collections.emptyList();

    /**
     * 初始化方法，在 Bean 创建后执行
     * <p>
     * 将注入的反序列化器列表设置到静态变量中，以便在其他地方使用。
     * </p>
     */
    @PostConstruct
    public void init() {
        setStaticResultDeserializerList(elasticsearchResultDeserializerList);
        setStaticResponseDeserializerList(elasticsearchResponseDeserializerList);
        // 预留：未来可在此处理配置中的多个数据源配置
    }

    /**
     * 设置静态结果反序列化器列表
     *
     * @param resultDeserializerList 结果反序列化器列表
     */
    static void setStaticResultDeserializerList(final List<IElasticsearchResultDeserializer> resultDeserializerList) {
        staticResultDeserializerList = resultDeserializerList;
    }

    /**
     * 设置静态响应反序列化器列表
     *
     * @param responseDeserializerList 响应反序列化器列表
     */
    static void setStaticResponseDeserializerList(final List<IResponseDeserializer> responseDeserializerList) {
        FunElasticsearchAutoConfiguration.staticResponseDeserializerList = responseDeserializerList;
    }
}
