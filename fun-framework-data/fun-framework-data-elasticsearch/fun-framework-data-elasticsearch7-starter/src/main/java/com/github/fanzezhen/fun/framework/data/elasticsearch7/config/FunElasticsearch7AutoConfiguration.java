package com.github.fanzezhen.fun.framework.data.elasticsearch7.config;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.github.fanzezhen.fun.framework.core.model.common.IColumnDeserializer;
import com.github.fanzezhen.fun.framework.core.log.support.FunLogHelper;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.config.FunElasticsearchProperties;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IElasticsearchResultDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.deserializer.IResponseDeserializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.DefaultDocumentSerializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.serializer.IDocumentSerializer;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.BaseMultiDatasourceElasticsearchTemplate;
import com.github.fanzezhen.fun.framework.data.elasticsearch7.impl.template.ElasticsearchMultiDataSourceTemplateImpl;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Elasticsearch 7.x 自动配置类
 *
 * <p>负责初始化 Elasticsearch 7.x 客户端、模板实例和相关 Bean。
 * 提供默认的文档序列化器和多数据源模板实现。
 */
@Configuration
@EnableConfigurationProperties(FunElasticsearchProperties.class)
@ComponentScan("com.github.fanzezhen.fun.framework.data.elasticsearch7")
public class FunElasticsearch7AutoConfiguration {

    /**
     * Elasticsearch 配置属性
     */
    @Resource
    private FunElasticsearchProperties funElasticsearchProperties;

    /**
     * 日志辅助类
     */
    @Resource
    private FunLogHelper funLogHelper;

    /**
     * 列反序列化器列表
     */
    @Resource
    private List<IColumnDeserializer> columnDeserializerList;

    /**
     * Elasticsearch 结果反序列化器列表
     */
    @Resource
    private List<IElasticsearchResultDeserializer> elasticsearchResultDeserializerList;

    /**
     * Jackson JSON 映射器
     */
    @Getter
    static JacksonJsonpMapper jacksonJsonpMapper;

    /**
     * 构造函数，初始化 Jackson JSON 映射器
     *
     * @param jacksonJsonpMapper Jackson JSON 映射器实例，可为空
     */
    public FunElasticsearch7AutoConfiguration(@Autowired(required = false) final JacksonJsonpMapper jacksonJsonpMapper) {
        setJacksonJsonpMapper(jacksonJsonpMapper == null ? new JacksonJsonpMapper() : jacksonJsonpMapper);
    }

    /**
     * 创建默认文档序列化器 Bean
     *
     * @return 默认文档序列化器实例
     */
    @Bean
    @ConditionalOnMissingBean(BaseMultiDatasourceElasticsearchTemplate.class)
    public IDocumentSerializer funDefaultDocumentSerializer() {
        return new DefaultDocumentSerializer();
    }

    /**
     * 创建多数据源 Elasticsearch 模板 Bean
     *
     * @param documentSerializerList 文档序列化器列表
     * @param responseDeserializerList 响应反序列化器列表
     * @return 多数据源 Elasticsearch 模板实例
     */
    @Bean
    @ConditionalOnMissingBean(BaseMultiDatasourceElasticsearchTemplate.class)
    public BaseMultiDatasourceElasticsearchTemplate funMultiDatasourceElasticsearchTemplate(
        final List<IDocumentSerializer> documentSerializerList,
        final List<IResponseDeserializer> responseDeserializerList
    ) {
        return new ElasticsearchMultiDataSourceTemplateImpl(
            funElasticsearchProperties,
            funLogHelper,
            documentSerializerList,
            responseDeserializerList);
    }

    /**
     * 设置 Jackson JSON 映射器
     *
     * @param jacksonJsonpMapper Jackson JSON 映射器实例
     */
    static void setJacksonJsonpMapper(final JacksonJsonpMapper jacksonJsonpMapper) {
        FunElasticsearch7AutoConfiguration.jacksonJsonpMapper = jacksonJsonpMapper;
    }
}
